package com.jointech.sdk.jt707.utils;

import com.jointech.sdk.jt707.constants.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 *  Parse package preprocessing (do unescaping)
 * @author HyoJung
 */
public class PacketUtil {
    /**
     *  Parse message packets
     *
     * @param in
     * @return
     */
    public static Object decodePacket(ByteBuf in) {
        //The readable length cannot be less than the base length
        if (in.readableBytes() < Constant.BINARY_MSG_BASE_LENGTH) {
            return null;
        }

        //Prevent illegal code stream attacks, the data is too large to be abnormal data
        if (in.readableBytes() > Constant.BINARY_MSG_MAX_LENGTH) {
            in.skipBytes(in.readableBytes());
            return null;
        }

        //Look for the end of the message, if not found, continue to wait for the next packet
        in.readByte();
        int tailIndex = in.bytesBefore(Constant.BINARY_MSG_HEADER);
        if (tailIndex < 0) {
            in.resetReaderIndex();
            return null;
        }

        int bodyLen = tailIndex;
         //Create a ByteBuf to store the reversed data
        ByteBuf frame = ByteBufAllocator.DEFAULT.heapBuffer(bodyLen + 2);
        frame.writeByte(Constant.BINARY_MSG_HEADER);
        //The data between the header and the end of the message is escaped
        unescape(in, frame, bodyLen);
        in.readByte();
        frame.writeByte(Constant.BINARY_MSG_HEADER);

        //The length after the reverse escape cannot be less than the base length
        if (frame.readableBytes() < Constant.BINARY_MSG_BASE_LENGTH) {
            ReferenceCountUtil.release(frame);
            return null;
        }
        return frame;
    }

    /**
     * In the message header, message body and check code, 0x7D 0x02 is reversed to 0x7E, and 0x7D 0x01 is reversed to 0x7D
     *
     * @param in
     * @param frame
     * @param bodyLen
     */
    public static void unescape(ByteBuf in, ByteBuf frame, int bodyLen) {
        int i = 0;
        while (i < bodyLen) {
            int b = in.readUnsignedByte();
            if (b == 0x7D) {
                int nextByte = in.readUnsignedByte();
                if (nextByte == 0x01) {
                    frame.writeByte(0x7D);
                } else if (nextByte == 0x02) {
                    frame.writeByte(0x7E);
                } else {
                     //abnormal data
                    frame.writeByte(b);
                    frame.writeByte(nextByte);
                }
                i += 2;
            } else {
                frame.writeByte(b);
                i++;
            }
        }
    }

    /**
     * In the message header, message body and check code, 0x7E is escaped as 0x7D 0x02, and 0x7D is escaped as 0x7D 0x01
     *
     * @param out
     * @param bodyBuf
     */
    public static void escape(ByteBuf out, ByteBuf bodyBuf) {
        while (bodyBuf.readableBytes() > 0) {
            int b = bodyBuf.readUnsignedByte();
            if (b == 0x7E) {
                out.writeShort(0x7D02);
            } else if (b == 0x7D) {
                out.writeShort(0x7D01);
            } else {
                out.writeByte(b);
            }
        }
    }

    /**
     * reply content
     * @param terminalNumArr
     * @param msgFlowId
     * @return
     */
    public static String replyBinaryMessage(byte[] terminalNumArr,int msgFlowId) {
        //Remove the length of the head and tail
        int contentLen = Constant.BINARY_MSG_BASE_LENGTH + 4;
        ByteBuf bodyBuf = ByteBufAllocator.DEFAULT.heapBuffer(contentLen-2);
        ByteBuf replyBuf = ByteBufAllocator.DEFAULT.heapBuffer(25);
        try {
            //message ID
            bodyBuf.writeShort(0x8001);
            //data length
            bodyBuf.writeShort(0x0005);
            //Device ID
            bodyBuf.writeBytes(terminalNumArr);
            Random random = new Random();
            //Generate random numbers from 1-65534
            int index = random.nextInt() * (65534 - 1 + 1) + 1;
            //current message serial number
            bodyBuf.writeShort(index);
            //response message serial number
            bodyBuf.writeShort(msgFlowId);
            //response message ID
            bodyBuf.writeShort(0x0200);
            //response result
            bodyBuf.writeByte(0x00);
            //check code
            int checkCode = CommonUtil.xor(bodyBuf);
            bodyBuf.writeByte(checkCode);
            //packet header
            replyBuf.writeByte(Constant.BINARY_MSG_HEADER);
            //The read pointer is reset to the starting position
            bodyBuf.readerIndex(0);
            //escape
            PacketUtil.escape(replyBuf, bodyBuf);
            //packet end
            replyBuf.writeByte(Constant.BINARY_MSG_HEADER);
            return ByteBufUtil.hexDump(replyBuf);
        } catch (Exception e) {
            ReferenceCountUtil.release(replyBuf);
            return "";
        }
    }

    /**
     *  Timing command reply
     * @param itemList
     */
    public static String replyBASE2Message(List<String> itemList) {
        try {
            //set date format
            ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneOffset.UTC);
            String strBase2Reply = String.format("(%s,%s,%s,%s,%s,%s)", itemList.get(0), itemList.get(1)
                    , itemList.get(2), itemList.get(3), itemList.get(4), DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(currentDateTime));
           return strBase2Reply;
        }catch (Exception e) {
            return "";
        }
    }
}