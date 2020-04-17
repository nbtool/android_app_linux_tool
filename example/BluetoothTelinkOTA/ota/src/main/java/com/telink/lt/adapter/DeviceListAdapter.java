package com.telink.lt.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.telink.lt.R;
import com.telink.lt.ble.AdvDevice;
import com.telink.lt.ui.AdvDeviceListActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * 设备列表适配器
 * Created by Administrator on 2016/10/25.
 */
public class DeviceListAdapter extends BaseAdapter {
    List<AdvDevice> mDevices;
    Context mContext;

    public DeviceListAdapter(Context context, List<AdvDevice> devices) {
        mContext = context;
        mDevices = devices;
    }

    @Override
    public int getCount() {
        return mDevices == null ? 0 : mDevices.size();
    }


    @Override
    public Object getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_list, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_mac = (TextView) convertView.findViewById(R.id.tv_mac);
            holder.tv_rssi = (TextView) convertView.findViewById(R.id.tv_rssi);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.tv_name.setText(mDevices.get(position).device.getName());
        holder.tv_name.setText(parseName(mDevices.get(position).scanRecord));
        holder.tv_mac.setText(mDevices.get(position).device.getAddress());
        holder.tv_rssi.setText("Rssi: " + mDevices.get(position).rssi + " dBm");
        return convertView;
    }

    class ViewHolder {
        public TextView tv_name;
        public TextView tv_mac;
        public TextView tv_rssi;
    }


    private String parseName(byte[] adv_data) {
        String localName = null;
        ByteBuffer buffer = ByteBuffer.wrap(adv_data).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0)
                break;

            byte type = buffer.get();
            if (type != 0x09) {
                buffer.position(buffer.position() + length - 1);
            } else {
                byte sb[] = new byte[length];
                buffer.get(sb, 0, length);
//                length = 0;
                localName = new String(sb).trim();
                break;
            }
            /*length -= 1;

            switch (type) {
                case 0x01: // Flags
                    length--;
                    break;
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                case 0x14: // List of 16-bit Service Solicitation UUIDs
                    while (length >= 2) {
                        length -= 2;
                    }
                    break;
                case 0x04: // Partial list of 32 bit service UUIDs
                case 0x05: // Complete list of 32 bit service UUIDs
                    while (length >= 4) {
                        length -= 4;
                    }
                    break;
                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                case 0x15: // List of 128-bit Service Solicitation UUIDs
                    while (length >= 16) {
                        length -= 16;
                    }
                    break;
                case 0x08: // Short local device name
                case 0x09: // Complete local device name
                    byte sb[] = new byte[length];
                    buffer.get(sb, 0, length);
                    length = 0;
                    localName = new String(sb).trim();
                    break;
                case (byte) 0xFF: // Manufacturer Specific Data
                    length -= 2;
                    break;
                default: // skip
                    break;
            }
            if (length > 0) {
                buffer.position(buffer.position() + length);
            }*/
        }
        return localName;
    }
}
