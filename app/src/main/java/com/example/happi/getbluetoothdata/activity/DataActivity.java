package com.example.happi.getbluetoothdata.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happi.getbluetoothdata.R;
import com.example.happi.getbluetoothdata.model.DeviceResult;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class DataActivity extends Activity {

    private HttpUtils httpUtils;
    private Gson gson;

    private ArrayList<HashMap<String,String>> listItem = new ArrayList<>();
    private ArrayList<DeviceResult> listItem2 = new ArrayList<>();

    private ListView device_lv;
    private SimpleAdapter simpleAdapter;
    private TextView info_tv;

    //public int totalPages = 0;
    //public int currentPage = 1;

    //private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        httpUtils = new HttpUtils(10000);
        httpUtils.configCurrentHttpCacheExpiry(0); // 设置缓存5秒,5秒内直接返回上次成功请求的结果。
        gson = new Gson();

        device_lv = (ListView)findViewById(R.id.device_lv);
        info_tv = (TextView)findViewById(R.id.info);

        getDevice(getIntent().getStringExtra("token"),getIntent().getStringExtra("id"));


        /*

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int page = currentPage - 1;
                if (page > 0) {
                    currentPage = page;
                    getDevice(id,""+currentPage);
                } else {
                }
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("tag","下一页");
                int page = currentPage + 1;
                if (page <= totalPages) {
                    currentPage = page;
                    getDevice(id,""+currentPage);
                } else {
                }
            }
        });

        //创建数据
        db = new DBHelper(DataActivity.this);
        //db.deleteData(null);
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("tag","保存");
                //添加到数据库
                for (Records rec :listItem2) {
                    Log.i("--",rec.value+"<===>"+rec.datetime+"<===>"+rec.id);
                    db.addData(rec);
                }
                Toast.makeText(DataActivity.this,"保存成功",Toast.LENGTH_LONG).show();
            }
        });*/


    }



    /**
     * 获取数据
     */
    public void getDevice(String token,String vendor_id) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", token);
        params.addBodyParameter("vendor_id", vendor_id);

        String url = Constant.XIWANGAPI_URL + "/api/v1/devices";
        httpUtils.send(HttpMethod.GET, url, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        LogUtils.i("成功==》" + responseInfo.result);

                        // 解析数据
                        Type listType = new TypeToken<LinkedList<DeviceResult>>() {}.getType();
                        LinkedList<DeviceResult> resources = gson.fromJson(responseInfo.result, listType);
                        for (Iterator iterator = resources.iterator(); iterator.hasNext();) {
                            DeviceResult resource = (DeviceResult) iterator.next();

                            listItem2.add(resource);

                            HashMap<String, String> map = new HashMap();
                            map.put("value", resource.getMac_address());
                            map.put("datetime", resource.getCreated_at());
                            listItem.add(map);
                        }

                        info_tv.setText("总数有：" + listItem2.size() + "条数据" );


                        /*
                        //解析数据
                        RequestResult result = gson.fromJson(responseInfo.result,RequestResult.class);

                        info_tv.setText("总数有：" + result.totalRecords + " 条\n共：" + result.totalPages + "页\n当前第：" + result.currentPage + "页" );

                        totalPages = result.totalPages;
                        currentPage = result.currentPage;


                        listItem2.clear();
                        listItem2.addAll(result.records);

                        listItem.clear();
                        for (Records rec :result.records) {
                            //Log.i("--",rec.value+"<===>"+rec.datetime+"<===>"+rec.id);
                            HashMap<String, String> map = new HashMap();
                            map.put("value", rec.value);
                            map.put("datetime", rec.datetime);
                            listItem.add(map);
                        }*/

                        if (listItem.size() > 0) {
                            if (simpleAdapter == null) {
                                simpleAdapter = new SimpleAdapter(DataActivity.this,listItem,R.layout.item,new String[] {"value","datetime"},new int[]{R.id.value,R.id.datetime});
                                device_lv.setAdapter(simpleAdapter);
                            }
                            simpleAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(DataActivity.this, "暂无数据", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        LogUtils.e("失败" + error.toString() + "<=====>" + msg);
                    }
                });
    }




}
