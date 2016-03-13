Open Training is looking for a new maintainer
=============
[![No Maintenance Intended](http://unmaintained.tech/badge.svg)](http://unmaintained.tech/)

I don't have enough time to work regularly on this project anymore. If anyone wants to continue this project please contact me.


Open Training
=============

Open Training is an Android app for planning your fitness training.


Where can I download the app?
-----------------------------
Google Play Store: https://play.google.com/store/apps/details?id=de.skubware.opentraining

F-Droid.org (catalogue of FOSS): https://f-droid.org/repository/browse/?fdid=de.skubware.opentraining

You can also use this QR-Codes:

<a href='http://www.qrcode-generator.de' border='0' style='cursor:default'><img src='https://chart.googleapis.com/chart?cht=qr&chl=https://play.google.com/store/apps/details?id=de.skubware.opentraining&chs=150x150&choe=UTF-8&chld=L|2' alt='qrcodes'></a>
<a href='http://www.qrcode-generator.de' border='0' style='cursor:default'><img src='https://chart.googleapis.com/chart?cht=qr&chl=https://f-droid.org/repository/browse/?fdid=de.skubware.opentraining&chs=150x150&choe=UTF-8&chld=L|2' alt='hier qr code erstellen'></a>

Programming language
--------------------
Java (and some shell scripts for development)

License
-------
GPL 3
Exercises are CC licensed(currently all CC-BY-SA)

Translations
------------
English, German. More wanted! Help here: http://crowdin.net/project/opentraining


Building Instruction
====================

Requirements
------------

  * Git
  * Android-SDK v19
  * Android Support Repository

Building with Eclipse (TODO: add instructions for android studio)
--------------------- 

#### 1. Clone the repository

    $ git clone git://github.com/chaosbastler/opentraining.git

#### 2. Change Eclipse workspace

#### 3. Import 'app'
    * File -> Import -> Existing Projects into Workspace
#### 4. Import 'test'
    * File -> Import -> Existing Projects into Workspace

#### 5. Import support library
    * Instructions: http://developer.android.com/tools/support-library/setup.html#add-library; 'Adding libraries with resources', step 1 to 4 should be enough

#### 6. Disable Lint für support library
    *Click right on project 'android-support-v7-appcompat' -> Properties -> Android Lint Preferences -> Ignore all


Building with gradle
--------------------

#### 1. Connect your phone with USB

#### 2. Build & Install

    $ gradle installDebug
