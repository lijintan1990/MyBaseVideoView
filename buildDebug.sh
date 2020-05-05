#!/bin/bash

echo "准备打包 debug 开发包"

./gradlew clean --debug --stacktrace
./gradlew assembleDebug

echo "打包结束"