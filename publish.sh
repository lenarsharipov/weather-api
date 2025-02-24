#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: ./publish.sh <version>"
  exit 1
fi

VERSION=$1

mvn versions:set -DnewVersion=$VERSION

if [ $? -ne 0 ]; then
  echo "Failed to update version to $VERSION."
  exit 1
fi

mvn clean deploy

if [ $? -eq 0 ]; then
  echo "SDK version $VERSION published successfully!"
  rm -f pom.xml.versionsBackup
else
  echo "Failed to publish SDK version $VERSION."
  mv pom.xml.versionsBackup pom.xml
  exit 1
fi