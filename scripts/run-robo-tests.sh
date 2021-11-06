#!/usr/bin/env bash
# fail if any commands fails
set -e
# debug log
set -x

gcloud firebase test android run \
  --type robo \
  --app  "$BITRISE_APK_PATH" \
  --device model=Pixel3,version=30,orientation=portrait \
  --timeout 90s