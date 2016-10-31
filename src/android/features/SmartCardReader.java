package it.alfonsovinti.cordova.plugins.bixolonprint.features;

import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.bixolon.printer.BixolonPrinter;
import com.bixolon.printer.utility.Utility;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import it.alfonsovinti.cordova.plugins.bixolonprint.BixolonPrint;

/**
 * Created by xyzxyz442 on 10/19/2016.
 */

public class SmartCardReader {

    private static final String TAG = "BixolonSmartCardReader";

    // Action for SmartCardReader Reader
    public static final String ACTION_SMARTCARD_POWER_UP = "smartCardPowerUp";
    public static final String ACTION_SMARTCARD_POWER_DOWN = "smartCardPowerDown";
    public static final String ACTION_SMARTCARD_GET_STATUS = "smartCardGetStatus";

    public static final String ACTION_START_SMARTCARD_LISTENER = "startSmartCardListener";
    public static final String ACTION_STOP_SMARTCARD_LISTENER = "stopSmartCardListener";

    // Action for SmartCardReader Reader (ThaiNational IDCard)
    public static final String ACTION_SMARTCARD_TH_IDCARD_READALL = "smartCardThIdCardReadAll";
    //public static final String ACTION_SMARTCARD_TH_IDCARD_SELECT = "smartCardThIdCardSelect";
    //public static final String ACTION_SMARTCARD_TH_IDCARD_CID = "smartCardThIdCardCid";
    //public static final String ACTION_SMARTCARD_TH_IDCARD_INFO = "smartCardThIdCardInfo";
    //public static final String ACTION_SMARTCARD_TH_IDCARD_ADDRESS = "smartCardThIdCardAddress";
    //public static final String ACTION_SMARTCARD_TH_IDCARD_PHOTO = "smartCardThIdCardPhoto";

    // SmartCardReader APDU Command for ThaiNational IDCard
    public static final byte[] SMARTCARD_TH_IDCARD_CMD_SELECT = new byte[] { 0x00, (byte) 0xA4, 0x04, 0x00, 0x08, (byte) 0xA0, 0x00, 0x00, 0x00, 0x54, 0x48, 0x00, 0x01 };

    public static final byte[] SMARTCARD_TH_IDCARD_CMD_READ_CID = new byte[] { (byte) 0x80, (byte) 0xb0, 0x00, 0x04, 0x02, 0x00, 0x0d };
    public static final byte[] SMARTCARD_TH_IDCARD_CMD_READ_INFO = new byte[] { (byte) 0x80, (byte) 0xb0, 0x00, 0x11, 0x02, 0x00, (byte) 0xd1 };
    public static final byte[] SMARTCARD_TH_IDCARD_CMD_READ_ADDRESS = new byte[] { (byte) 0x80, (byte) 0xb0, 0x15, 0x79, 0x02, 0x00, 0x64 };
    public static final byte[] SMARTCARD_TH_IDCARD_CMD_READ_CARD_ISS_EXP = new byte[] { (byte) 0x80, (byte) 0xb0, 0x01, 0x67, 0x02, 0x00, 0x12 };

    public static final byte[] SMARTCARD_TH_IDCARD_TYPE_1_CMD_RESPONSE_CID = new byte[] { 0x00, (byte) 0xc0, 0x00, 0x01, 0x0d };
    public static final byte[] SMARTCARD_TH_IDCARD_TYPE_1_CMD_RESPONSE_INFO = new byte[] { 0x00, (byte) 0xc0, 0x00, 0x01, (byte) 0xd1};
    public static final byte[] SMARTCARD_TH_IDCARD_TYPE_1_CMD_RESPONSE_ADDRESS = new byte[] { 0x00, (byte) 0xc0, 0x00, 0x01, 0x64};
    public static final byte[] SMARTCARD_TH_IDCARD_TYPE_1_CMD_RESPONSE_CARD_ISS_EXP = new byte[] { 0x00, (byte) 0xc0, 0x00, 0x01, 0x12 };

    public static final byte[] SMARTCARD_TH_IDCARD_TYPE_2_CMD_RESPONSE_CID = new byte[] { 0x00, (byte) 0xc0, 0x00, 0x00, 0x0d };
    public static final byte[] SMARTCARD_TH_IDCARD_TYPE_2_CMD_RESPONSE_INFO = new byte[] { 0x00, (byte) 0xc0, 0x00, 0x00, (byte) 0xd1};
    public static final byte[] SMARTCARD_TH_IDCARD_TYPE_2_CMD_RESPONSE_ADDRESS = new byte[] { 0x00, (byte) 0xc0, 0x00, 0x00, 0x64};
    public static final byte[] SMARTCARD_TH_IDCARD_TYPE_2_CMD_RESPONSE_CARD_ISS_EXP = new byte[] { 0x00, (byte) 0xc0, 0x00, 0x00, 0x12 };

