#!/bin/bash

echo "准备打包 assembleProductRelease release(线上包)"

./gradlew clean --debug --stacktrace
./gradlew assembleRelease

echo "打包结束"