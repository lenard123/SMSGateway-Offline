<?php

require_once "SmsGateway.php";


$sms = new SmsGateway("192.168.43.1");
if($sms->sendMessage("09203656587", "Testing testing") == SmsGateway::SUCCESS) {
    echo "Message Sent successfully";
} else {
    echo "Message not sent";
}