    public static final int COMMAND_TH_IDCARD_SELECT = 0;
    public static final int COMMAND_TH_IDCARD_READ_CID = 1;
    public static final int COMMAND_TH_IDCARD_READ_INFO = 2;
    public static final int COMMAND_TH_IDCARD_READ_ADDRESS = 3;
    public static final int COMMAND_TH_IDCARD_READ_CARD_ISS_EXP = 4;
    public static final int COMMAND_TH_IDCARD_TYPE_1_RESPONSE_CID = 5;
    public static final int COMMAND_TH_IDCARD_TYPE_1_RESPONSE_INFO = 6;
    public static final int COMMAND_TH_IDCARD_TYPE_1_RESPONSE_ADDRESS = 7;
    public static final int COMMAND_TH_IDCARD_TYPE_1_RESPONSE_CARD_ISS_EXP = 8;
    public static final int COMMAND_TH_IDCARD_TYPE_2_RESPONSE_CID = 9;
    public static final int COMMAND_TH_IDCARD_TYPE_2_RESPONSE_INFO = 10;
    public static final int COMMAND_TH_IDCARD_TYPE_2_RESPONSE_ADDRESS = 11;
    public static final int COMMAND_TH_IDCARD_TYPE_2_RESPONSE_CARD_ISS_EXP = 12;
    public static final int COMMAND_TH_IDCARD_READ_PHOTO = 13;
    public static final int COMMAND_TH_IDCARD_RESPONSE_PHOTO = 14;

    public static final int RESPONSE_TYPE_STOP_LISTENER = 0;
    public static final int RESPONSE_TYPE_START_LISTENER = 1;
    public static final int RESPONSE_TYPE_POWER_UP = 2;
    public static final int RESPONSE_TYPE_POWER_DOWN = 3;
    public static final int RESPONSE_TYPE_STATUS = 4;
    public static final int RESPONSE_TYPE_EXCHANGE_APDU = 5;
    public static final int RESPONSE_TYPE_TH_IDCARD_READALL = 6;

    public static final int SMARTCARD_TH_IDCARD_TYPE_1 = 1;
    public static final int SMARTCARD_TH_IDCARD_TYPE_2 = 2;

    private CallbackContext callbackContext;

    private int statusBitPower;
    private boolean isCardPowered;
    private boolean isCardInserted;

    private boolean isReadAllPhotoIncluded = false;

    private boolean isReaderListenerStarted = false;

    private int cardType = -1;

    private BixolonPrint plugin;

    private String lastAction;
    private int lastCommand;

    private JSONObject card;
    private ByteArrayOutputStream cardPhoto;
    private int currCmdPhotoIndex = -1;

    private boolean isReadCompleted = false;

    public SmartCardReader(BixolonPrint plugin) {
        this.plugin = plugin;
    }

    public void processMessageRead(Message msg) {
        switch (msg.arg1) {
            case BixolonPrinter.PROCESS_SMART_CARD_POWER_UP:
                processPowerUp(msg);
                break;
            case BixolonPrinter.PROCESS_SMART_CARD_POWER_DOWN:
                processPowerDown(msg);
                break;
            case BixolonPrinter.PROCESS_SMART_CARD_STATUS:
                processStatus(msg);
                break;
            case BixolonPrinter.PROCESS_SMART_CARD_EXCHANGE_APDU:
                processExchangeApdu(msg);
                break;
        }
    }

    public boolean startSmartCardListener() {
        JSONObject metadata = new JSONObject();

        try {
            metadata.put("messageType", RESPONSE_TYPE_START_LISTENER);
        } catch(JSONException e) {
            Log.e(TAG, "BixolonSmartCardReader.startSmartCardListener: " + e.getMessage(), e);
        }

        if(this.callbackContext != null) {
            this.plugin.getCallbackContext().error(createSmartCardData(metadata, "SmartCardReader listener already started.", null));
            return false;
        }

        this.callbackContext = this.plugin.getCallbackContext();

        this.isReaderListenerStarted = true;

        sendSmartCardData(createSmartCardData(metadata, "SmartCardReader listener started.", null), true);

        return true;
    }

