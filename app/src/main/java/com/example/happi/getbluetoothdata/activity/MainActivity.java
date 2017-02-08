package com.example.happi.getbluetoothdata.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.happi.getbluetoothdata.R;
import com.example.happi.getbluetoothdata.model.VendorsResult;
import com.example.happi.getbluetoothdata.utils.Constant;
import com.example.happi.getbluetoothdata.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private HttpUtils httpUtils;
    private Gson gson;

    private ListView sensor_lv;

    private ArrayList<String> sensorArray = new ArrayList();
    private ArrayList<VendorsResult> sensorArray1 = new ArrayList<>();
    private ArrayList<YunSensor> sensorArray2 = new ArrayList<>();

    private String token;

    /**
     * 获取传感云数据  还是我们自己的数据
     * 0：我们自己的  1：传感云的
     */
    private int yun = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        token = getIntent().getStringExtra("token");

        httpUtils = new HttpUtils();
        gson = new Gson();

        sensor_lv = (ListView)findViewById(R.id.sensor_lv);
        sensor_lv.setOnItemClickListener(this);

        if (yun == 0) {
            getSensor();
        } else  {
            getSensorYun();
        }

    }


    /**
     * 得到所有列表
     */
    private void getSensor() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", token);
        String url = Constant.XIWANGAPI_URL + "/api/v1/vendors";
        httpUtils.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                // 解析数据
                Type listType = new TypeToken<LinkedList<VendorsResult>>() {}.getType();
                LinkedList<VendorsResult> resources = gson.fromJson(responseInfo.result, listType);
                for (Iterator iterator = resources.iterator(); iterator.hasNext();) {
                    VendorsResult resource = (VendorsResult)iterator.next();
                    sensorArray.add(resource.getProduct_vendor());
                    sensorArray1.add(resource);
                }
                sensor_lv.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,sensorArray));
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.i(error.toString() + "<=====>" + msg);
                Toast.makeText(MainActivity.this,"发送失败\n" + msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
       if (yun == 0) {
           VendorsResult resultModel = sensorArray1.get(i);
           LogUtils.e("==>" + resultModel.toString());
           Intent intent = new Intent(MainActivity.this,DataActivity.class);
           intent.putExtra("id",resultModel.getId());
           intent.putExtra("token",token);
           startActivity(intent);
       } else {
           YunSensor sensor = sensorArray2.get(i);
           Intent intent = new Intent(MainActivity.this,YunDataActivity.class);
           intent.putExtra("id",sensor.id);
           startActivity(intent);
       }


    }


    /**
     * 获取 传感云上的设备 厂商列表
     */
    public void getSensorYun() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("ak", "8a3c3fb30501c328d8d1ad37fe124447");
        params.addBodyParameter("deviceId", "573bdb3be4b04d683ef5fdea");
        String url = "http://api.wsncloud.com/sensor/v1/list";
        httpUtils.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

               // LogUtils.i("==>" + responseInfo.result);

                // 解析数据
                Type listType = new TypeToken<LinkedList<YunSensor>>() {}.getType();
                LinkedList<YunSensor> resources = gson.fromJson(responseInfo.result, listType);
                for (Iterator iterator = resources.iterator(); iterator.hasNext();) {
                    YunSensor resource = (YunSensor) iterator.next();
                    sensorArray.add(resource.title);
                    sensorArray2.add(resource);
                }
                sensor_lv.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,sensorArray));

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.i(error.toString() + "<=====>" + msg);
                Toast.makeText(MainActivity.this,"获取失败失败\n" + msg,Toast.LENGTH_SHORT).show();
            }
        });
    }


    class YunSensor {
        public String id;
        public String title;
        public String type;
        public String description;
    }


}
