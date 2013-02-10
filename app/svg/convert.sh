#!/bin/sh

if [ -d ../res/drawable-xhdpi ]
then
  echo "Verzeichnis 'drawable-xhdpi' existiert bereits"
else
  mkdir ../res/drawable-xhdpi
  echo "Verzeichnis 'drawable-xhdpi' erstellt"
fi 
echo ""

if [ -d ../res/drawable-hdpi ]
then
  echo "Verzeichnis 'drawable-hdpi' existiert bereits"
else
  mkdir ../res/drawable-hdpi
  echo "Verzeichnis 'drawable-hdpi' erstellt"
fi 
echo ""

if [ -d ../res/drawable-mdpi ]
then
  echo "Verzeichnis 'drawable-mdpi' existiert bereits"
else
  mkdir ../res/drawable-mdpi
  echo "Verzeichnis 'drawable-mdpi' erstellt"
fi 
echo ""

if [ -d ../res/drawable-ldpi ]
then
  echo "Verzeichnis 'drawable-ldpi' existiert bereits"
else
  mkdir ../res/drawable-ldpi
  echo "Verzeichnis 'drawable-ldpi' erstellt"
fi 
echo ""

echo "Starting image conversion."


for f in *.svg 
do	
	NEWFILE=`basename $f .svg`.png
	echo -e "\033[46;1;31m STARTING $NEWFILE \033[0m"
		
	
	if [ -f ../res/drawable-ldpi/$NEWFILE ]
		then
		echo ../res/drawable-ldpi/$NEWFILE existiert bereits
	else
			inkscape --without-gui --export-png=../res/drawable-ldpi/$NEWFILE --export-dpi=120 $f
	echo finished ldpi
	echo ""
	fi 
		

	if [ -f ../res/drawable-mdpi/$NEWFILE ]
		then
		echo ../res/drawable-mdpi/$NEWFILE existiert bereits
	else
		inkscape --without-gui --export-png=../res/drawable-mdpi/$NEWFILE --export-dpi=160 $f
		echo finished mdpi
		echo ""
	fi 


	if [ -f ../res/drawable-hdpi/$NEWFILE ]
		then
		echo ../res/drawable-hdpi/$NEWFILE existiert bereits
	else
		inkscape --without-gui --export-png=../res/drawable-hdpi/$NEWFILE --export-dpi=240 $f
		echo finished hdpi
		echo ""
	fi 


	if [ -f ../res/drawable-xhdpi/$NEWFILE ]
		then
		echo ../res/drawable-xhdpi/$NEWFILE existiert bereits
	else
			inkscape --without-gui --export-png=../res/drawable-xhdpi/$NEWFILE --export-dpi=320 $f
	echo finished xhdpi
	echo ""
	fi 

	
	echo -e "\033[46;1;31m FINISHED $NEWFILE \033[0m"
	echo ""
done

echo "all images finished"
