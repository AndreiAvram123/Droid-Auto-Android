#!/usr/bin/env bash
# fail if any commands fails
set -e
# debug log
set -x

# Add the Cloud SDK distribution URI as a package source
echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] http://packages.cloud.google.com/apt cloud-sdk main" | sudo tee -a /etc/apt/sources.list.d/google-cloud-sdk.list

# Import the Google Cloud Platform public key
curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key --keyring /usr/share/keyrings/cloud.google.gpg add -

# Update the package list and install the Cloud SDK
sudo apt-get update && sudo apt-get install  --yes --force-yes google-cloud-sdk

gcloud auth activate-service-account --key-file=../../Desktop/car-rental-android-1fdfc-65c371b85820.json --project car-rental-android-1fdfc
