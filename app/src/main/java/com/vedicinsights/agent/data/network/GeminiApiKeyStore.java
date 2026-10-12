package com.vedicinsights.agent.data.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public final class GeminiApiKeyStore {
    private static final String PREFERENCES = "encrypted_ai_settings";
    private static final String KEY_NAME = "gemini_api_key";
    private static final String KEY_ALIAS = "daily_vedic_gemini_key";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final int GCM_TAG_BITS = 128;

    private GeminiApiKeyStore() { }

    public static void save(Context context, String apiKey) throws GeneralSecurityException, IOException {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Gemini API key cannot be empty.");
        }
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey());
        byte[] plaintext = apiKey.trim().getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = cipher.doFinal(plaintext);
        ByteBuffer stored = ByteBuffer.allocate(1 + cipher.getIV().length + encrypted.length);
        stored.put((byte) cipher.getIV().length);
        stored.put(cipher.getIV());
        stored.put(encrypted);
        String encoded = Base64.encodeToString(stored.array(), Base64.NO_WRAP);
        if (!preferences(context).edit().putString(KEY_NAME, encoded).commit()) {
            throw new IOException("Could not save the encrypted Gemini API key.");
        }
    }

    public static String read(Context context) throws GeneralSecurityException, IOException {
        String encoded = preferences(context).getString(KEY_NAME, null);
        if (encoded == null) {
            return null;
        }
        byte[] storedBytes = Base64.decode(encoded, Base64.NO_WRAP);
        ByteBuffer stored = ByteBuffer.wrap(storedBytes);
        int ivLength = Byte.toUnsignedInt(stored.get());
        if (ivLength == 0 || ivLength > stored.remaining()) {
            throw new GeneralSecurityException("Stored Gemini API key data is invalid.");
        }
        byte[] iv = new byte[ivLength];
        stored.get(iv);
        byte[] encrypted = new byte[stored.remaining()];
        stored.get(encrypted);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), new GCMParameterSpec(GCM_TAG_BITS, iv));
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }

    public static void delete(Context context) {
        preferences(context).edit().remove(KEY_NAME).apply();
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    private static SecretKey getOrCreateKey() throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        if (keyStore.containsAlias(KEY_ALIAS)) {
            return (SecretKey) keyStore.getKey(KEY_ALIAS, null);
        }

        KeyGenerator generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        generator.init(new KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)
            .build());
        return generator.generateKey();
    }
}
