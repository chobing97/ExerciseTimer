#!/bin/bash

pushd $(dirname $0) > /dev/null

mkdir -p bin
mkdir -p assets
mkdir -p gen

popd > /dev/null

echo "Setting environment complete!"
