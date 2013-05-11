#!/bin/sh

echo "Starting image icon conversion."

counter=0

for f in *.svg 
do	
	NEWFILE=`basename $f .svg`_icon.png
	
	echo -e "\033[46;1;31m STARTING $NEWFILE \033[0m"

	inkscape --without-gui --export-png=../../assets/opentraining-exercises/$NEWFILE --export-height=80  $f
	echo finished
	
	echo ""

	
	width=$(identify -format "%w" ../../assets/opentraining-exercises/$NEWFILE)

	if [ $width  -gt 1200 ]; then
		echo "Exporting $NEWFILE again with smaller widht"
		inkscape --without-gui --export-png=../../assets/opentraining-exercises/$NEWFILE --export-width=80  $f
	fi
	
	echo ""

	counter=`expr $counter + 1`
		
	echo -e "\033[46;1;31m FINISHED $NEWFILE \033[0m"
	echo ""
	
done

echo "all images finished, converted $counter images"

echo "++++++++++++++++++++++++++++++++++++++++++++++"
echo "+                                            +"
echo "++++++++++++++++++++++++++++++++++++++++++++++"
