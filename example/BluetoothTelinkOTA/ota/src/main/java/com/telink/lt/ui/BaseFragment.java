package com.telink.lt.ui;

import android.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.telink.lt.R;

/**
 * Created by Administrator on 2017/2/24.
 */
public class BaseFragment extends Fragment {

    protected Toast toast;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
    }

    public void toastMsg(CharSequence s) {

        if (this.toast != null) {
            this.toast.setView(this.toast.getView());
            this.toast.setDuration(Toast.LENGTH_SHORT);
            this.toast.setText(s);
            this.toast.show();
        }
    }
}
