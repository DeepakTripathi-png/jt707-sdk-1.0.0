package com.jointech.sdk.jt707.utils;

import com.jointech.sdk.jt707.constants.Constant;
import com.jointech.sdk.jt707.model.LocationData;
import com.jointech.sdk.jt707.model.Result;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description:Analysis method tool class</p>
 * @author HyoJung
 * @date 20210526
 */
public class ParserUtil {
    /**
     * Parse Positioning data 0x0200
     * @param in
     * @return
     */
    public static Result decodeBinaryMessage(ByteBuf in) {
        //unexcape rawdata
        ByteBuf msg = (ByteBuf) PacketUtil.decodePacket(in);
        //message length
        int msgLen = msg.readableBytes();
        //packet header
        msg.readByte();
        //message ID
        int msgId = msg.readUnsignedShort();
        //message body properties
        int msgBodyAttr = msg.readUnsignedShort();
        //message body length
        int msgBodyLen = msgBodyAttr & 0b00000011_11111111;
        //Whether to subcontract
        boolean multiPacket = (msgBodyAttr & 0b00100000_00000000) > 0;
        //Remove the base length of the message body
        int baseLen = Constant.BINARY_MSG_BASE_LENGTH;

        //The following packet length is obtained according to the length of the message body and whether it is subcontracted
        int ensureLen = multiPacket ? baseLen + msgBodyLen + 4 : baseLen + msgBodyLen;
        if (msgLen != ensureLen) {
            return null;
        }
         //array of deviceID
        byte[] terminalNumArr = new byte[6];
        msg.readBytes(terminalNumArr);
        //Device ID (remove leading 0)
        String terminalNumber = StringUtils.stripStart(ByteBufUtil.hexDump(terminalNumArr), "0");
        //message serial number
        int msgFlowId = msg.readUnsignedShort();
        //total number of message packets
        int packetTotalCount = 0;
        //package serial number
        int packetOrder = 0;
        //subcontract
        if (multiPacket) {
            packetTotalCount = msg.readShort();
            packetOrder = msg.readShort();
        }
        //message body
        byte[] msgBodyArr = new byte[msgBodyLen];
        msg.readBytes(msgBodyArr);
        if(msgId==0x0200) {
            //Parse the message body
            LocationData locationData=parseLocationBody(Unpooled.wrappedBuffer(msgBodyArr));
            locationData.setIndex(msgFlowId);
            locationData.setDataLength(msgBodyLen);
            //check code
            int checkCode = msg.readUnsignedByte();
            //packet end
            msg.readByte();
            //Get message response content
            String replyMsg= PacketUtil.replyBinaryMessage(terminalNumArr,msgFlowId);
            //Define the location data entity class
            Result model = new Result();
            model.setDeviceID(terminalNumber);
            model.setMsgType("Location");
            model.setDataBody(locationData);
            model.setReplyMsg(replyMsg);
            return model;
        }else {
            //Define the location data entity class
            Result model = new Result();
            model.setDeviceID(terminalNumber);
            model.setMsgType("heartbeat");
            model.setDataBody(null);
            return model;
        }
    }

    /**
     * Parse instruction data
     * @param in raw data 
     * @return
     */
    public static Result decodeTextMessage(ByteBuf in) {

         //The read pointer is set to the message header
        in.markReaderIndex();
        //Look for the end of the message, if not found, continue to wait for the next packet
        int tailIndex = in.bytesBefore(Constant.TEXT_MSG_TAIL);
        if (tailIndex < 0) {
            in.resetReaderIndex();
            return null;
        }
        //Define the location data entity class
        Result model = new Result();
        //packet header(
        in.readByte();
        //Field list
        List<String> itemList = new ArrayList<String>();
        while (in.readableBytes() > 0) {
            //Query the subscript of comma to intercept data
            int index = in.bytesBefore(Constant.TEXT_MSG_SPLITER);
            int itemLen = index > 0 ? index : in.readableBytes() - 1;
            byte[] byteArr = new byte[itemLen];
            in.readBytes(byteArr);
            in.readByte();
            itemList.add(new String(byteArr));
        }
        String msgType = "";
        if (itemList.size() >= 5) {
            msgType = itemList.get(3) + itemList.get(4);
        }
        Object dataBody=null;
        if(itemList.size()>0){
            dataBody="(";
            for(String item :itemList) {
                dataBody+=item+",";
            }
            dataBody=CommonUtil.trimEnd(dataBody.toString(),",");
            dataBody += ")";
        }
        String replyMsg="";
        if(msgType.equals("BASE2")&&itemList.get(5).toUpperCase().equals("TIME")) {
            replyMsg=PacketUtil.replyBASE2Message(itemList);
        }
        model.setDeviceID(itemList.get(0));
        model.setMsgType(msgType);
        model.setDataBody(dataBody);
        model.setReplyMsg(replyMsg);
        return model;
    }

