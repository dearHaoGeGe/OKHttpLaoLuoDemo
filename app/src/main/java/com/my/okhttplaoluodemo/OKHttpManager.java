package com.my.okhttplaoluodemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OKHttp工具类
 * <p>
 * Created by Administrator on 2016/5/29.
 */
public class OKHttpManager {

    private OkHttpClient client;
    private volatile static OKHttpManager manager;
    private static final String TAG = "OKHttpManager------>";
    private Handler handler;
    //提交json数据
    private static final MediaType JSON = MediaType.parse("application/json;charset=ytf-8");
    //提交字符串
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");

    public OKHttpManager() {
        client = new OkHttpClient();
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 采用单例模式获取对象
     *
     * @return
     */
    public static OKHttpManager getInstance() {
        OKHttpManager instance = null;
        if (manager == null) {
            synchronized (OKHttpManager.class) {
                if (instance == null) {
                    instance = new OKHttpManager();
                    manager = instance;
                }
            }
        }
        return instance;
    }

    /**
     * 同步请求，在Android开发中不常用,会阻塞UI线程ANR
     *
     * @param url
     * @return
     */
    public String syncGetByURL(String url) {
        //构建一个request请求
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return request.body().toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 请求指定的URL返回的结果是json字符串
     *
     * @param url
     * @param callBack
     */
    public void asyncJsonStringByURL(String url, final Fun1 callBack) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "" + call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccessJsonStringMethod(response.body().string(), callBack);
                }
            }
        });
    }


    /**
     * 请求返回的是byte[] 字节数组
     *
     * @param url
     * @param callBack
     */
    public void asyncGetByteByURL(String url, final Fun2 callBack){
        Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    onSuccessByteMethod(response.body().bytes(),callBack);
                }
            }
        });
    }

    /**
     * 请求返回结果imageView 类型bitmap
     *
     * @param url
     * @param callBack
     */
    public void asyncDownLoadImageByURL(String url, final Fun3 callBack){
        Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    byte[] data=response.body().bytes();
                    Bitmap bitmap=new CropSquareTrans().transform(BitmapFactory.decodeByteArray(data,0,data.length));
                    callBack.onResponse(bitmap);
                }
            }
        });
    }

    /**
     * 请求的是json对象
     *
     * @param url
     * @param callBack
     */
    public void asyncJsonObjectByURL(String url, final Fun4 callBack){
        Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    onSuccessJsonObjectMethod(response.body().string(),callBack);
                }
            }
        });
    }

    /**
     * 请求返回的结果是json字符串
     *
     * @param jsonValue
     * @param callBack
     */
    private void onSuccessJsonStringMethod(final String jsonValue, final Fun1 callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.onResponse(jsonValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 返回响应的结果是json对象
     *
     * @param jsonValue
     * @param callBack
     */
    private void onSuccessJsonObjectMethod(final String jsonValue, final Fun4 callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.onResponse(new JSONObject(jsonValue));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 请求返回的是byte[] 字节数组
     *
     * @param data
     * @param callBack
     */
    private void onSuccessByteMethod(final byte[] data, final Fun2 callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onResponse(data);
                }
            }
        });
    }

    /**
     * json网络成功的回调
     */
    interface Fun1 {
        void onResponse(String result);
    }

    /**
     * 字节数组
     */
    interface Fun2 {
        void onResponse(byte[] result);
    }

    interface Fun3 {
        void onResponse(Bitmap bitmap);
    }

    interface Fun4 {
        void onResponse(JSONObject jsonObject);
    }
}
