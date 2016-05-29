package com.my.okhttplaoluodemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn1, btn2,btn3;
    private ImageView iv1;
    private static final int SUCCESS_STATUS = 1;
    private static final int FAIL_STATUS = 0;
    private static final String TAG = "MainActivity------>";
    private OkHttpClient client;
    private Request request;
    private String iv_url = "http://attimg.dospy.com/img/day_130714/20130714_ec6461694efe14f339fe6JpXmWkl4kBK.jpg";
    private String json_url="http://api2.hichao.com/stars?category=%E5%85%A8%E9%83%A8&pin=&ga=%2Fstars&flag=&gv=63&access_token=&gi=862949022047018&gos=5.2.3&p=2013022&gc=xiaomi&gn=mxyc_adr&gs=720x1280&gf=android&page=2";
    private OKHttpManager manager;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_STATUS:
                    byte[] result = (byte[]) msg.obj;
                    //Bitmap bitmap= BitmapFactory.decodeByteArray(result,0,result.length);
                    Bitmap bitmap = new CropSquareTrans().transform(BitmapFactory.decodeByteArray(result, 0, result.length));
                    iv1.setImageBitmap(bitmap);
                    break;

                case FAIL_STATUS:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3= (Button) findViewById(R.id.btn3);
        iv1 = (ImageView) findViewById(R.id.iv1);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
    }

    private void initData() {
        client = new OkHttpClient();
        request = new Request.Builder().get().url(iv_url).build();
        manager = OKHttpManager.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(MainActivity.this, "网络访问失败！" + call.request().body().toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = handler.obtainMessage();
                        if (response.isSuccessful()) {
                            message.what = SUCCESS_STATUS;
                            message.obj = response.body().bytes();
                            handler.sendMessage(message);
                        } else {
                            handler.sendEmptyMessage(FAIL_STATUS);
                        }
                    }
                });
                break;

            case R.id.btn2:
                manager.asyncJsonStringByURL(json_url,
                        new OKHttpManager.Fun1() {
                            @Override
                            public void onResponse(String result) {
                                Log.d(TAG,result);
                            }
                        });
                break;

            case R.id.btn3:
//                Request request=
                break;
        }
    }
}