    public void stopSmartCardListener() {
        JSONObject metadata = new JSONObject();

        try {
            metadata.put("messageType", RESPONSE_TYPE_STOP_LISTENER);
        } catch(JSONException e) {
            Log.e(TAG, "BixolonSmartCardReader.stopSmartCardListener: " + e.getMessage(), e);
        }

        this.isReaderListenerStarted = false;
        this.sendSmartCardData(createSmartCardData(metadata, "SmartCardReader listener stopped.", null), false);
        this.callbackContext = null;
    }

    public void powerUpSmartCard() {
        Log.d(TAG, "SmartCardReader.powerUpSmartCard_START");

        this.plugin.getPrinter().powerUpSmartCard();

        Log.d(TAG, "SmartCardReader.powerUpSmartCard_END");
    }

    public void powerDownSmartCard() {
        Log.d(TAG, "SmartCardReader.powerDownSmartCard_START");

        this.plugin.getPrinter().powerDownSmartCard();

        Log.d(TAG, "SmartCardReader.powerDownSmartCard_END");
    }

    public void getSmartCardStatus() {
        Log.d(TAG, "SmartCardReader.getSmartCardStatus_START");

        this.plugin.getPrinter().getSmartCardStatus();

        Log.d(TAG, "SmartCardReader.getSmartCardStatus_END");
    }

    public void readAll() {
        Log.d(TAG, "BixolonSmartCardReader.readAll_START");

        this.lastAction = ACTION_SMARTCARD_TH_IDCARD_READALL;

        this.card = new JSONObject();
        this.isReadCompleted = false;

        if(this.isCardPowered && this.isCardInserted) {
            switch(this.cardType) {
                case SMARTCARD_TH_IDCARD_TYPE_1:
                case SMARTCARD_TH_IDCARD_TYPE_2:
                    this.readThIdCard();
                    break;
            }
        } else {
            // Auto power up: under development
            Log.d(TAG, "Auto power up is under development.");
        }

        Log.d(TAG, "BixolonSmartCardReader.readAll_END");
    }

    public boolean isReaderListenerStarted() {
        return this.isReaderListenerStarted;
    }

    public void setIsReadAllPhotoIncluded(boolean isIncluded) {
        this.isReadAllPhotoIncluded = isIncluded;
    }

    private JSONObject createSmartCardData(JSONObject metadata, JSONObject data, Message message) {
        try {
            metadata.put("listenerStarted", this.isReaderListenerStarted);

            String msg = data.getString("message");

            if(message != null) {
                String statusMessage = getStatusString(message.arg2);

                if(message.obj != null) {
                    byte[] response = (byte[]) message.obj;

                    if(message.arg2 == BixolonPrinter.SMART_CARD_STATUS_CODE_COMMAND_SUCCESSFUL) {
                        switch (message.arg1) {
                            case BixolonPrinter.PROCESS_SMART_CARD_POWER_UP:
                                if(response.length > 1) {
                                    if(response[0] == 0x3B && response[1] == 0x68) { //Smart card tested with old type
                                        this.cardType = SMARTCARD_TH_IDCARD_TYPE_2;
                                    } else if (response[0] == 0x3B && response[1] == 0x78) {  //Smart card tested with new type (figure B.)
                                        this.cardType = SMARTCARD_TH_IDCARD_TYPE_2;
                                    } else if (response[0] == 0x3B && response[1] == 0x79) {  // add 2016-07-10
                                        this.cardType = SMARTCARD_TH_IDCARD_TYPE_2;
                                    } else if (response[0] == 0x3B && response[1] == 0x67) {
                                        this.cardType = SMARTCARD_TH_IDCARD_TYPE_1;
                                    } else {
                                        this.cardType = -1;
                                        msg += " Card not supported.";
                                    }

                                    JSONObject card = new JSONObject();
                                    card.put("type", this.cardType);

                                    data.put("data", card);
                                }
                                break;
                            case BixolonPrinter.PROCESS_SMART_CARD_POWER_DOWN:
                                this.cardType = -1;
                                break;
                            case BixolonPrinter.PROCESS_SMART_CARD_STATUS:
                                setupCardStatus(response[0]);
                                if(msg.length() > 0)
                                    msg += " ";
                                msg += getCardStatusString(response[0]);
                                break;
                            case BixolonPrinter.PROCESS_SMART_CARD_EXCHANGE_APDU:

                                if(metadata.getInt("messageType") == RESPONSE_TYPE_TH_IDCARD_READALL) {
                                    this.card.put("type", this.cardType);
                                    data.put("data", card);
                                } else {
                                    try {
                                        msg += new String(response, "UTF-8");
                                    } catch (UnsupportedEncodingException e) {
                                        Log.e(TAG, "BixolonSmartCardReader.createSmartCardData: " + e.getMessage(), e);
                                    }
                                }
                                break;
                        }
                    }
                }

                metadata.put("statusMessage", statusMessage);
            }

            data.put("meta", metadata);
            data.put("message", msg);
        } catch(JSONException e) {
            Log.e(TAG, "BixolonSmartCardReader.createSmartCardData: " + e.getMessage(), e);
        }

        return data;
    }

