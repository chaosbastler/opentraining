#!/bin/sh

git submodule init
git submodule update
android update project -p ActionBarSherlock/library --target "android-17"
android update project -p app --subprojects --target "android-17"
cd test
android update test-project -m ../app -p .
