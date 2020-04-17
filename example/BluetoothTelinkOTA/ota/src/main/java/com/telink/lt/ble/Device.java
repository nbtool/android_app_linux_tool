package com.telink.lt.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;


import com.telink.lt.util.Arrays;
import com.telink.lt.util.TelinkLog;

import java.util.List;
import java.util.UUID;

public class Device extends Peripheral {

    public static final String TAG = Device.class.getSimpleName();

//    private BluetoothGattCharacteristic mOTAGattCharacteristic;

    public UUID SERVICE_UUID = UUID.fromString("00010203-0405-0607-0809-0a0b0c0d1911");

    public static final UUID CHARACTERISTIC_UUID_WRITE = UUID.fromString("00010203-0405-0607-0809-0a0b0c0d2b12");
    private final static int DELAY_PERIOD = 20;

    public static final int OTA_PREPARE = 0xFF00;
    public static final int OTA_START = 0xFF01;
    public static final int OTA_END = 0xFF02;

    public static final int STATE_SUCCESS = 1;
    public static final int STATE_FAILURE = 0;
    public static final int STATE_PROGRESS = 2;

    private static final int TAG_OTA_WRITE = 1;
    private static final int TAG_OTA_READ = 2;
    private static final int TAG_OTA_LAST = 3;
    private static final int TAG_OTA_LAST_READ = 10;
    private static final int TAG_OTA_PRE_READ = 4;
    private static final int TAG_OTA_PREPARE = 5; // prepare
    private static final int TAG_OTA_START = 7;
    private static final int TAG_OTA_END = 8;
    private static final int TAG_OTA_ENABLE_NOTIFICATION = 9;

    private static final int TAG_GENERAL_READ = 11;
    private static final int TAG_GENERAL_WRITE = 12;
    private static final int TAG_GENERAL_READ_DESCRIPTOR = 13;
    private static final int TAG_GENERAL_ENABLE_NOTIFICATION = 14;

    private final OtaPacketParser mOtaParser = new OtaPacketParser();
    private final OtaCommandCallback mOtaCallback = new OtaCommandCallback();
    private final CharacteristicCommandCallback mCharacteristicCommandCallback = new CharacteristicCommandCallback();

    private DeviceStateCallback mDeviceStateCallback;
    private GattOperationCallback mGattOperationCallback;
    private DescriptorCallback mDescriptorCallback;

    public Device(BluetoothDevice device, byte[] scanRecord, int rssi) {
        super(device, scanRecord, rssi);
    }

    public void setDeviceStateCallback(DeviceStateCallback callback) {
        this.mDeviceStateCallback = callback;
    }

    @Override
    protected void onConnect() {
        super.onConnect();
        if (mDeviceStateCallback != null) {
            mDeviceStateCallback.onConnected(this);
        }
    }

    @Override
    protected void onDisconnect() {
        super.onDisconnect();
        resetOta();
        if (mDeviceStateCallback != null) {
            mDeviceStateCallback.onDisconnected(this);
        }
    }

    @Override
    protected void onServicesDiscovered(List<BluetoothGattService> services) {
        super.onServicesDiscovered(services);
        //this.enablePcmNotification();
        if (mDeviceStateCallback != null) {
            mDeviceStateCallback.onServicesDiscovered(this, services);
        }
    }

    @Override
    protected void onNotify(byte[] data, UUID serviceUUID, UUID characteristicUUID, Object tag) {
        super.onNotify(data, serviceUUID, characteristicUUID, tag);
        Log.d(TAG, " onNotify ==> " + Arrays.bytesToHexString(data, ":"));
        if (this.mGattOperationCallback != null)
            mGattOperationCallback.onNotify(data, serviceUUID, characteristicUUID, tag);
    }

    protected void onEnableNotify() {
        if (this.mGattOperationCallback != null) {
            this.mGattOperationCallback.onEnableNotify();
        }
    }

    protected void onDisableNotify() {
        if (this.mGattOperationCallback != null) {
            this.mGattOperationCallback.onDisableNotify();
        }
    }


    protected void onOtaSuccess() {
        if (mDeviceStateCallback != null) {
            mDeviceStateCallback.onOtaStateChanged(this, STATE_SUCCESS);
        }
    }

    protected void onOtaFailure() {
        if (mDeviceStateCallback != null) {
            mDeviceStateCallback.onOtaStateChanged(this, STATE_FAILURE);
        }
    }

