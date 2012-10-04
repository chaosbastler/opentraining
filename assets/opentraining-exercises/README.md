opentraining-exercises
======================
(Fitness) Exercises (.xml files and images), especially for the Android app Open Training.
Repository: https://github.com/chaosbastler/opentraining


License
=======
Currently all images are under a Creative Commons Attribution-ShareAlike 3.0 Unported license.
Author/source is http://everkinetic.com/, Everkinetic. 
Unfortunatelly their website is down, but the images can also be found at Wikipedia.org (e.g. http://commons.wikimedia.org/wiki/File:Biceps-curl-1.gif)
and perhaps facebook.com

The .xml files should all contain the license & author of the images.

Image format
============
Original images all were .gif or .png - that's bad when you have to change image size.
So I tried to convert them to .svg, and I think they now do look even better than before.

Specification for .xml files
============================
Every time something starts with '$', replace this with a value(e.g.: $NAME -> Curl).


Each .xml file has to contain this.
<pre><code>
&lt;ExerciseType name="$NAME"&gt;
	&lt;!-- more meta data here --&gt;
&lt;/ExerciseType&gt;
</code></pre> 

Image
-----
Example for including image:
<pre><code>
&lt;Image imageLicenseText="License: Creative Commons Attribution-Share Alike 3.0 Unported, Author: Everkinetic" path="One-arm-preacher-curl-2.gif"/&gt;
</code></pre> 

There is no translation/I18N for the following meta data.
Will propably be added in future.

Description
-----------
<pre><code>
&lt;Description text="$DESCRIPTIONTEXT" /&gt;
</code></pre> 


Hint
----
<pre><code>
&lt;Hint text="$HINTTEXT"/&gt;
</code></pre> 


URL
---
<pre><code>
&lt;RelatedURL url="$URL" /&gt;
</code></pre> 


Muscle:
-------

<pre><code>
&lt;Muscle name="$MUSCLE" level="$ACTIVATIONLEVEL"/&gt;
</code></pre> 

where $ACTIVATIONLEVEL may be 1,3 or 5
$Muscle may be one of these:

Brustmuskel, Bauchmuskeln, Rückenmuskeln, Po,
Schulter, Bizeps, Trizeps, Oberschenkelmuskel,
Unterschenkelmuskel

Examples :
<pre><code>
&lt;Muscle name="Brustmuskel" level="3"/&gt;
&lt;Muscle name="Bauchmuskeln" level="3"/&gt;
&lt;Muscle name="Rückenmuskeln" level="3"/&gt;
&lt;Muscle name="Po" level="3"/&gt;
&lt;Muscle name="Schulter" level="3"/&gt;
&lt;Muscle name="Bizeps" level="3"/&gt;
&lt;Muscle name="Trizeps" level="3"/&gt;
&lt;Muscle name="Oberschenkelmuskel" level="3"/&gt;
&lt;Muscle name="Unterschenkelmuskel" level="3"/&gt;
</code></pre> 


SportsEquipment:
----------------
<pre><code>
&lt;SportsEquipment name="$EQUIPMENTNAME"/&gt;
</code></pre> 
where $EQUIPMENTNAME may be one of these:

Keine
Langhantel
SZ-Stange
Kurzhantel
Trainingsbank
Curlpult
Beinstrecker Maschine
Beinpresse
Gymnastikmatte
Swiss Ball
Klimmzug Stange
Hantelscheibe

Examples:
<pre><code>
&lt;SportsEquipment name="Keine"/&gt;
&lt;SportsEquipment name="Langhantel"/&gt;
&lt;SportsEquipment name="SZ-Stange"/&gt;
&lt;SportsEquipment name="Kurzhantel"/&gt;
&lt;SportsEquipment name="Trainingsbank"/&gt;
&lt;SportsEquipment name="Curlpult"/&gt;
&lt;SportsEquipment name="Beinstrecker Maschine"/&gt;
&lt;SportsEquipment name="Beinpresse"/&gt;
&lt;SportsEquipment name="Gymnastikmatte"/&gt;
&lt;SportsEquipment name="Swiss Ball"/&gt;
&lt;SportsEquipment name="Klimmzug Stange"/&gt;
&lt;SportsEquipment name="Hantelscheibe"/&gt;
</code></pre> 




ExerciseTag
-----------
<pre><code>
&lt;Tag name="$TAG"/&gt;
</code></pre> 

where $TAG may be one of these:

Fitness Studio Übung
Heim Übung
Einsteiger Übung
Fortgeschrittenen Übung
Experten Übung
Isolierte Übung
Komplexe Übung


Currently not used:
Icon
----
<pre><code>
&lt;Icon path="$ICONPATH" /&gt;
</code></pre>
