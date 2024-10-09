  /**
     * Parse Hex string raw data
     * @param strData hexadecimal string
     * @return
     */
    public static Object receiveData(String strData) {
        int length=strData.length()%2>0?strData.length()/2+1:strData.length()/2;
        ByteBuf msgBodyBuf = Unpooled.buffer(length);
        msgBodyBuf.writeBytes(CommonUtil.hexStr2Byte(strData));
        return receiveData(msgBodyBuf);
    }

    /**
     *  Parse byte[] raw data
     * @param bytes
     * @return
     */
    private static Object receiveData(byte[] bytes)
    {
        ByteBuf msgBodyBuf =Unpooled.buffer(bytes.length);
        msgBodyBuf.writeBytes(bytes);
        return receiveData(msgBodyBuf);
    }

    /**
     * Parse ByteBuf raw data
     * @param in
     * @return
     */
    private static Object receiveData(ByteBuf in)
    {
        Object decoded = null;
        in.markReaderIndex();
        int header = in.readByte();
        if (header == Constant.TEXT_MSG_HEADER) {
            in.resetReaderIndex();
            decoded = ParserUtil.decodeTextMessage(in);
        } else if (header == Constant.BINARY_MSG_HEADER) {
            in.resetReaderIndex();
            decoded = ParserUtil.decodeBinaryMessage(in);
        } else {
            return null;
        }
        return JSONArray.toJSON(decoded).toString();
    }