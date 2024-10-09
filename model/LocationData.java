package com.jointech.sdk.jt707.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>Description: Location entity class</p>
 *
 * @author lenny
 * @version 1.0.1
 * @date 20210328
 */
@Data
public class LocationData implements Serializable {
    /**
     * Messagebody
     */
    @JSONField(name = "DataLength")
    public int DataLength;
    /**
     * positioning time
     */
    @JSONField(name = "GpsTime")
    public String GpsTime;
    /**
     * Latitude
     */
    @JSONField(name = "Latitude")
    public double Latitude;
    /**
     * Longitude
     */
    @JSONField(name = "Longitude")
    public double Longitude;
    /**
     *  Positioning type
     */
    @JSONField(name = "LocationType")
    public int LocationType;
    /**
     * Speed
     */
    @JSONField(name = "Speed")
    public int Speed;
    /**
     * Direction
     */
    @JSONField(name = "Direction")
    public int Direction;
    /**
     * Mileage
     */
    @JSONField(name = "Mileage")
    public long Mileage;
    /**
     * GpsSignal
     */
    @JSONField(name = "GpsSignal")
    public int GpsSignal;
    /**
     * GSMSignal
     */
    @JSONField(name = "GSMSignal")
    public int GSMSignal;
    /**
     * Battery
     */
    @JSONField(name = "Battery")
    public double Battery;
    /**
     *  Voltage
     */
    @JSONField(name = "Voltage")
    public int Voltage;
    /**
     * Device lock status
     */
    @JSONField(name = "LockStatus")
    public int LockStatus;
    /**
     * Lock rope status
     */
    @JSONField(name = "LockRope")
    public int LockRope;
    /**
     * Number of rope cuts
     */
    @JSONField(name = "UnLockTime")
    public int UnLockTime;
    /**
     * Motion status
     */
    @JSONField(name = "RunStatus")
    public int RunStatus;
    /**
     * Sim card type
     */
    @JSONField(name = "SimStatus")
    public int SimStatus;
    /**
     * Number of positioning data sent
     */
    @JSONField(name = "SendDataCount")
    public int SendDataCount;
    /**
     * MCC
     */
    @JSONField(name = "MCC")
    public int MCC;
    /**
     * MNC
     */
    @JSONField(name = "MNC")
    public int MNC;
    /**
     * LAC
     */
    @JSONField(name = "LAC")
    public int LAC;
    /**
     * CELLID
     */
    @JSONField(name = "CELLID")
    public long CELLID;
    /**
     * Awaken
     */
    @JSONField(name = "Awaken")
    public int Awaken;
    /**
     * Data serial number
     */
    @JSONField(name = "Index")
    public int Index;
    /**
     * Temperature
     */
    @JSONField(name = "Temperature")
    public int Temperature=-1000;
}