    private JSONObject createSmartCardData(JSONObject metadata, String dataMessage, Message message) {
        JSONObject data = new JSONObject();

        String msg = "";

        if(dataMessage != null)
            msg = dataMessage;

        try {
            data.put("message", msg);
        } catch(JSONException e) {
            Log.e(TAG, "SmartCardReader.createSmartCardData: " + e.getMessage(), e);
        }


        return createSmartCardData(metadata, data, message);
    }

    private void sendSmartCardData(JSONObject obj, boolean keepCallback) {
        if(callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
            result.setKeepCallback(keepCallback);
            this.callbackContext.sendPluginResult(result);
        }
    }

    private void processPowerUp(Message msg) {
        JSONObject metadata = new JSONObject();

        try {
            metadata.put("messageType", RESPONSE_TYPE_POWER_UP);
        } catch(JSONException e) {
            Log.e(TAG, "SmartCardReader.processPowerUp: " + e.getMessage(), e);
        }

        if(msg.arg2 == BixolonPrinter.SMART_CARD_STATUS_CODE_COMMAND_SUCCESSFUL) {
            this.getSmartCardStatus();

            sendSmartCardData(createSmartCardData(metadata, "SmartCard is powered up.", msg), true);
        }
    }

    private void processPowerDown(Message msg) {
        JSONObject metadata = new JSONObject();

        try {
            metadata.put("messageType", RESPONSE_TYPE_POWER_DOWN);
        } catch(JSONException e) {
            Log.e(TAG, "SmartCardReader.processPowerDown: " + e.getMessage(), e);
        }

        if(msg.arg2 == BixolonPrinter.SMART_CARD_STATUS_CODE_COMMAND_SUCCESSFUL) {
            this.getSmartCardStatus();

            sendSmartCardData(createSmartCardData(metadata, "SmartCard is powered down.", msg), true);
        }
    }

    private void processStatus(Message msg) {
        JSONObject metadata = new JSONObject();

        try {
            metadata.put("messageType", RESPONSE_TYPE_STATUS);
        } catch(JSONException e) {
            Log.e(TAG, "SmartCardReader.processStatus: " + e.getMessage(), e);
        }

        if(msg.arg2 == BixolonPrinter.SMART_CARD_STATUS_CODE_COMMAND_SUCCESSFUL) {
            sendSmartCardData(createSmartCardData(metadata, "", msg), true);
        }
    }

    private void processExchangeApdu(Message msg) {
        if(msg.arg2 == BixolonPrinter.SMART_CARD_STATUS_CODE_COMMAND_SUCCESSFUL) {
            JSONObject metadata = new JSONObject();

            try {
                metadata.put("messageType", RESPONSE_TYPE_EXCHANGE_APDU);
            } catch (JSONException e) {
                Log.e(TAG, "SmartCardReader.processExchangeApdu: " + e.getMessage(), e);
            }
            if (this.lastAction == "") {
                sendSmartCardData(createSmartCardData(metadata, "", msg), true);
            } else {
                if(ACTION_SMARTCARD_TH_IDCARD_READALL.equals(this.lastAction)) {
                    this.processThIdCard(msg);
                }
            }
        }
    }

