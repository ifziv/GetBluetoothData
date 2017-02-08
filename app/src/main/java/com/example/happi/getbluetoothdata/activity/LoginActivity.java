package com.example.happi.getbluetoothdata.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.happi.getbluetoothdata.utils.Constant;
import com.example.happi.getbluetoothdata.utils.LogUtils;
import com.example.happi.getbluetoothdata.R;
import com.example.happi.getbluetoothdata.model.RequestResult;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * Created by happi on 16/7/14.
 */
public class LoginActivity extends Activity {

    private HttpUtils httpUtils = new HttpUtils();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        findViewById(R.id.shengchan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("token","use_carefully");
                startActivity(intent);
                //finish();
            }
        });

        findViewById(R.id.yonghu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,UsersActivity.class);
                intent.putExtra("token","use_carefully");
                startActivity(intent);
            }
        });



    }

    /**
     * 注册
     */
    private void users () {
        RequestParams params = new RequestParams();
        params.addBodyParameter("provider","0");//9d5c14ce8d56bc180710a254e8fe2959
        params.addBodyParameter("uid","admin");
        params.addBodyParameter("name","admin");
        params.addBodyParameter("role","2");
        params.addBodyParameter("token","use_carefully");
        String url = Constant.XIWANGAPI_URL + "/api/v1/users";

        httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.i("注册成功==>" + responseInfo.result);

                RequestResult result = gson.fromJson(responseInfo.result, RequestResult.class);
                String token = result.getToken();
                if (token != null && !token.equals("")) {
                    LogUtils.i("token==>" + result.getToken());

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                    finish();
                } else {
                    TextView textView = (TextView)findViewById(R.id.text);
                    textView.setText("登录失败\n" + result.getError());
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.i("登录失败==>" + error.toString() + "<=====>" + msg);
                TextView textView = (TextView)findViewById(R.id.text);
                textView.setText("登录失败 \n 请检查网络...");
            }
        });
    }


}
