package com.jointech.sdk.jt707.constants;

/**
 * constant definition
 * @author HyoJung
 * @date 20210526
 */
public class Constant {
    private Constant(){}
    /**
     * binary message header
     */
    public static final byte BINARY_MSG_HEADER = 0x7E;
    /**
     * Base length without message body
     */
    public static final int BINARY_MSG_BASE_LENGTH = 15;

    /**
     * message length
     */
    public static final int BINARY_MSG_MAX_LENGTH = 102400;

    /**
     * text message header
     */
    public static final byte TEXT_MSG_HEADER = '(';

    /**
     * text message tail
     */
    public static final byte TEXT_MSG_TAIL = ')';

    /**
     *text message delimiter
     */
    public static final byte TEXT_MSG_SPLITER = ',';
}