    protected void onOtaProgress() {
        if (mDeviceStateCallback != null) {
            mDeviceStateCallback.onOtaStateChanged(this, STATE_PROGRESS);
        }
    }

    /********************************************************************************
     * OTA API
     *******************************************************************************/

    public void startOta(byte[] firmware) {

        TelinkLog.d("Start OTA");
        this.resetOta();
        this.mOtaParser.set(firmware);
        //this.notificationToggle();
//        this.sendOtaStartCommand();
        this.sendOTAPrepareCommand();
    }

    public int getOtaProgress() {
        return this.mOtaParser.getProgress();
    }

    private void resetOta() {
        this.mDelayHandler.removeCallbacksAndMessages(null);
        this.mOtaParser.clear();
    }

    private void setOtaProgressChanged() {

        if (this.mOtaParser.invalidateProgress()) {
            onOtaProgress();
        }
    }

    private void sendOTAPrepareCommand() {
        Command prepareCmd = Command.newInstance();
        prepareCmd.serviceUUID = SERVICE_UUID;
        prepareCmd.characteristicUUID = CHARACTERISTIC_UUID_WRITE;
        prepareCmd.type = Command.CommandType.WRITE_NO_RESPONSE;
        prepareCmd.tag = TAG_OTA_PREPARE;
        prepareCmd.data = new byte[]{OTA_PREPARE & 0xFF, (byte) (OTA_PREPARE >> 8 & 0xFF)};
        sendCommand(mOtaCallback, prepareCmd);
    }

    // OTA 开始时发送的命令
    private void sendOtaStartCommand() {
        Command startCmd = Command.newInstance();
        startCmd.serviceUUID = SERVICE_UUID;
        startCmd.characteristicUUID = CHARACTERISTIC_UUID_WRITE;
        startCmd.type = Command.CommandType.WRITE_NO_RESPONSE;
        startCmd.tag = TAG_OTA_START;
        startCmd.data = new byte[]{OTA_START & 0xFF, (byte) (OTA_START >> 8 & 0xFF)};
        sendCommand(mOtaCallback, startCmd);
    }

    private void sendOtaEndCommand() {
        Command endCmd = Command.newInstance();
        endCmd.serviceUUID = SERVICE_UUID;
        endCmd.characteristicUUID = CHARACTERISTIC_UUID_WRITE;
        endCmd.type = Command.CommandType.WRITE_NO_RESPONSE;
        endCmd.tag = TAG_OTA_END;
        int index = mOtaParser.getIndex();
        /*endCmd.data = new byte[]{OTA_END & 0xFF, (byte) (OTA_END >> 8 & 0xFF),
                (byte) (index & 0xFF), (byte) (index >> 8 & 0xFF),
                (byte) (~index & 0xFF), (byte) (~index >> 8 & 0xFF)
        };*/
        byte[] data = new byte[8];
        data[0] = OTA_END & 0xFF;
        data[1] = (byte) ((OTA_END >> 8) & 0xFF);
        data[2] = (byte) (index & 0xFF);
        data[3] = (byte) (index >> 8 & 0xFF);
        data[4] = (byte) (~index & 0xFF);
        data[5] = (byte) (~index >> 8 & 0xFF);

        int crc = mOtaParser.crc16(data);
        mOtaParser.fillCrc(data, crc);
        endCmd.data = data;
        sendCommand(mOtaCallback, endCmd);
    }


    private boolean sendNextOtaPacketCommand(int delay) {
        boolean result = false;
        if (this.mOtaParser.hasNextPacket()) {
            Command cmd = Command.newInstance();
            cmd.serviceUUID = SERVICE_UUID;
            cmd.characteristicUUID = CHARACTERISTIC_UUID_WRITE;
            cmd.type = Command.CommandType.WRITE_NO_RESPONSE;
            cmd.data = this.mOtaParser.getNextPacket();
            if (this.mOtaParser.isLast()) {
                cmd.tag = TAG_OTA_LAST;
                result = true;
            } else {
                cmd.tag = TAG_OTA_WRITE;
            }
            cmd.delay = delay;
            this.sendCommand(this.mOtaCallback, cmd);
            /*if (this.mOtaParser.isLast()) {
                TelinkLog.d("ota last packet");
                result = true;
                //cmd.tag = TAG_OTA_LAST;
                Command end = Command.newInstance();
                end.serviceUUID = mOTAGattCharacteristic.getService().getUuid();
                end.characteristicUUID = mOTAGattCharacteristic.getUuid();
                end.type = Command.CommandType.WRITE_NO_RESPONSE;
                end.tag = TAG_OTA_LAST;
                end.delay = 0;
                byte[] endPacket = new byte[6];
                endPacket[0] = 0x02;
                endPacket[1] = (byte) 0xFF;
                endPacket[2] = cmd.data[0];
                endPacket[3] = cmd.data[1];
                endPacket[4] = (byte) (0xFF - cmd.data[0]);
                endPacket[5] = (byte) (0xFF - cmd.data[1]);
                end.data = endPacket;
                this.sendCommand(this.mOtaCallback, cmd);
                this.sendCommand(this.mOtaCallback, end);
            } else {
                this.sendCommand(this.mOtaCallback, cmd);
            }*/
        }
        return result;
    }

