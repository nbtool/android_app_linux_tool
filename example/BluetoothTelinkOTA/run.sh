#!/bin/bash

set -e
ANDROID_BUILD_TOOLS_VERSION=28.0.1
ANDROID_PLATFORM_VERSION=28
GRADLE_VERSION=2.14.1

PROJECT_NAME=BluetoothTelinkOTA
MODULE_NAME=ota


PROJECT_ROOT=../..
TOOL_PATH=$PROJECT_ROOT/tool
ANDROID_SDK_PATH=$TOOL_PATH/android-sdk
ANDROID_BUILD_TOOLS_PATH=$ANDROID_SDK_PATH/build-tools/$ANDROID_BUILD_TOOLS_VERSION
ANDROID_PLATFORM_PATH=$ANDROID_SDK_PATH/platforms/android-$ANDROID_PLATFORM_VERSION
ANDROID_TOOLS_PATH=$ANDROID_SDK_PATH/tools

GRADLE_PATH=$TOOL_PATH/gradle/gradle-$GRADLE_VERSION
SDKMANAGER=$ANDROID_TOOLS_PATH/bin/sdkmanager

# ref:https://developer.android.com/studio/build/building-cmdline
APK_PATH=$PROJECT_ROOT/example/$PROJECT_NAME/$MODULE_NAME/build/outputs/apk

function build(){
    export GRADLE_HOME=$GRADLE_PATH
    export PATH=$PATH:$GRADLE_HOME/bin
    gradle clean
    gradle assembleDebug
}

function clean(){
    echo "Cleaning..."
    rm -rf build
    rm -rf $PROJECT_ROOT/example/$PROJECT_NAME/$MODULE_NAME/build
}

function program(){
	echo "Launching..."
	adb install -r $APK_PATH/*.apk
    adb shell am start -n  com.telink.lt/.ui.AdvDeviceListActivity
}

function tool(){
    #export JAVA_OPTS='-XX:+IgnoreUnrecognizedVMOptions --add-modules java.se.ee'

    if [ ! -d $ANDROID_SDK_PATH ]; then 
        #download tool
        echo "> download tool...."
        wget https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip
        mkdir -p $ANDROID_SDK_PATH
        unzip sdk-tools-linux-3859397.zip -d $ANDROID_SDK_PATH
        rm -rf sdk-tools-linux-3859397.zip
        sudo chmod 777 -R $ANDROID_SDK_PATH
    fi

    #install sdk build-tools platform
    echo "> install sdk build-tools platform...."
    echo $SDKMANAGER
    $SDKMANAGER "platform-tools" "platforms;android-$ANDROID_PLATFORM_VERSION"
    $SDKMANAGER "platform-tools" "build-tools;$ANDROID_BUILD_TOOLS_VERSION" 
    $SDKMANAGER --list

    if [ ! -d $GRADLE_PATH ];then
        #download gredle
        echo "> download gredle..."
        wget https://downloads.gradle-dn.com/distributions/gradle-$GRADLE_VERSION-bin.zip 
        unzip gradle-$GRADLE_VERSION-bin.zip -d $TOOL_PATH/gradle
        rm -rf gradle-$GRADLE_VERSION-bin.zip 
    fi
}

if [ "$1" == "all" ]; then
    clean
    build
    program
elif [ "$1" == "clean" ]; then
    clean
elif [ "$1" == "build" ]; then
    build
elif [ "$1" == "program" ]; then
    program
elif [ "$1" == "tool" ]; then
    tool
else
    echo "error"
fi
