package com.jointech.sdk.jt707.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * digital tools
 * @author HyoJung
 * @date 20210526
 */
public class NumberUtil {
    /**
     * Coordinate accuracy
     */
    public static final BigDecimal COORDINATE_PRECISION = new BigDecimal("0.000001");

    /**
     * Coordinate factor
     */
    public static final BigDecimal COORDINATE_FACTOR = new BigDecimal("1000000");

    /**
     * One decimal place precision
     */
    public static final BigDecimal ONE_PRECISION = new BigDecimal("0.1");

    private NumberUtil() {
    }

    /**
     * Format message ID (convert to 0xXXXX)
     *
    * @param msgId Message ID
     * @return format string
     */
    public static String formatMessageId(int msgId) {
        return String.format("0x%04x", msgId);
    }

    /**
     * format short numbers
     *
     * @param num number
     * @return format string
     */
    public static String formatShortNum(int num) {
        return String.format("0x%02x", num);
    }

   /**
     * Convert 4-digit hexadecimal string
     *
     * @param num digits
     * @return format string
     */
    public static String hexStr(int num) {
        return String.format("%04x", num).toUpperCase();
    }

    /**
     * Parse the value of type short and get the number of digits whose value is 1
     *
     * @param number
     * @return
     */
    public static List<Integer> parseShortBits(int number) {
        List<Integer> bits = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            if (getBitValue(number, i) == 1) {
                bits.add(i);
            }
        }
        return bits;
    }

    /**
     * Parse the value of type int and get the number of digits whose value is 1
     *
     * @param number
     * @return
     */
    public static List<Integer> parseIntegerBits(long number) {
        List<Integer> bits = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            if (getBitValue(number, i) == 1) {
                bits.add(i);
            }
        }
        return bits;
    }

    /**
     * Get the value of the index-th bit in binary
     *
     * @param number
     * @param index
     * @return
     */
    public static int getBitValue(long number, int index) {
        return (number & (1 << index)) > 0 ? 1 : 0;
    }

    /**
     * bit list to int
     *
     * @param bits
     * @param len
     * @return
     */
    public static int bitsToInt(List<Integer> bits, int len) {
        if (bits == null || bits.isEmpty()) {
            return 0;
        }

        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            char value = bits.contains(i) ? '1' : '0';
            chars[len - 1 - i] = value;
        }
        int result = Integer.parseInt(new String(chars), 2);
        return result;
    }

    /**
     *  bit list to long
     *
     * @param bits
     * @param len
     * @return
     */
    public static long bitsToLong(List<Integer> bits, int len) {
        if (bits == null || bits.isEmpty()) {
            return 0L;
        }

        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            char value = bits.contains(i) ? '1' : '0';
            chars[len - 1 - i] = value;
        }
        long result = Long.parseLong(new String(chars), 2);
        return result;
    }

    /**
     * BigDecimal Multiplication
     *
     * @param longNum
     * @param precision
     * @return
     */
    public static double multiply(long longNum, BigDecimal precision) {
        return new BigDecimal(String.valueOf(longNum)).multiply(precision).doubleValue();
    }

    /**
     * BigDecimal Multiplication
     *
     * @param longNum
     * @param precision
     * @return
     */
    public static double multiply(int longNum, BigDecimal precision) {
        return new BigDecimal(String.valueOf(longNum)).multiply(precision).doubleValue();
    }
}