    /**
     * Parse and locate message body
     * @param msgBodyBuf
     * @return
     */
    private static LocationData parseLocationBody(ByteBuf msgBodyBuf){
         //alarm sign
        long alarmFlag = msgBodyBuf.readUnsignedInt();
        //Device status
        long status = msgBodyBuf.readUnsignedInt();
        //latitude
        double lat = NumberUtil.multiply(msgBodyBuf.readUnsignedInt(), NumberUtil.COORDINATE_PRECISION);
        //longitude
        double lon = NumberUtil.multiply(msgBodyBuf.readUnsignedInt(), NumberUtil.COORDINATE_PRECISION);
        //Altitude, in meters
        int altitude = msgBodyBuf.readShort();
        //Speed
        double speed = NumberUtil.multiply(msgBodyBuf.readUnsignedShort(), NumberUtil.ONE_PRECISION);
        //directioin
        int direction = msgBodyBuf.readShort();
        //GPS time
        byte[] timeArr = new byte[6];
        msgBodyBuf.readBytes(timeArr);
        String bcdTimeStr = ByteBufUtil.hexDump(timeArr);
        ZonedDateTime gpsZonedDateTime = CommonUtil.parseBcdTime(bcdTimeStr);
         //Determine whether the south latitude and west longitude are based on the value of the status bit
        if (NumberUtil.getBitValue(status, 2) == 1) {
            lat = -lat;
        }
        if (NumberUtil.getBitValue(status, 3) == 1) {
            lon = -lon;
        }
        //Positioning status
        int locationType=NumberUtil.getBitValue(status, 18);
        if(locationType==0)
        {
            locationType = NumberUtil.getBitValue(status, 1);
        }
        if(locationType==0)
        {
            locationType = NumberUtil.getBitValue(status, 6) > 0 ? 2 : 0;
        }

        LocationData locationData=new LocationData();
        locationData.setGpsTime(gpsZonedDateTime.toString());
        locationData.setLatitude(lat);
        locationData.setLongitude(lon);
        locationData.setLocationType(locationType);
        locationData.setSpeed((int)speed);
        locationData.setDirection(direction);
        //Handling additional information
        if (msgBodyBuf.readableBytes() > 0) {
            parseExtraInfo(msgBodyBuf, locationData);
        }
        return locationData;
    }

    /**
     * Parse additional information
     *
     * @param msgBody
     * @param location
     */
    private static void parseExtraInfo(ByteBuf msgBody, LocationData location) {
        ByteBuf extraInfoBuf = null;
        while (msgBody.readableBytes() > 1) {
            int extraInfoId = msgBody.readUnsignedByte();
            int extraInfoLen = msgBody.readUnsignedByte();
            if (msgBody.readableBytes() < extraInfoLen) {
                break;
            }
            extraInfoBuf = msgBody.readSlice(extraInfoLen);
            switch (extraInfoId) {
                case 0x0F:
                    //Parsing temperature data
                    double temperature = -1000.0;
                    temperature = parseTemperature(extraInfoBuf.readShort());
                    location.setTemperature((int)temperature);
                    break;
                //Wireless communication network signal strength
                case 0x30:
                    int fCellSignal=extraInfoBuf.readByte();
                    location.setGSMSignal(fCellSignal);
                    break;
                 //number of satellites
                case 0x31:
                    int fGPSSignal=extraInfoBuf.readByte();
                    location.setGpsSignal(fGPSSignal);
                    break;
                //battery percentage
                case 0xD4:
                    int fBattery=extraInfoBuf.readUnsignedByte();
                    location.setBattery(fBattery);
                    break;
                 //battery voltage
                case 0xD5:
                    int fVoltage=extraInfoBuf.readUnsignedShort();
                    location.setBattery(fVoltage*0.01);
                    break;
                case 0xDA:
                    //Number of rope cuts
                    int fTimes =extraInfoBuf.readUnsignedShort();
                    //Status bit
                    int status =extraInfoBuf.readUnsignedByte();
                    //Lock status big
                    int fLockStatus=(status&0b0000_0001)==1?0:1;
                    //motion status
                    int fRunStatus=(status&0b0000_0010)>0?1:0;
                    //Sim card status
                    int fSimStatus=(status&0b0000_0100)>0?1:0;
                    //wake-up source
                    int fWakeSource=((status>>3)&0b0111);
                    location.setLockStatus(fLockStatus);
                    location.setLockRope(fLockStatus);
                    location.setUnLockTime(fTimes);
                    location.setRunStatus(fRunStatus);
                    location.setSimStatus(fSimStatus);
                    location.setAwaken(fWakeSource);
                    break;
                case 0xDB:
                    //Number of positioning data sent
                    int sendCount=extraInfoBuf.readUnsignedShort();
                    location.setSendDataCount(sendCount);
                    break;
                case 0xDC:
                    //Debug
                    byte[] debugArr = new byte[4];
                    extraInfoBuf.readBytes(debugArr);
                    String strDebug=ByteBufUtil.hexDump(debugArr);
                    break;
                case 0xF8:
                    //temperature value
                    int temp=extraInfoBuf.readUnsignedShort();
                    if(temp==0xffff){
                        location.setTemperature(-1000);
                    }else {
                        temp=(temp / 10)-50;
                        location.setTemperature(temp);
                    }
                    break;
                case 0xF9:
                    //Protocol version
                    int version=extraInfoBuf.readUnsignedShort();
                    break;
                case 0xFD:
                    //LBS info.
                    int mcc=extraInfoBuf.readUnsignedShort();
                    location.setMCC(mcc);
                    int mnc=extraInfoBuf.readUnsignedByte();
                    location.setMNC(mnc);
                    long cellId=extraInfoBuf.readUnsignedInt();
                    location.setCELLID((int)cellId);
                    int lac=extraInfoBuf.readUnsignedShort();
                    location.setLAC(lac);
                    break;
                case 0xFE:
                    long mileage = extraInfoBuf.readUnsignedInt();
                    location.setMileage(mileage);
                    break;
                default:
                    ByteBufUtil.hexDump(extraInfoBuf);
                    break;
            }
        }
    }

    /**
     * Parse temperature
     * @param temperatureInt
     * @return
     */
    private static double parseTemperature(int temperatureInt) {
        if (temperatureInt == 0xFFFF) {
            return -1000;
        }
        double temperature = ((short) (temperatureInt << 4) >> 4) * 0.1;
        if ((temperatureInt >> 12) > 0) {
            temperature = -temperature;
        }
        return temperature;
    }
}