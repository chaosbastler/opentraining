<?php
// Based on acra-mailer(https://github.com/d-a-n/acra-mailer) of d-a-n.

//CONFIG
$shared_secret = "my_on_github_with_everyone_shared_secret"; // does not really provide security (better than nothing)
$email = "opentraining@skubware.de";

//IMPLEMENTATION
$key = $_GET['key'];
$token = $_GET['token'];
if ($key != hash("md5", "{$shared_secret}+{$token}")) {
	header('HTTP/1.0 403 Forbidden');
	die('403 Forbidden: You are not allowed to access this file.');
}

ob_start();
?>

<table class="table" style="width:100%;" cellpadding="0" cellspacing="0" border="0" align="center">
	
	
	<?php foreach($_REQUEST as $k=>$v) { 	
		// foreach loop that handles the key-value-pairs

		// ignore "key", "token" and empty fields
		if (in_array(trim($k), array("", "key", "token"))) continue;
	?>

	<tr class="<?strtoupper($k) //in css you could e.g. define an different color?>"> 
		<td valign="top">
<?= str_replace("_", " ", strtoupper($k)) ?>
		</td>
		<td valign="top"><pre class="prettyprint linenums">
<?= $v ?></pre>
		</td>
	</tr>
	

	
	<?php } ?>
</table>



<?php
	// read mail template from file
	$filename="mail_template.txt";
	$mail_content="";
	$file = fopen($filename, "r");
	while(!feof($file)) {
		//read file line by line into variable
		$mail_content = $mail_content . fgets($file, 4096);
	}
	fclose ($file); 

	// save everything from <table ... to </table>
	$text_to_insert = ob_get_contents();
	
	// insert the table
	$mail_content = str_replace("<!-- REPLACE_HERE -->", $text_to_insert, $mail_content);
	ob_end_clean();

	// send the mail
	$headers = "Content-type: text/html\r\n";
	$subject = "OpenTraining Feedback";

	mail($email, $subject, $mail_content, $headers);
?>
