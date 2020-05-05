#!/bin/bash

echo "准备打包 assembleDevPreview preview(给测试/内测用) "

./gradlew clean --stacktrace #--debug
./gradlew assemblePreview

echo "打包结束"