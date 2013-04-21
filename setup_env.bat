git submodule init
git submodule update
android.bat update project -p ActionBarSherlock/library --target "android-17"
android.bat update project -p app --subprojects --target "android-17"
cd test
android.bat update test-project -m ../app -p .
