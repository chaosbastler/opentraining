<?php
	$verzeichnis = openDir("./");
	// Verzeichnis lesen
	while ($file = readDir($verzeichnis)) {
		// Höhere Verzeichnisse nicht anzeigen!
		$file_extension = substr($file, strlen($file)-4);
		
		if ($file != "." && $file != ".." && ($file_extension == ".html" || $file_extension == ".HTML" ) ) {
			// Link erstellen
			echo "<a href=\"$file\">$file</a><br>\n";
		}
		
		if(is_dir($file)){
			// Link zu HQ und HD Bildern erstellen
			echo "<a href=\"$file\"/hq/list_images.php>HQ $file</a><br>\n";
			echo "<a href=\"$file\"/fullhd/list_images.php>HD $file</a><br>\n";
		}
	}
	// Verzeichnis schließen
	closeDir($verzeichnis);
?>
