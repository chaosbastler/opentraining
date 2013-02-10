Open Training
=============

Open Training is an Android app for planning your fitness training.

More information:
http://skubware.de/opentraining/

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
English, German.


Building Open Training
======================

Requirements
------------
  * Git
  * Eclipse with Android-SDK
  
Building with Eclipse
--------------------- 

#### 1. Clone repository

#### 2. Add submodule
    * git submodule init
    * git submodule update

#### 3. Change Eclipse workspace

#### 4. Import 'app'
    * File -> Import -> Existing Projects into Workspace

#### 5. Import ActionBarSherlock
    *File -> New -> Other -> Android Project from Existing Code
    *Root Directory: choose ActionBarSherlock/library

#### 6. Disable Lint für ActionBarSherlock
    *Click right on project 'library' -> Properties -> Android Lint Preferences -> Ignore all
