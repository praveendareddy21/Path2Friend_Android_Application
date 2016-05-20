<?php
date_default_timezone_set('Etc/UTC');

header('Access-Control-Allow-Origin:*');
$emailString = strval($_GET['emails']);
$emailArray = explode(",",$emailString);
$from = strval($_GET['from']);

$latitude = strval($_GET['latitude']);
$longitude = strval($_GET['longitude']);
//echo $emailString;
//echo $latitude;
//echo $longitude;
//echo "<br />";

require './PHPMailerAutoload.php';
//Create a new PHPMailer instance
$mail = new PHPMailer;
//Tell PHPMailer to use SMTP
$mail->isSMTP();
//Enable SMTP debugging
// 0 = off (for production use)
// 1 = client messages
// 2 = client and server messages
$mail->SMTPDebug = 2;
//Ask for HTML-friendly debug output
$mail->Debugoutput = 'html';
//Set the hostname of the mail server
//$mail->Host = 'smtp.gmail.com';
// use
$mail->Host = gethostbyname('p3plcpnl0546.prod.phx3.secureserver.net');
// if your network does not support SMTP over IPv6
//Set the SMTP port number - 587 for authenticated TLS, a.k.a. RFC4409 SMTP submission
$mail->Port = 25;
//Set the encryption system to use - ssl (deprecated) or tls
$mail->SMTPSecure = 'tls';
//Whether to use SMTP authentication
$mail->SMTPAuth = true;
//Username to use for SMTP authentication - use full email address for gmail
$mail->Username = "mohit@indianios.guru";
//Password to use for SMTP authentication
$mail->Password = "Fuperkalif1234";
//Set who the message is to be sent from
$mail->setFrom('mohit@indianios.guru', 'Path 2 Friend');
//Set an alternative reply-to address
//$mail->addReplyTo('replyto@example.com', 'First Last');
//Set who the message is to be sent to
foreach($emailArray as $to_add){
$to = str_replace(",", ".", $to_add);
$mail->AddAddress($to);                  // name is optional
}
//$mail->addAddress('mathwani@mail.csuchico.edu', 'Mohit Athwani');
//Set the subject line
$mail->Subject = '[SOS] '. $from . " has requested to share SOS updates with you!";

$mail->Body    = "<b>".$from." is currently at : <a href='http://maps.google.com/?q=".$latitude.",".$longitude."'>" .$latitude.",".$longitude."</a>.</b><br /><br /><img src='http://maps.googleapis.com/maps/api/staticmap?center=".$latitude.",".$longitude."&zoom=13&size=600x300&maptype=roadmap&sensor=false&markers=color:blue%7Clabel:S%7C".$latitude.",".$longitude."'>";
//Read an HTML message body from an external file, convert referenced images to embedded,
//convert HTML into a basic plain-text alternative body
//$mail->msgHTML(<p>Hello</p>, dirname(__FILE__));
//Replace the plain text body with one created manually
$mail->AltBody = 'This is a plain-text message body';
//Attach an image file
//$mail->addAttachment('images/phpmailer_mini.png');
//send the message, check for errors
if (!$mail->send()) {
    echo "Mailer Error: " . $mail->ErrorInfo;
} else {
    echo "Message sent!";
}

?>