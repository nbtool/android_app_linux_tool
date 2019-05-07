package com.example.third_test;

import android.app.Dialog;
import android.content.Context;
import android.widget.ListView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.Window;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import java.util.List;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.Button;


public class ListViewDialog extends Dialog {

    private final Context mContext;
    private ListView lv ;//����һ���б�
    private List<String> list ;//����һ��List����
    private ArrayAdapter<String> aa ;

	private Button mBtnNo, mBtnYes;

    public ListViewDialog(Context context) {
        super(context);
        mContext = context;
        initView();
        initListView();
        initButton();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.content_dialog, null);
        lv = (ListView) contentView.findViewById(R.id.lv);
        setContentView(contentView);
    }

    private void initListView() {
        list = new ArrayList<String>();//ʵ����List
        //���������������
        //list.add("Item1");
        //list.add("Item2");
        //list.add("Item3");
        //ʵ��������
        //��һ��������Context
        //�ڶ���������ListView��ÿһ�в�����ʽ
        //android.R.layout.simple_list_item_1��ϵͳ��ÿ��ֻ��ʾһ�����ֲ���
        //�������������б���������
        aa = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,list);
        lv.setAdapter(aa);//������������ӳ��ListView��
        //Ϊ�б���Ӽ���
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(mContext, "��ǰѡ���б�����±�Ϊ��"+arg2, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void add_item(String item){
        list.add(item);
        aa.notifyDataSetChanged();
        setHeight();
    }

    private void initButton(){
        mBtnNo = (Button) findViewById(R.id.btn_no);
        mBtnYes = (Button) findViewById(R.id.btn_yes);
        mBtnNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                add_item("xxxx");
            }
        });

        mBtnYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
        setHeight();
    }

    private void setHeight() {
        Window window = getWindow();
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (window.getDecorView().getHeight() >= (int) (displayMetrics.heightPixels * 0.6)) {
            attributes.height = (int) (displayMetrics.heightPixels * 0.6);
        }
        window.setAttributes(attributes);
    }
}
