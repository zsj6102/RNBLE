package com.example.rn_ble;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin on 2017/5/8.
 */

public class Beacon implements Serializable {
    public BeaconType BType;
    public BluetoothDevice Device;
    public String MacAddress;
    public int Major;
    public int MeasuredPower;
    public int Minor;
    public String Name;
    public int RSSI;
    public String UUID;
    public Date LastScanTime;
    public String Token;
    public int Interval;
    public int TransmitPower;
    public ProtocolType Protocol;

    public Beacon() {
        this.BType = BeaconType.Unknown;
    }

    public Beacon(BluetoothDevice device, String uuid, String name, String macAddress, int major, int minor, int measuredPower, int rssi, ProtocolType protocol) {
        this.BType = BeaconType.Unknown;
        this.BType = BeaconType.Unknown;
        this.Device = device;
        this.UUID = uuid;
        this.Name = name;
        this.MacAddress = macAddress;
        this.Major = major;
        this.Minor = minor;
        this.MeasuredPower = measuredPower;
        this.RSSI = rssi;
        this.LastScanTime = new Date();
        this.Token = null;
        this.Interval = -1;
        this.TransmitPower = -1;
        this.Protocol = protocol;
    }

    public static Beacon fromScanData(BluetoothDevice device, int rssi, byte[] scanData) {
        if((scanData[5] & 255) == 168 && (scanData[6] & 255) == 1) {
            Log.i("fromScanData", "alibeacon getDeviceType：" + device.getType());
            Beacon var9 = new Beacon();
            byte[] var10 = new byte[16];
            System.arraycopy(scanData, 8, var10, 0, 16);
            String var11 = StringConvertUtil.bytesToHexString(var10);
            StringBuilder var12 = new StringBuilder();
            var12.append(var11.substring(0, 8));
            var12.append("-");
            var12.append(var11.substring(8, 12));
            var12.append("-");
            var12.append(var11.substring(12, 16));
            var12.append("-");
            var12.append(var11.substring(16, 20));
            var12.append("-");
            var12.append(var11.substring(20, 32));
            var9.UUID = var12.toString();
            var9.RSSI = rssi;
            if(device != null) {
                var9.MacAddress = device.getAddress();
                var9.Name = device.getName();
                if(var9.Name == null) {
                    var9.Name = "--";
                }
            }

            var9.Device = device;
            var9.Protocol = ProtocolType.AliBeacon;
            return var9;
        } else {
            Log.i("fromScanData", "ibeacon getDeviceType：" + device.getType());
            int startByte = 2;

            boolean patternFound;
            for(patternFound = false; startByte <= 5; ++startByte) {
                if((scanData[startByte + 2] & 255) == 2 && (scanData[startByte + 3] & 255) == 21) {
                    patternFound = true;
                    break;
                }
            }

            if(!patternFound) {
                return null;
            } else {
                Beacon beacon = new Beacon();
                beacon.Major = (scanData[startByte + 20] & 255) * 256 + (scanData[startByte + 21] & 255);
                beacon.Minor = (scanData[startByte + 22] & 255) * 256 + (scanData[startByte + 23] & 255);
                beacon.MeasuredPower = scanData[startByte + 24];
                beacon.RSSI = rssi;
                byte[] proximityUuidBytes = new byte[16];
                System.arraycopy(scanData, startByte + 4, proximityUuidBytes, 0, 16);
                String hexString = StringConvertUtil.bytesToHexString(proximityUuidBytes);
                StringBuilder sb = new StringBuilder();
                sb.append(hexString.substring(0, 8));
                sb.append("-");
                sb.append(hexString.substring(8, 12));
                sb.append("-");
                sb.append(hexString.substring(12, 16));
                sb.append("-");
                sb.append(hexString.substring(16, 20));
                sb.append("-");
                sb.append(hexString.substring(20, 32));
                beacon.UUID = sb.toString();
                if(device != null) {
                    beacon.MacAddress = device.getAddress();
                    beacon.Name = device.getName();
                    if(beacon.Name == null) {
                        beacon.Name = "--";
                    }
                }

                beacon.Device = device;
                beacon.Protocol = ProtocolType.IBeacon;
                return beacon;
            }
        }
    }
}
