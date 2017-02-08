package com.example.happi.getbluetoothdata.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happi.getbluetoothdata.R;
import com.example.happi.getbluetoothdata.model.UserResult;
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

/**
 * Created by happi on 16/8/30.
 */
public class UsersActivity extends Activity {

    private HttpUtils httpUtils;
    private Gson gson;

    private ListView users_lv;
    private SimpleAdapter simpleAdapter;
    private TextView user_tv;

    private ArrayList<HashMap<String,String>> listItem = new ArrayList<>();
    private ArrayList<UserResult> listItem2 = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        httpUtils = new HttpUtils(10000);
        httpUtils.configCurrentHttpCacheExpiry(0); // 设置缓存5秒,5秒内直接返回上次成功请求的结果。
        gson = new Gson();

        users_lv = (ListView)findViewById(R.id.users_lv);
        user_tv = (TextView)findViewById(R.id.user_info);

        getUsers(getIntent().getStringExtra("token"));
    }



    /**
     * 获取用户数据
     */
    public void getUsers(String token) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", token);

        String url = Constant.XIWANGAPI_URL + "/api/v1/users";
        httpUtils.send(HttpMethod.GET, url, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        LogUtils.i("成功==》" + responseInfo.result);

                        // 解析数据
                        Type listType = new TypeToken<LinkedList<UserResult>>() {}.getType();
                        LinkedList<UserResult> resources = gson.fromJson(responseInfo.result, listType);
                        for (Iterator iterator = resources.iterator(); iterator.hasNext();) {
                            UserResult resource = (UserResult) iterator.next();

                            listItem2.add(resource);

                            HashMap<String, String> map = new HashMap();
                            map.put("id",  "ID：" + resource.getId());
                            map.put("datetime", "创建时间：" + resource.getCreated_at());
                            listItem.add(map);
                        }

                        user_tv.setText("总数有：" + listItem2.size() + "条数据" );

                        if (listItem.size() > 0) {
                            if (simpleAdapter == null) {
                                simpleAdapter = new SimpleAdapter(UsersActivity.this,listItem,R.layout.item,new String[] {"id","datetime"},new int[]{R.id.value,R.id.datetime});
                                users_lv.setAdapter(simpleAdapter);
                            }
                            simpleAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(UsersActivity.this, "暂无数据", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        LogUtils.e("失败" + error.toString() + "<=====>" + msg);
                    }
                });
    }




}
