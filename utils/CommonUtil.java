package com.jointech.sdk.jt707.utils;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>Description: Used to store some public methods encountered in parsing</p>
 *
 * @author lenny
 * @version 1.0.1
 * @date 20210328
 */
public class CommonUtil {
    /**
     * remove last character from string
     * @param inStr input string
     * @param suffix characters to remove
     * @return
     */
    public static String trimEnd(String inStr, String suffix) {
        while(inStr.endsWith(suffix)){
            inStr = inStr.substring(0,inStr.length()-suffix.length());
        }
        return inStr;
    }

    /**
     * Hexadecimal to byte[]
     * @param hex
     * @return
     */
    public static byte[] hexStr2Byte(String hex) {
        ByteBuffer bf = ByteBuffer.allocate(hex.length() / 2);
        for (int i = 0; i < hex.length(); i++) {
            String hexStr = hex.charAt(i) + "";
            i++;
            hexStr += hex.charAt(i);
            byte b = (byte) Integer.parseInt(hexStr, 16);
            bf.put(b);
        }
        return bf.array();
    }

    /**
     * Convert GPS time
     *
     * @param bcdTimeStr
     * @return
     */
    public static ZonedDateTime parseBcdTime(String bcdTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        LocalDateTime localDateTime = LocalDateTime.parse(bcdTimeStr, formatter);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneOffset.UTC);
        return zonedDateTime;
    }

    /**
     *XOR evaluation of each byte
     *
     * @param buf
     * @return
     */
    public static int xor(ByteBuf buf) {
        int checksum = 0;
        while (buf.readableBytes() > 0) {
            checksum ^= buf.readUnsignedByte();
        }
        return checksum;
    }
}