    private boolean validateOta() {
        /**
         * 发送read指令
         */
        int sectionSize = 16 * 8;
        int sendTotal = this.mOtaParser.getNextPacketIndex() * 16;
        TelinkLog.i("ota onCommandSampled byte length : " + sendTotal);
        if (sendTotal > 0 && sendTotal % sectionSize == 0) {
            TelinkLog.i("onCommandSampled ota read packet " + mOtaParser.getNextPacketIndex());
            Command cmd = Command.newInstance();
            cmd.serviceUUID = SERVICE_UUID;
            cmd.characteristicUUID = CHARACTERISTIC_UUID_WRITE;
            cmd.type = Command.CommandType.READ;
            cmd.tag = TAG_OTA_READ;
            this.sendCommand(mOtaCallback, cmd);
            return true;
        }
        return false;
    }

    public boolean isNotificationEnable(BluetoothGattCharacteristic characteristic) {
        String key = generateHashKey(characteristic.getService().getUuid(),
                characteristic);
        return mNotificationCallbacks.containsKey(key);
    }

    public void notificationToggle(BluetoothGattCharacteristic characteristic) {
        Command cmd = Command.newInstance();
        cmd.serviceUUID = characteristic.getService().getUuid();
        cmd.characteristicUUID = characteristic.getUuid();
        cmd.type = !isNotificationEnable(characteristic) ? Command.CommandType.ENABLE_NOTIFY : Command.CommandType.DISABLE_NOTIFY;
        cmd.tag = TAG_GENERAL_ENABLE_NOTIFICATION;
        sendCommand(mCharacteristicCommandCallback, cmd);
    }

    /**
     * 通用read方法
     *
     * @param characteristic
     */
    public void sendGeneralReadCommand(BluetoothGattCharacteristic characteristic) {
        Command cmd = Command.newInstance();
        cmd.serviceUUID = characteristic.getService().getUuid();
        cmd.characteristicUUID = characteristic.getUuid();
        cmd.type = Command.CommandType.READ;
        cmd.tag = TAG_GENERAL_READ;
        this.sendCommand(mCharacteristicCommandCallback, cmd);
    }

    public void sendGeneralWriteCommand(BluetoothGattCharacteristic characteristic, byte[] data) {
        Command cmd = Command.newInstance();
        cmd.serviceUUID = characteristic.getService().getUuid();
        cmd.characteristicUUID = characteristic.getUuid();
        cmd.type = Command.CommandType.WRITE_NO_RESPONSE;
        cmd.tag = TAG_GENERAL_WRITE;
        cmd.data = data;
        this.sendCommand(mCharacteristicCommandCallback, cmd);
    }

