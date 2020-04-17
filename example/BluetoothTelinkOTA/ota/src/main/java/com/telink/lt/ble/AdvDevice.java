package com.telink.lt.ble;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 广播设备实体类
 * Created by Administrator on 2017/2/22.
 */
public class AdvDevice implements Parcelable {
    public BluetoothDevice device;
    public int rssi;
    public byte[] scanRecord;

    public AdvDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }

    public static final Creator<AdvDevice> CREATOR = new Creator<AdvDevice>() {
        @Override
        public AdvDevice createFromParcel(Parcel in) {
            return new AdvDevice(in);
        }

        @Override
        public AdvDevice[] newArray(int size) {
            return new AdvDevice[size];
        }
    };

    public AdvDevice(Parcel in) {
        this.device = in.readParcelable(getClass().getClassLoader());
        this.rssi = in.readInt();
        this.scanRecord = new byte[in.readInt()];
        in.readByteArray(this.scanRecord);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.device, 0);
        dest.writeInt(this.rssi);
        dest.writeInt(this.scanRecord.length);
        dest.writeByteArray(this.scanRecord);
    }
}
