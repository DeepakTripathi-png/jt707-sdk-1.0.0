package com.jointech.sdk.jt707.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * result entity class
 * @author HyoJung
 * @date 20210526
 */
@Data
public class Result implements Serializable {
    @JSONField(name = "DeviceID")
    private String DeviceID;
    @JSONField(name = "MsgType")
    private String MsgType;
    @JSONField(name = "DataBody")
    private Object DataBody;
    @JSONField(name = "ReplyMsg")
    private String ReplyMsg;
}