    public void sendDescriptorReadCommand(BluetoothGattCharacteristic characteristic) {
        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
            Command cmd = Command.newInstance();
            cmd.serviceUUID = characteristic.getService().getUuid();
            cmd.characteristicUUID = characteristic.getUuid();
            cmd.descriptorUUID = descriptor.getUuid();
            cmd.type = Command.CommandType.READ_DESCRIPTOR;
            cmd.tag = TAG_GENERAL_READ_DESCRIPTOR;
            this.sendCommand(mCharacteristicCommandCallback, cmd);
        }
    }


    public interface DeviceStateCallback {
        void onConnected(Device device);

        void onDisconnected(Device device);

        void onServicesDiscovered(Device device, List<BluetoothGattService> services);

        void onOtaStateChanged(Device device, int state);
    }

    public interface GattOperationCallback {
        /**
         * characteristic read callback
         */
        void onRead(Command command, Object obj);

        /**
         * characteristic write callback
         */
        void onWrite(Command command, Object obj);

        /**
         * characteristic read callback
         */
        void onNotify(byte[] data, UUID serviceUUID, UUID characteristicUUID, Object tag);


        void onEnableNotify();

        void onDisableNotify();
    }

    public interface DescriptorCallback {
        void onDescriptorRead(Command command, Object obj);
    }

    private final class CharacteristicCommandCallback implements Command.Callback {

        @Override
        public void success(Peripheral peripheral, Command command, Object obj) {
            TelinkLog.i("CharacteristicCommandCallback success");
            switch (command.type) {
                case READ:
                    if (mGattOperationCallback != null) {
                        mGattOperationCallback.onRead(command, obj);
                    }
                    break;
                case READ_DESCRIPTOR:
                    if (mDescriptorCallback != null) {
                        mDescriptorCallback.onDescriptorRead(command, obj);
                    }
                    break;
                case WRITE:
                    if (mGattOperationCallback != null) {
                        mGattOperationCallback.onWrite(command, obj);
                    }
                    break;
                case WRITE_NO_RESPONSE:
                    if (mGattOperationCallback != null) {
                        mGattOperationCallback.onWrite(command, obj);
                    }
                    break;
                case ENABLE_NOTIFY:
                    if (mGattOperationCallback != null) {
                        mGattOperationCallback.onEnableNotify();
                    }
                    break;
                case DISABLE_NOTIFY:
                    if (mGattOperationCallback != null) {
                        mGattOperationCallback.onDisableNotify();
                    }
                    break;
            }
        }

        @Override
        public void error(Peripheral peripheral, Command command, String errorMsg) {
            TelinkLog.i("CharacteristicCommandCallback success");
        }

        @Override
        public boolean timeout(Peripheral peripheral, Command command) {
            TelinkLog.i("CharacteristicCommandCallback success");
            return false;
        }
    }

    private final class OtaCommandCallback implements Command.Callback {

        @Override
        public void success(Peripheral peripheral, Command command, Object obj) {
            if (command.tag.equals(TAG_OTA_PRE_READ)) {
                TelinkLog.d("read =========> " + Arrays.bytesToHexString((byte[]) obj, "-"));
            } else if (command.tag.equals(TAG_OTA_PREPARE)) {
                sendOtaStartCommand();
            } else if (command.tag.equals(TAG_OTA_START)) {
                sendNextOtaPacketCommand(0);
            } else if (command.tag.equals(TAG_OTA_END)) {
                // ota success
                resetOta();
                setOtaProgressChanged();
                onOtaSuccess();
            } else if (command.tag.equals(TAG_OTA_LAST)) {
//                sendLastReadCommand();
                sendOtaEndCommand();
                // OTA测试时无需发后面两个指令
                /*resetOta();
                setOtaProgressChanged();
                onOtaSuccess();*/
            } else if (command.tag.equals(TAG_OTA_WRITE)) {
                //int delay = 0;
                //if (delay <= 0) {
                /*if (!validateOta()) {
                    sendNextOtaPacketCommand(0);
                } else {
                    sendNextOtaPacketCommand(20);
//                    mDelayHandler.postDelayed(mOtaTask, delay);
                }*/
                /*if (!validateOta()) {
                    sendNextOtaPacketCommand(0);
                } else {

                    sendNextOtaPacketCommand(DELAY_PERIOD);
                }*/
                if (!validateOta()) {
                    sendNextOtaPacketCommand(0);
                }
                setOtaProgressChanged();
            } else if (command.tag.equals(TAG_OTA_READ)) {
                sendNextOtaPacketCommand(0);
            } else if (command.tag.equals(TAG_OTA_LAST_READ)) {
//                sendOtaEndCommand();
            }
        }

        @Override
        public void error(Peripheral peripheral, Command command, String errorMsg) {
            TelinkLog.d("error packet : " + command.tag + " errorMsg : " + errorMsg);
            if (command.tag.equals(TAG_OTA_END)) {
                // ota success
                resetOta();
                setOtaProgressChanged();
                onOtaSuccess();
            } else {
                resetOta();
                onOtaFailure();
            }
        }

        @Override
        public boolean timeout(Peripheral peripheral, Command command) {
            TelinkLog.d("timeout : " + Arrays.bytesToHexString(command.data, ":"));
            if (command.tag.equals(TAG_OTA_END)) {
                // ota success
                resetOta();
                setOtaProgressChanged();
                onOtaSuccess();
            } else {
                resetOta();
                onOtaFailure();
            }
            return false;
        }
    }
}
