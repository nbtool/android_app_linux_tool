package com.telink.lt.web;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 文件下载器
 * Created by kee on 2016/9/23.
 */
public class TelinkHttpClient {
    private static TelinkHttpClient mHttpclient;
//    public final static String Base_Url = "http://119.23.231.171";
    public final static String Base_Url_0 = "https://contentmsa-sh.vips100.com/v2/delivery/data/2687fdbb0d0a404894aefcbc37b778db/OTA_TEST_BIN/OTA_TEST_BIN_Customer1_FK/[target_file_name]?token=";
    public final static String Base_Url_1 = "https://contentmsa-hk.vips100.com/v2/delivery/data/2687fdbb0d0a404894aefcbc37b778db/OTA_TEST_BIN/OTA_TEST_BIN_Customer1_FK/[target_file_name]?token=";
    public final static String Replace_Element = "[target_file_name]";

    private final OkHttpClient client = new OkHttpClient();


    private TelinkHttpClient() {

    }

    public static TelinkHttpClient getInstance() {
        if (mHttpclient == null) {
            mHttpclient = new TelinkHttpClient();
        }
        return mHttpclient;
    }

    public void downloadFile(String url, Object tag, Callback callback) throws Exception {
        Request request = new Request.Builder()
                .url(url).tag(tag)
                .build();
        client.setConnectTimeout(15, TimeUnit.SECONDS);
        client.setReadTimeout(15, TimeUnit.SECONDS);
        client.setWriteTimeout(15, TimeUnit.SECONDS);
        client.newCall(request).enqueue(callback);
    }

    public void cancelRequest(Object tag) {
        client.cancel(tag);
    }

    public void getVersion() throws Exception {
        Request request = new Request.Builder()
                .url("http://123.207.9.208/telink/getLatestBleBinVersion")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                System.out.println(response.body().string());
            }
        });
    }
}
