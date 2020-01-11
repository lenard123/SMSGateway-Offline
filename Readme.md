# SMSGateway Offline

## Description
Sending SMS using any Programming Language.  
This app is just similar to [SMSGateway](https://smsgateway.me/) but
it doesn't require you to use internet to work
but make sure that your phone is connected to
the same local network.

## HOW TO SETUP?

1. First download and install [SMSGateway-offline.apk](https://github.com/lenard123/SMSGateway-Offline/raw/master/app/build/bin/app.apk) into your android Phone.
2. Open the app and start the server.
3. done.

![Screenshot 1](https://github.com/lenard123/SMSGateway-Offline/raw/master/images/Screenshot1.png "Screenshot 1")

## HOW TO USE
- Make sure that your phone is connected to the same local network.
- Just send an http get request to (Phone IP):8080?number=[number of recipients]&message=[message to send]

## EXAMPLE

### Using Javascript
```javascript
var number = 1234
var message = "Hello world"
axios.get('http://0.0.0.0:8080',{
    params:{number,message}
}).then(function(response){
    console.log(response)
}).catch(function(err){
    console.log(err)
})
```

### Using PHP

First download [SmsGateway.php](https://github.com/lenard123/SMSGateway-Offline/blob/master/API/PHP/SmsGateway.php) and add it to your project.

```php
<?php

//Require the SmsGateway.php
require_once "SmsGateway.php";

//Instantiate the Object and setup the host
$sms = new SmsGateway("192.168.43.1");

//send sms
$sms->sendMessage("8080", "Testing");

```