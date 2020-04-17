package com.telink.lt.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.telink.lt.R;

public class WaitingDialog extends Dialog {

    private TextView tipsView;

    public WaitingDialog(Context context) {
        super(context, R.style.WaitingDialogStyle);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_waiting, null);
        tipsView = (TextView) mView.findViewById(R.id.waiting_tips);
        super.setContentView(mView);
    }

    public void setWaitingText(String waitingText) {
        if (!TextUtils.isEmpty(waitingText)) {
            tipsView.setText(waitingText);
        }
    }

    public void setWaitingText(int resId) {
        if (resId != 0)
            tipsView.setText(resId);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }
}
