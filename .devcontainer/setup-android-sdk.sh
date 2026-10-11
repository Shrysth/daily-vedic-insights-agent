#!/bin/bash
set -e
sudo apt-get update && sudo apt-get install -y wget unzip
export ANDROID_SDK_ROOT="/usr/local/android-sdk"
sudo mkdir -p $ANDROID_SDK_ROOT/cmdline-tools
cd /tmp
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
sudo unzip commandlinetools-linux-11076708_latest.zip -d $ANDROID_SDK_ROOT/cmdline-tools
sudo mv $ANDROID_SDK_ROOT/cmdline-tools/cmdline-tools $ANDROID_SDK_ROOT/cmdline-tools/latest
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools
yes | sdkmanager --licenses || true
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
