package com.telink.lt.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.telink.lt.R;
import com.telink.lt.ble.BleNamesResolver;
import com.telink.lt.ble.PropertyResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/23.
 */
public class ServiceListAdapter extends BaseExpandableListAdapter {
    private List<BluetoothGattService> mServiceList;
    private Context mContext;

    public ServiceListAdapter(Context context) {
        this.mContext = context;
        this.mServiceList = new ArrayList<>();
    }

    public void setData(List<BluetoothGattService> serviceList) {
        this.mServiceList = serviceList;
        notifyDataSetInvalidated();
    }

    @Override
    public int getGroupCount() {
        return mServiceList == null ? 0 : mServiceList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mServiceList.get(groupPosition).getCharacteristics().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mServiceList.get(groupPosition);
    }

    @Override
    public BluetoothGattCharacteristic getChild(int groupPosition, int childPosition) {
        return mServiceList.get(groupPosition).getCharacteristics().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mServiceList.get(groupPosition).getUuid().hashCode();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mServiceList.get(groupPosition).getCharacteristics().get(childPosition).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        final BluetoothGattService bluetoothGattService = mServiceList.get(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_service, null);
            groupHolder = new GroupHolder();
            groupHolder.serviceName = (TextView) convertView.findViewById(R.id.serviceName);
            groupHolder.serviceUUID = (TextView) convertView.findViewById(R.id.serviceUUID);
            groupHolder.serviceType = (TextView) convertView.findViewById(R.id.serviceType);
            groupHolder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.serviceName.setText(BleNamesResolver.resolveServiceName(bluetoothGattService.getUuid().toString()));
        groupHolder.serviceType.setText("TYPE: " + getGattServiceTypeDesc(bluetoothGattService.getType()));
        groupHolder.serviceUUID.setText("UUID: " + bluetoothGattService.getUuid().toString());
        if (isExpanded) {
            groupHolder.arrow.setImageResource(R.drawable.more_unfold);
        } else {
            groupHolder.arrow.setImageResource(R.drawable.more);
        }
        return convertView;
    }

    class GroupHolder {
        public TextView serviceName;
        public TextView serviceUUID;
        public TextView serviceType;
        public ImageView arrow;
    }

    class ChildHolder {
        public TextView charName;
        public TextView charProp;
        public TextView charUUID;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        final BluetoothGattCharacteristic bluetoothGattCharacteristic = mServiceList.get(groupPosition).getCharacteristics().get(childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_characteristic, null);
            childHolder = new ChildHolder();
            childHolder.charName = (TextView) convertView.findViewById(R.id.charName);
            childHolder.charProp = (TextView) convertView.findViewById(R.id.charProp);
            childHolder.charUUID = (TextView) convertView.findViewById(R.id.charUUID);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        String charName = BleNamesResolver.resolveCharacteristicName(bluetoothGattCharacteristic.getUuid().toString());
        if (charName.equals(BleNamesResolver.DEFAULT_CHARACTERISTIC_NAME)) {
            StringBuilder cName = new StringBuilder();
            for (BluetoothGattDescriptor descriptor : bluetoothGattCharacteristic.getDescriptors()) {
                if (descriptor.getValue() != null) {
                    cName.append(new String(descriptor.getValue())).append(" ");
                }
            }
            childHolder.charName.setText(cName.toString());
        } else {
            childHolder.charName.setText(charName);
        }
//        childHolder.charName.setText(BleNamesResolver.resolveCharacteristicName(bluetoothGattCharacteristic.getUuid().toString()));
//        childHolder.charProp.setText(getGattServiceTypeDesc(bluetoothGattCharacteristic.getProperties()));
        int prop = bluetoothGattCharacteristic.getProperties();
//        childHolder.charProp.setText("Properties: 0x" + Integer.toHexString(prop) + getGattCharacteristicPropDesc(prop));
        PropertyResolver propertyResolver = new PropertyResolver(prop);
        childHolder.charProp.setText("Properties: " + propertyResolver.getGattCharacteristicPropDesc());
        childHolder.charUUID.setText("UUID: " + bluetoothGattCharacteristic.getUuid().toString());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private String getGattServiceTypeDesc(int type) {
        if (type == BluetoothGattService.SERVICE_TYPE_PRIMARY)
            return "PRIMARY";
        else
            return "SECONDARY";
    }


}