    private void processThIdCard(Message msg) {
        byte[] response = new byte[256];

        if(msg != null) {
            if(msg.obj != null) {
                response = (byte[]) msg.obj;
            }
        }

        try {
            switch(this.lastCommand) {
                case COMMAND_TH_IDCARD_SELECT:
                    this.lastCommand = COMMAND_TH_IDCARD_READ_CID;
                    this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_CMD_READ_CID);
                    break;
                case COMMAND_TH_IDCARD_READ_CID:
                    if(this.cardType == SMARTCARD_TH_IDCARD_TYPE_1) {
                        this.lastCommand = COMMAND_TH_IDCARD_TYPE_1_RESPONSE_CID;
                        this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_TYPE_1_CMD_RESPONSE_CID);
                    } else if(this.cardType == SMARTCARD_TH_IDCARD_TYPE_2) {
                        this.lastCommand = COMMAND_TH_IDCARD_TYPE_2_RESPONSE_CID;
                        this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_TYPE_2_CMD_RESPONSE_CID);
                    }
                    break;
                case COMMAND_TH_IDCARD_READ_INFO:
                    if(this.cardType == SMARTCARD_TH_IDCARD_TYPE_1) {
                        this.lastCommand = COMMAND_TH_IDCARD_TYPE_1_RESPONSE_INFO;
                        this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_TYPE_1_CMD_RESPONSE_INFO);
                    } else if(this.cardType == SMARTCARD_TH_IDCARD_TYPE_2) {
                        this.lastCommand = COMMAND_TH_IDCARD_TYPE_2_RESPONSE_INFO;
                        this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_TYPE_2_CMD_RESPONSE_INFO);
                    }
                    break;
                case COMMAND_TH_IDCARD_READ_ADDRESS:
                    if(this.cardType == SMARTCARD_TH_IDCARD_TYPE_1) {
                        this.lastCommand = COMMAND_TH_IDCARD_TYPE_1_RESPONSE_ADDRESS;
                        this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_TYPE_1_CMD_RESPONSE_ADDRESS);
                    } else if(this.cardType == SMARTCARD_TH_IDCARD_TYPE_2) {
                        this.lastCommand = COMMAND_TH_IDCARD_TYPE_2_RESPONSE_ADDRESS;
                        this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_TYPE_2_CMD_RESPONSE_ADDRESS);
                    }
                    break;
                case COMMAND_TH_IDCARD_READ_CARD_ISS_EXP:
                    if(this.cardType == SMARTCARD_TH_IDCARD_TYPE_1) {
                        this.lastCommand = COMMAND_TH_IDCARD_TYPE_1_RESPONSE_CARD_ISS_EXP;
                        this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_TYPE_1_CMD_RESPONSE_CARD_ISS_EXP);
                    } else if(this.cardType == SMARTCARD_TH_IDCARD_TYPE_2) {
                        this.lastCommand = COMMAND_TH_IDCARD_TYPE_2_RESPONSE_CARD_ISS_EXP;
                        this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_TYPE_2_CMD_RESPONSE_CARD_ISS_EXP);
                    }
                    break;
                case COMMAND_TH_IDCARD_READ_PHOTO:
                case COMMAND_TH_IDCARD_RESPONSE_PHOTO:
                        this.processThIdCardPhoto(msg, response);
                    break;
                case COMMAND_TH_IDCARD_TYPE_1_RESPONSE_CID:
                case COMMAND_TH_IDCARD_TYPE_2_RESPONSE_CID:
                    String id = new String(response, "TIS-620");
                    this.card.put("id", id.trim());

                    this.lastCommand = COMMAND_TH_IDCARD_READ_INFO;
                    this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_CMD_READ_INFO);
                    break;
                case COMMAND_TH_IDCARD_TYPE_1_RESPONSE_INFO:
                case COMMAND_TH_IDCARD_TYPE_2_RESPONSE_INFO:
                    String info = new String(response, "TIS-620");

                    String[] splits = info.split("\\s+");

                    String[] thSplits = splits[0].trim().split("[#]+");
                    String[] enSplits = splits[1].trim().split("[#]+");

                    JSONObject title = new JSONObject();
                    title.put("en", enSplits[0].trim());
                    title.put("th", thSplits[0].trim());

                    JSONObject name = new JSONObject();
                    name.put("en", enSplits[1].trim());
                    name.put("th", thSplits[1].trim());

                    JSONObject surname = new JSONObject();
                    surname.put("en", enSplits[2].trim());
                    surname.put("th", thSplits[2].trim());

                    JSONObject infoObj = new JSONObject();
                    infoObj.put("title", title);
                    infoObj.put("name", name);
                    infoObj.put("surname", surname);

                    String date = splits[2].substring(0, 8).trim();

                    infoObj.put("dateOfBirth", (date.substring(6, 8) + "/" + date.substring(4, 6) + "/" + (Integer.parseInt(date.substring(0,4)) - 543)));

                    int sexInt = Integer.parseInt(info.substring(208, 209));

                    JSONObject sex = new JSONObject();
                    sex.put("raw", sexInt);

                    if(sexInt == 1) {

                        sex.put("en", "Male");
                        sex.put("th", "ชาย");
                    }

                    else if(sexInt == 2) {
                        sex.put("en", "Female");
                        sex.put("th", "หญิง");
                    }

                    infoObj.put("sex", sex);

                    infoObj.put("raw", info);

                    this.card.put("info", infoObj);

                    this.lastCommand = COMMAND_TH_IDCARD_READ_ADDRESS;
                    this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_CMD_READ_ADDRESS);
                    break;
                case COMMAND_TH_IDCARD_TYPE_1_RESPONSE_ADDRESS:
                case COMMAND_TH_IDCARD_TYPE_2_RESPONSE_ADDRESS:
                    String addr = new String(response, "TIS-620");

                    JSONObject addrObj = new JSONObject();
                    addrObj.put("raw", addr);
                    addrObj.put("fullAddress", addr.trim().replace("#", " ").trim());

                    String[] addrSplits = addr.split("#");

                    addrObj.put("houseNo", addrSplits[0].trim());
                    addrObj.put("villageNo", addrSplits[1].trim());
                    addrObj.put("lane", addrSplits[2].trim());
                    addrObj.put("road", addrSplits[3].trim());
                    addrObj.put("tambol", addrSplits[4].trim());
                    addrObj.put("amphur", addrSplits[5].trim());
                    addrObj.put("province", addrSplits[6].trim());

                    this.card.put("address", addrObj);

                    this.lastCommand = COMMAND_TH_IDCARD_READ_CARD_ISS_EXP;
                    this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_CMD_READ_CARD_ISS_EXP);
                    break;
                case COMMAND_TH_IDCARD_TYPE_1_RESPONSE_CARD_ISS_EXP:
                case COMMAND_TH_IDCARD_TYPE_2_RESPONSE_CARD_ISS_EXP:
                    String iss_exp = new String(response, "TIS-620");

                    String issueDate = (iss_exp.substring(6, 8) + "/" + iss_exp.substring(4, 6) + "/" + (Integer.parseInt(iss_exp.substring(0, 4)) - 543));
                    String expireDate = (iss_exp.substring(14, 16) + "/" + iss_exp.substring(12, 14) + "/" + (Integer.parseInt(iss_exp.substring(8, 12)) - 543));

                    this.card.put("issueDate", issueDate);
                    this.card.put("expireDate", expireDate);

                    if(this.isReadAllPhotoIncluded) {
                        this.cardPhoto = new ByteArrayOutputStream();

                        this.lastCommand = COMMAND_TH_IDCARD_READ_PHOTO;
                        this.currCmdPhotoIndex = 0;
                        //this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_CMD_PHOTO[this.currCmdPhotoIndex][0]);

                        this.isStartReadJpegFile = false;
                        this.currStartAndOffsetToRead = new byte[] { 0x01, (byte) 0x5A };
                        this.plugin.getPrinter().exchangeApdu(new byte[] { (byte)0x80, (byte)0xB0, 0x01, (byte) 0x5A, 0x02, 0x00, (byte) 0xFC });
                    } else {
                        JSONObject metadata = new JSONObject();

                        metadata.put("messageType", RESPONSE_TYPE_TH_IDCARD_READALL);

                        this.isReadCompleted = true;

                        sendSmartCardData(createSmartCardData(metadata, "TH National IDCard readAll completed.", msg), true);
                    }
                    break;
            }
        } catch (JSONException e) {
            Log.e(TAG, "BixolonSmartCardReader.processThIdCard: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "BixolonSmartCardReader.processThIdCard: " + e.getMessage(), e);
        }
    }

    private byte[] findStartOffsetBytes(int responseLength) {
        byte[] bytes = new byte[2];

        int start = this.currStartAndOffsetToRead[0] & 0xff;
        int offset = this.currStartAndOffsetToRead[1] & 0xff;
        int newOffset = offset + responseLength;

        bytes[1] =  (byte) (newOffset);

        int lastByte = newOffset - 1;

        if (lastByte >= 256) {
            bytes[0] = (byte)((((start * 256) + 256) >> 8) & 0xff);

            if(lastByte > 256) {
                bytes[1] = (byte)((newOffset) - ((bytes[0] & 0xff) * 256));
            }
        }

        return bytes;
    }

    private boolean isStartReadJpegFile = false;
    private boolean isReadJpegFileCompleted = false;
    private byte[] currStartAndOffsetToRead;

    private void processThIdCardPhoto(Message msg, byte[] response) {
        try {
            switch(this.lastCommand) {
                case COMMAND_TH_IDCARD_READ_PHOTO:
                    if(response.length > 0) {
                        this.lastCommand = COMMAND_TH_IDCARD_RESPONSE_PHOTO;
                        //this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_CMD_PHOTO[this.currCmdPhotoIndex][1]);
                        this.plugin.getPrinter().exchangeApdu(new byte[] { 0x00, (byte)0xC0, 0x00, 0x00, (byte)0xFC });
                    } else {
                        JSONObject metadata = new JSONObject();

                        metadata.put("messageType", RESPONSE_TYPE_TH_IDCARD_READALL);

                        this.isReadCompleted = true;

                        sendSmartCardData(createSmartCardData(metadata, "TH National IDCard readAll completed.", msg), true);
                    }
                    break;
                case COMMAND_TH_IDCARD_RESPONSE_PHOTO:
                    boolean isFoundJpegBeginFile = false;
                    boolean isFoundJpegEndFile = false;
                    int jpegBeginAt = -1;
                    int jpegEndAt = -1;

                    int resLength = response.length - 2;

                    //Log.d(TAG, "READ[" + resLength + "][" + Utility.toHexString(this.currStartAndOffsetToRead) + "]: " + new String(response, "TIS-620"));

                    for(int i=0;i<resLength;i++) {
                        if(response[i] == (byte)0xFF && response[i + 1] == (byte)0xD8 && !this.isStartReadJpegFile) {
                            isFoundJpegBeginFile = true;
                            jpegBeginAt = i;
                            Log.d(TAG, "Found jpeg begin file [" + Utility.toHexString(this.currStartAndOffsetToRead) + "] at[" + i + "]: " + Utility.toHexString(new byte[] { response[i], response[i + 1] }));
                            break;
                        } else if(response[i] == (byte)0xFF && response[i + 1] == (byte)0xD9) {
                            jpegEndAt = i + 1;
                            isFoundJpegEndFile = true;
                            Log.d(TAG, "Found jpeg end file [" + Utility.toHexString(this.currStartAndOffsetToRead) + "] at[" + i + "]: " + Utility.toHexString(new byte[] { response[i], response[i + 1] }));
                            break;
                        }
                    }

                    if((this.isStartReadJpegFile && !this.isReadJpegFileCompleted) || isFoundJpegEndFile) {
                        int length = resLength;

                        if(isFoundJpegEndFile) {
                            length = jpegEndAt + 1;
                        }

                        ByteBuffer bb = ByteBuffer.allocate(length);
                        bb.put(response, 0, length);

                        Log.d(TAG, "READ_IMAGE[" + length + "][" + Utility.toHexString(this.currStartAndOffsetToRead) + "][" + this.currCmdPhotoIndex + "]: " + Utility.toHexString(bb.array()));

                        this.cardPhoto.write(response, 0, length);
                    } else {
                        Log.d(TAG, "READ[" + resLength + "][" + Utility.toHexString(this.currStartAndOffsetToRead) + "]: " + Utility.toHexString(response));
                    }

                    this.lastCommand = COMMAND_TH_IDCARD_READ_PHOTO;

                    if(isFoundJpegBeginFile && !this.isStartReadJpegFile) {
                        this.isStartReadJpegFile = true;
                        this.isReadJpegFileCompleted = false;

                        this.currStartAndOffsetToRead[1] = (byte)((this.currStartAndOffsetToRead[1] & 0xff) + jpegBeginAt);

                        this.currCmdPhotoIndex = 0;
                        this.plugin.getPrinter().exchangeApdu(new byte[]{(byte) 0x80, (byte) 0xB0, this.currStartAndOffsetToRead[0], this.currStartAndOffsetToRead[1], 0x02, 0x00, (byte) 0xFC});
                    } else if(isFoundJpegEndFile) {
                        this.isStartReadJpegFile = false;
                        this.isReadJpegFileCompleted = true;

                        String encodedImage = Base64.encodeToString(this.cardPhoto.toByteArray(), Base64.DEFAULT);

                        Log.d(TAG, "Image file size: " + this.cardPhoto.size());

                        this.card.put("photo", encodedImage);
                        this.card.put("photoMimetype", "image/jpeg");
                        this.card.put("photoEncode", "base64");

                        JSONObject metadata = new JSONObject();

                        metadata.put("messageType", RESPONSE_TYPE_TH_IDCARD_READALL);

                        this.isReadCompleted = true;

                        sendSmartCardData(createSmartCardData(metadata, "TH National IDCard readAll completed.", msg), true);
                    } else {
                        this.currCmdPhotoIndex++;
                        this.currStartAndOffsetToRead = findStartOffsetBytes(resLength);

                        this.plugin.getPrinter().exchangeApdu(new byte[]{(byte) 0x80, (byte) 0xB0, this.currStartAndOffsetToRead[0], this.currStartAndOffsetToRead[1], 0x02, 0x00, (byte) 0xFC});
                    }
                    break;
            }
        } catch (JSONException e) {
            Log.e(TAG, "BixolonSmartCardReader.processThIdCard: " + e.getMessage(), e);
        }
    }

    private void readThIdCard() {
        this.lastCommand = COMMAND_TH_IDCARD_SELECT;
        this.plugin.getPrinter().exchangeApdu(SMARTCARD_TH_IDCARD_CMD_SELECT);
    }

    private String getStatusString(int statusCode) {
        switch (statusCode) {
            case BixolonPrinter.SMART_CARD_STATUS_CODE_COMMAND_SUCCESSFUL:
                return "Command successful";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_WRONG_COMMAND_LENGTH:
                return "Wrong command length";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_EXCESSIVE_CURRENT:
                return "The reader detects an excessive current. The card is powered off.";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_DEFECTIVE_VOLTAGE:
                return "The reader detects a defective voltage. The card is powered off.";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_SHORT_CIRCUITING:
                return "The card is short-circuiting. The card is powered off.";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_ATR_TOO_LONG:
                return "The ATR is too long. The number of bytes is greater than 33.";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_CARD_TOO_LONG:
                return "The reader is in EMV mode and the T=1 message sent by the card is too long. "
                        + "The buffer is limited to 254 bytes under the T=1 protocol.";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_EMV_PROTOCOL_ERROR:
                return "The reader has encountered a protocol error in the EMV mode. For example, "
                        + "erroneous first byte of the ATR, bad checksum(TCK) character, parity error, "
                        + "timeout during reception of the ATR, ATR is not EMV compliant.";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_CARD_PROTOCOL_ERROR:
                return "Card protocol error during a T=1 exchange.";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_APDU_COMMAND_LENGTH_WRONG:
                return "The APDU command length is wrong.";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_ATR_TCK_INAVALID:
                return "The checksum byte(TCK) of the ATR is invalid. (reader in PC/SC - ISO mode)";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_ATR_TS_INVALID:
                return "The first byte(TS) of the ATR is invalid. (reader in PC/SC - ISO mode)";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_PARITY_ERROR:
                return "Parity error during a microprocessor exchange";

            case BixolonPrinter.SMART_CARD_STATUS_CODE_CARD_NOT_PRESENT:
                return "The card is not present or it is mute.";

            default:
                return "The byte displayed is invalid.";
        }
    }

    private void setupCardStatus(byte cardStatus) {

        if ((cardStatus & BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_18V)
                == BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_18V) {
            this.statusBitPower = BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_18V;
        } else if ((cardStatus & BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_3V)
                == BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_3V) {
            this.statusBitPower = BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_3V;
        } else if ((cardStatus & BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_5V)
                == BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_5V) {
            this.statusBitPower = BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_5V;
        }

        if ((cardStatus & BixolonPrinter.SMART_CARD_STATUS_BIT_POWERED)
                == BixolonPrinter.SMART_CARD_STATUS_BIT_POWERED) {
            this.isCardPowered = true;
        } else {
            this.isCardPowered = false;
        }

        if ((cardStatus & BixolonPrinter.SMART_CARD_STATUS_BIT_INSERTED)
                == BixolonPrinter.SMART_CARD_STATUS_BIT_INSERTED) {
            this.isCardInserted = true;
        } else {
            this.isCardInserted = false;
        }
    }

    private String getCardStatusString(byte cardStatus) {
        StringBuffer buffer = new StringBuffer();

        if ((cardStatus & BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_18V)
                == BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_18V) {
            buffer.append("Power supply = 1.8V");
        } else if ((cardStatus & BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_3V)
                == BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_3V) {
            buffer.append("Power supply = 3V");
        } else if ((cardStatus & BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_5V)
                == BixolonPrinter.SMART_CARD_STATUS_BIT_POWER_5V) {
            buffer.append("Power supply = 5V");
        }

        if ((cardStatus & BixolonPrinter.SMART_CARD_STATUS_BIT_POWERED)
                == BixolonPrinter.SMART_CARD_STATUS_BIT_POWERED) {
            buffer.append(", Card powered");
        } else {
            buffer.append(", Card not powered");
        }

        if ((cardStatus & BixolonPrinter.SMART_CARD_STATUS_BIT_INSERTED)
                == BixolonPrinter.SMART_CARD_STATUS_BIT_INSERTED) {
            buffer.append(", Card inserted");
        } else {
            buffer.append(", Card not inserted");
        }

        return buffer.toString();
    }

    private boolean checksum(String citizenId) {
        int sum = 0;

        if(citizenId.length() != 13)
            return false;

        for(int i=0;i<12;i++) {
            sum += (Integer.parseInt(citizenId.substring(i, 1)) * (13 - i));
        }

        if (((11 - (sum % 11)) % 10) == Integer.parseInt(citizenId.substring(12, 1)))
            return true;

        return false;
    }
}
