Open Training [![Build Status](https://travis-ci.org/chaosbastler/opentraining.png)](https://travis-ci.org/chaosbastler/opentraining)
=============

Open Training is an Android app for planning your fitness training.

More information:
http://skubware.de/opentraining/

There will also be a web application for editing the exercise database.
The code of the website will be released under the AGPL. Here is a preview of the website: 

http://www.trainingdb.de/

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
  * Android-SDK v17

Building with ant
-----------------

### 1. Clone the respository

    $ git clone git://github.com/chaosbastler/opentraining.git

#### 2. Update project properties

    NOTE: You must have git, ant/bin, android/tools in your enviroment PATH

    On *nix based systems:

    $ ./setup_env.sh

    On windows open the `setup_env.bat`

#### 4. Build the project
    $ ant debug

#### 5. Install it
    $ ant installd

Building with Eclipse
--------------------- 

#### 1. Clone the repository

    $ git clone git://github.com/chaosbastler/opentraining.git

#### 2. Checkout dependencies

    $ git submodule init

    $ git submodule update

#### 3. Change Eclipse workspace

#### 4. Import 'app'
    * File -> Import -> Existing Projects into Workspace

#### 5. Import ActionBarSherlock
    *File -> New -> Other -> Android Project from Existing Code
    *Root Directory: choose ActionBarSherlock/library

#### 6. Disable Lint für ActionBarSherlock
    *Click right on project 'library' -> Properties -> Android Lint Preferences -> Ignore all


Building with maven
---------------------

#### 1. Connect your phone with USB

#### 2. Build & Install

    $  cd app && mvn clean install android:deploy android:run -Dandroid.device=usb -DskipTests
