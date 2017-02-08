package com.example.happi.getbluetoothdata.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happi.getbluetoothdata.R;
import com.example.happi.getbluetoothdata.model.VendorsResult;
import com.example.happi.getbluetoothdata.utils.Constant;
import com.example.happi.getbluetoothdata.utils.DBHelper;
import com.example.happi.getbluetoothdata.utils.LogUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YunDataActivity extends Activity {

    private HttpUtils httpUtils;
    private Gson gson;

    private ArrayList<HashMap<String,String>> listItem = new ArrayList<>();
    private ArrayList<Records> listItem2 = new ArrayList<>();

    private ListView device_lv;
    private SimpleAdapter simpleAdapter;
    private TextView info_tv;

    public int totalPages = 0;
    public int currentPage = 1;

    public String id;

    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_data);

        httpUtils = new HttpUtils(10000);
        httpUtils.configCurrentHttpCacheExpiry(0); //设置缓存5秒,5秒内直接返回上次成功请求的结果。
        gson = new Gson();

        device_lv = (ListView)findViewById(R.id.device_lv);
        info_tv = (TextView)findViewById(R.id.info);

        id = getIntent().getStringExtra("id");

        getDevice(id,""+currentPage);


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
                int page = currentPage + 1;
                if (page <= totalPages) {
                    currentPage = page;
                    getDevice(id,""+currentPage);
                } else {
                }
            }
        });


        /**
        //创建数据
        db = new DBHelper(YunDataActivity.this);
        //db.deleteData(null);
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.i("保存");
                //添加到数据库
                for (Records rec :listItem2) {
                    LogUtils.i(rec.value+"<===>"+rec.datetime+"<===>"+rec.id);
                    db.addKaerData(rec);
                }
                Toast.makeText(YunDataActivity.this,"保存成功",Toast.LENGTH_LONG).show();
            }
        });*/
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (final Records records : listItem2) {
                    //LogUtils.e("==>" + records.value);

//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    addDevive(records.value);
                }
            }
        });

    }




    /**
     * 获取数据
     */
    public void getDevice(String id,String page) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("ak", "8a3c3fb30501c328d8d1ad37fe124447");
        params.addBodyParameter("id", id);
        params.addBodyParameter("start", "2016-01-01 01:01:01");
        params.addBodyParameter("end", "2017-01-01 01:01:01");
        params.addBodyParameter("interval", "1");
        params.addBodyParameter("page", "" + page);

        String url = "http://api.wsncloud.com/data/v1/timeline";
        httpUtils.send(HttpMethod.GET, url, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        //LogUtils.i("成功==》" + responseInfo.result);

                        //解析数据
                        YunRequestResult result = gson.fromJson(responseInfo.result,YunRequestResult.class);

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
                        }

                        if (listItem.size() > 0) {
                            if (simpleAdapter == null) {
                                simpleAdapter = new SimpleAdapter(YunDataActivity.this,listItem,R.layout.item,new String[] {"value","datetime"},new int[]{R.id.value,R.id.datetime});
                                device_lv.setAdapter(simpleAdapter);
                            }
                            simpleAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(YunDataActivity.this, "暂无数据", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        LogUtils.e("失败" + error.toString() + "<=====>" + msg);
                    }
                });
    }


    class YunRequestResult {
        public int totalRecords;
        public int totalPages;
        public int currentPage;
        public List<Records> records;
    }

    public class Records {
        public String value;
        public String id;
        public String datetime;
    }






    /**
     * 创建厂商
     * @param product_vendor
     */
    private String vendor_id = "";
    private void addVendors() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("token","use_carefully");
        params.addBodyParameter("product_vendor","德利服装");
        String url = Constant.XIWANGAPI_URL + "/api/v1/vendors";

        httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.i("创建Vendors成功==>" + responseInfo.result);

                //获取 厂商id
                VendorsResult result = gson.fromJson(responseInfo.result, VendorsResult.class);
                if (result != null && !result.equals("")) {
                    vendor_id = result.getId();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.i("创建Vendors失败==>" + error.toString() + "<=====>" + msg);
            }
        });
    }



    /**
     * 添加设备
     * @param macAddress
     */
    private void addDevive(String macAddress) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("token","use_carefully");
        params.addBodyParameter("mac_address",macAddress);
        params.addBodyParameter("device_type_id","5");
        params.addBodyParameter("device_model_id","0");
        params.addBodyParameter("vendor_id","3");

        String url = Constant.XIWANGAPI_URL + "/api/v1/devices";

        httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<Object>() {
            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                LogUtils.i("添加设备成功==>" + responseInfo.result);
                //Uri notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.nn);
                //Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                //r.play();
                // 这里 是在他之后执行的 这个土石 都看到了
                //Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.e("添加设备失败==>" + error.toString() + "<=====>" + msg);
            }
        });

    }








}
