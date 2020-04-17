package com.telink.lt.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.telink.lt.util.TelinkLog;

/**
 *
 * Created by Administrator on 2017/2/21.
 */
public class BaseActivity extends Activity {

    protected Toast toast;
    private WaitingDialog mWaitingDialog;
    protected final String TAG = getClass().getSimpleName();

    @Override
    @SuppressLint("ShowToast")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelinkLog.w(TAG + " onCreate");

        this.toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dismissWaitingDialog();
        TelinkLog.w(TAG + " onDestroy");
        this.toast.cancel();
        this.toast = null;
    }

    public void toastMsg(CharSequence s) {

        if (this.toast != null) {
            this.toast.setView(this.toast.getView());
            this.toast.setDuration(Toast.LENGTH_SHORT);
            this.toast.setText(s);
            this.toast.show();
        }
    }

    protected void showWaitingDialog(String tip) {
        if (mWaitingDialog == null) {
            mWaitingDialog = new WaitingDialog(this);
        }
        mWaitingDialog.setWaitingText(tip);
        if (!mWaitingDialog.isShowing()) {
            mWaitingDialog.show();
        }
    }

    protected void dismissWaitingDialog() {
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
        }
    }


}
