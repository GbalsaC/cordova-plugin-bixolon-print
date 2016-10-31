BixolonPrint Corodva Plugin
==============

Cross-platform BixolonPrint Plugin for Cordova / PhoneGap. Forked from [alfonsovinti](https://github.com/alfonsovinti/cordova-plugin-bixolon-print.git)

### Additional Features

- QRCode printing
- 1D Barcode printing
- MSR Reader listener
- Connection listener
- SmartCard reader:
  - Thai National IDCard (Type 1, Type 2)
  - Credit Card (Under development)

### Supported Platforms

- Android (Using Bixolon SDK v2.2.9)
- iOS (All new features still under development)

## Installation
Below are the methods for installing this plugin automatically using command line tools.

### Using the Cordova CLI

```
$ cordova plugin add https://github.com/xyzxyz442/cordova-plugin-bixolon-print.git
```

### Using the Phonegap CLI

```
$ phonegap local plugin add https://github.com/xyzxyz442/cordova-plugin-bixolon-print.git
```

## Plugin Options

```javascript
cordova.plugins.bixolonPrint.settings = {
  lineFeed: 3,
  formFeed: false,      // enable\disable jump to next position, in black marker and label modes
  autoConnect: true,    // Android only: if this is set to false displays a dialog box for selecting the printer
  toastMessage: true,   // Android only: show a printer message
  separator: '=',
  codePage: cordova.plugins.bixolonPrint.CodePage.CP_1252_LATIN1 // define code page, default value is set to CP_1252_LATIN1.
};
```

## Available Code Page

    CP_437_USA               : 0,
    CP_KATAKANA              : 1,
    CP_850_MULTILINGUAL      : 2,
    CP_860_PORTUGUESE        : 3,
    CP_863_CANADIAN_FRENCH   : 4,
    CP_865_NORDIC            : 5,
    CP_1252_LATIN1           : 16,
    CP_866_CYRILLIC2         : 17,
    CP_852_LATIN2            : 18,
    CP_858_EURO              : 19,
    CP_862_HEBREW_DOS_CODE   : 21, // Android only
    CP_864_ARABIC            : 22,
    CP_THAI42                : 23,
    CP_1253_GREEK            : 24,
    CP_1254_TURKISH          : 25,
    CP_1257_BALTIC           : 26,
    CP_FARSI                 : 27,
    CP_1251_CYRILLIC         : 28,
    CP_737_GREEK             : 29,
    CP_775_BALTIC            : 30,
    CP_THAI14                : 31,
    CP_1255_HEBREW_NEW_CODE  : 33,
    CP_THAI11                : 34, // Android only
    CP_THAI18                : 35, // Android only
    CP_855_CYRILLIC          : 36,
    CP_857_TURKISH           : 37,
    CP_928_GREEK             : 38,
    CP_THAI16                : 39,
    CP_1256_ARABIC           : 40,
    CP_1258_VIETNAM          : 41, // Android only
    CP_KHMER_CAMBODIA        : 42, // Android only
    CP_1250_CZECH            : 43  // Android only

## Available BarCode System

    UPC_A: 65
    UPC_E: 66
    EAN13: 67
    EAN8: 68,
    CODE39: 69
    ITF: 70
    CODABAR: 71
    CODE93: 72
    CODE128: 73

## Available QRCode Model
    MODEL_1: 49
    MODEL_2: 50

## Using the plugin

The plugin creates the object `cordova/plugin/BixolonPrint` with following methods:

### Add line to print

```javascript
cordova.plugins.bixolonPrint.addLine({
    text       : String,    // text to print
    textAlign  : String,    // text align, default left
    textWidth  : int,       // text width, default 0
    textHeight : int,       // text height, default 0
    fontType   : String,    // font type, A or B
    fontStyle  : String     // font style, bold or underlined or reversed
});
```

### Add line separator

```javascript
cordova.plugins.bixolonPrint.addHr(separator String);
```

### Print text lines

```javascript
cordova.plugins.bixolonPrint.printText(successCallback, errorCallback, config Object);
```

### Cut paper

```javascript
cordova.plugins.bixolonPrint.cutPaper(successCallback, errorCallback, config Object);
```

### Get printer status

```javascript
cordova.plugins.bixolonPrint.getStatus(successCallback, errorCallback, printStatus Boolean);
```

### Print Barcode

```javascript
var data = {
  text              : String,    // text to print
  alignment         : String,    // text align, default left (left, center, right)
  width             : int,       // 1 - 6
  height            : int,       //
  barcodeSystem     : int,       // see "cordova.plugins.bixolonPrint.BarCodeSystem"
  characterPosition : int        // see "cordova.plugins.bixolonPrint.BarCodeCharacterPosition"
};
cordova.plugins.bixolonPrint.printBarCode(data, successCallback, errorCallback, printStatus Boolean);

```

### QRCode

```javascript
var data = {
  text       : String,    // text to print
  alignment  : String,    // text align, default left (left, center, right)
  size       : int,       // text width, 1 - 8
  model      : int,       // see "cordova.plugins.bixolonPrint.QRCodeModel"
};
cordova.plugins.bixolonPrint.printQRCode(data, successCallback, errorCallback, printStatus Boolean);
```

### Connection Listener
```javascript
// Start connection listener
cordova.plugins.bixolonPrint.startConnectionListener(connectionCallback, errorCallback);

// Stop connection listener
cordova.plugins.bixolonPrint.stopConnectionListener(successCallback, errorCallback);

cordova.plugins.bixolonPrint.reconnect(successCallback, errorCallback);

cordova.plugins.bixolonPrint.disconnect(successCallback, errorCallback);
```

### MSR Reader Listener
```javascript
// Start MSR Reader listener
cordova.plugins.bixolonPrint.startMsrReaderListener(connectionCallback, errorCallback);

// Stop MSR Reader listener
cordova.plugins.bixolonPrint.stopMsrReaderListener(successCallback, errorCallback);
```

### SCR
```javascript
// Start SCR Listener
cordova.plugins.bixolonPrint.startSmartCardListener(smartCardHandler, errorCallback);

// Stop SCR listener
cordova.plugins.bixolonPrint.stopSmartCardListener();

// Power Up SmartCard
cordova.plugins.bixolonPrint.powerUpSmartCard(responseHandler, errorCallback);

// Get SmartCard card status
cordova.plugins.bixolonPrint.getSmartCardStatus(responseHandler, errorCallback);

// Read SmartCard (TH National ID)
var flag = true // true: included read citizen image

cordova.plugins.bixolonPrint.readThIdCardAll(flag, responseHandler, errorCallback)
```

## Examples

### Print a text

```javascript
cordova.plugins.bixolonPrint.addLine("hello cordova!");
cordova.plugins.bixolonPrint.printText();
```
### Print a custom text

```javascript
// compose text
cordova.plugins.bixolonPrint.addLine({
    text: "hello cordova!"
    textAlign: cordova.plugins.bixolonPrint.TextAlign.CENTER,
    fontStyle: cordova.plugins.bixolonPrint.FontStyle.BOLD
});
cordova.plugins.bixolonPrint.addHr();
cordova.plugins.bixolonPrint.addLine("#@*èòçìàé€");
// finally print
cordova.plugins.bixolonPrint.printText(
    function (response) {
        alert("print success!")
    },
    function (error) {
        alert("print failure: " + error)
    },
    {
        codePage: cordova.plugins.bixolonPrint.CodePage.CP_1252_LATIN1
    }
);
```
### Print Barcode

```javascript
cordova.plugins.bixolonPrint.printBarCode({
  text: value,
  alignment: 'left', // left, center, right
  width: 2, // 1 - 6,
  height: 150,
  barcodeSystem: cordova.plugins.bixolonPrint.BarCodeSystem.CODE128,
  characterPosition: cordova.plugins.bixolonPrint.BarCodeCharacterPosition.BELOW_BAR_CODE
}, function(response) {
  alert("print success!")
}, function(error) {
  alert("print failure: " + error)
}, {
  codePage: cordova.plugins.bixolonPrint.CodePage.CP_THAI11
});
```
### Print QRCode

```javascript
cordova.plugins.bixolonPrint.printQRCode({
  text: value,
  alignment: 'center', // left, center, right
  size: 4, // 1 - 8,
  model: cordova.plugins.bixolonPrint.QRCodeModel.MODEL_2
}, function(response) {
  alert("print success!")
}, function(error) {
  alert("print failure: " + error)
}, {
  codePage: cordova.plugins.bixolonPrint.CodePage.CP_THAI11
});
```

### Start/Stop MSR Reader Listener

```javascript
var msrReaderRead = function(response) {
  // inside response contains keys [msrTrack1, msrTrack2, msrTrack3]
}

// printer must connected. to reconnect use this
cordova.plugins.bixolonPrint.reconnect();

cordova.plugins.bixolonPrint.startMsrReaderListener(
  msrReaderRead,
  function(error) {
    alert("MSR reader listener error: " + error)
  }
);

cordova.plugins.bixolonPrint.stopMsrReaderListener();
```

### Start/Stop Connection Listener

```javascript
var connectionMessageReceived = function(response) {
  // inside response contains keys [message, isConnected]
}

cordova.plugins.bixolonPrint.startConnectionListener(
  connectionMessageReceived,
  function(error) {
    alert("Connection listener error: " + error.message);
  }
);

cordova.plugins.bixolonPrint.stopConnectionListener();
```

### Reconnect/Disconnect manually

```javascript
// to reconnect
cordova.plugins.bixolonPrint.reconnect();

// to disconnect
cordova.plugins.bixolonPrint.disconnect();
```
