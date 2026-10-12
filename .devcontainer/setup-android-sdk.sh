#!/usr/bin/env bash
set -euo pipefail

sudo apt-get update
sudo apt-get install -y wget unzip

ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-/usr/local/android-sdk}"
SDKMANAGER="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"

if [ ! -x "$SDKMANAGER" ]; then
    archive="$(mktemp -p /tmp android-commandline-tools.XXXXXX.zip)"
    trap 'rm -f "$archive"' EXIT
    wget --quiet \
        https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip \
        --output-document="$archive"
    sudo mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"
    sudo unzip -q -o "$archive" -d "$ANDROID_SDK_ROOT/cmdline-tools"
    sudo rm -rf "$ANDROID_SDK_ROOT/cmdline-tools/latest"
    sudo mv "$ANDROID_SDK_ROOT/cmdline-tools/cmdline-tools" "$ANDROID_SDK_ROOT/cmdline-tools/latest"
fi

sudo mkdir -p "$ANDROID_SDK_ROOT"
sudo chown -R "$(id -u):$(id -g)" "$ANDROID_SDK_ROOT"
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export ANDROID_SDK_ROOT
export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools"

sdkmanager="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"
"$sdkmanager" --licenses < <(yes)
"$sdkmanager" --install \
    "platform-tools" \
    "platforms;android-34" \
    "build-tools;34.0.0"
