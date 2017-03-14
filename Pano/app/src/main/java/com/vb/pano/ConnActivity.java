package com.vb.pano;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vb.pano.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;

public class ConnActivity extends BaseActivity {

    private EditText nameEdit;
    private EditText pwdEdit;
    private Button connButton;
    private ProgressDialog dialog;

    private TextView skipView;
    private WifiManager mWifiManager;
    private static final int JUMP_CODE = 0x01;
    private static final int PERMISSION_REQUEST_WIFI_CODE = 0x02;
    private ConnectivityManager manager;
    private Spinner spinner;

    private LinearLayout rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conn);

        initView();
    }

    //    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE);
//            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 1);
//            } else {
//                doConn();
//            }
//        } else {
//            doConn();
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    doConn();
//                } else {
//                    Toast.makeText(this,"请打开应用WIFI权限",Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
    private void initView() {
        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        dialog = new ProgressDialog(this);
        dialog.setMessage("正在连接");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        rootView=(LinearLayout)findViewById(R.id.activity_conn);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        nameEdit = (EditText) findViewById(R.id.conn_wifiname);
        pwdEdit = (EditText) findViewById(R.id.conn_wifipwd);
        connButton = (Button) findViewById(R.id.conn_btn);
        skipView = (TextView) findViewById(R.id.skip);
        spinner = (Spinner) findViewById(R.id.wifi_type);
        fillEdit();
        connButton.setOnClickListener(connClickListener);
        if(!mWifiManager.isWifiEnabled())
        {
            mWifiManager.setWifiEnabled(true);
        }
        skipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayJump();
            }
        });
    }

    //填充用户名和密码
    private void fillEdit() {
        HashMap<String, String> map = getPrefs();
        String name = map.get("name");
        String pwd = map.get("pwd");
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
            nameEdit.setText(name);
            nameEdit.setSelection(name.length());
            pwdEdit.setText(pwd);
        }
    }

    //验证用户名和密码是否为空
    private boolean isEmpty(String wifiName) {

        if (!TextUtils.isEmpty(wifiName)) {
            return false;
        }
        return true;
    }

    private void doConn() {
        String name = nameEdit.getText().toString();
        String pwd = pwdEdit.getText().toString();
        if (!isEmpty(name)) {
            String url = "http://192.168.0.1";
            //String url = "http://192.168.199.221";
            url = url + "/network?mode=client&ssid=" + name.trim() + "&password=" + pwd.trim();
            dialog.show();
            //传送wifi至全景相机
            getJson(url);
        } else {
            toast(getString(R.string.login_check_toast));
        }
    }

    //连接wifi热点
    View.OnClickListener connClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            doConn();
        }
    };

    //发送httpget请求
    private void getJson(String url) {
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(5000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
                dialog.dismiss();
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                toast(result);
                if (isConnSuccess(result)) {
                    //返回成功，连接至选定的wifi
                    String name = nameEdit.getText().toString();
                    String pwd = pwdEdit.getText().toString();
//                    connWifi(name, pwd);
                    savePrefs(name, pwd);
                    delayJump();
                } else {
                    dialog.dismiss();
                    toast(getString(R.string.login_changewifi_toasterror));
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                  dialog.dismiss();
                toast(getString(R.string.login_getjson_toasterror));
//                String name = nameEdit.getText().toString();
//                String pwd = pwdEdit.getText().toString();
//                connWifi(name, pwd);
            }
        });
    }

    //连接wifi
//    private void connWifi(String name, String pwd) {
//        if (!mWifiManager.isWifiEnabled()) {
//            mWifiManager.setWifiEnabled(true);
//        }
//        int pos = spinner.getSelectedItemPosition();
//        ConnTask task = new ConnTask(name.trim(), pwd.trim(), pos);
//        task.execute("");
//    }
//
//    class ConnTask extends AsyncTask<String, Integer, Boolean> {
//        private String name;
//        private String pwd;
//        private int type;
//
//        public ConnTask(String name, String pwd, int type) {
//            this.name = name;
//            this.pwd = pwd;
//            this.type = type;
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            WifiConfiguration wifiConfig = createWifiInfo(name, pwd, type);
//            int result = mWifiManager.addNetwork(wifiConfig);
//            Log.i("vbpano", "wifi result :" + result);
//            boolean b = mWifiManager.enableNetwork(result, true);
//            // boolean isSucc = mWifiManager.reassociate();
//            //boolean connected = mWifiManager.reconnect();
//            return b;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean bool) {
//            super.onPostExecute(bool);
//
//            if (!bool) {
//                dialog.dismiss();
//                toast("wifi连接失败");
//            } else {
//                IntentFilter filter = new IntentFilter(
//                        ConnectivityManager.CONNECTIVITY_ACTION);
//                registerReceiver(mReceiver, filter);
//                savePrefs(name, pwd);
//                // delayJump();
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//    }
//
//    BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION) {
//                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
//                if (wifiInfo.getSSID().equals("\""+nameEdit.getText().toString().trim()+"\"")) {
//                    delayJump();
//                }
//
//            }
//        }
//    };

    //延迟五秒跳转到主页
    private void delayJump() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                handler.sendEmptyMessageDelayed(JUMP_CODE, 500);
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog.dismiss();
            //toast("wifi连接成功");
            Intent intent = new Intent(ConnActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregisterReceiver(mReceiver);
    }

    private WifiConfiguration IsExsits(String SSID) { // 查看以前是否已经配置过该SSID
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
        Log.i(Utils.TAG, "SSID:" + SSID + ",password:" + Password);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"".concat(SSID.trim()).concat("\"");
        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            Log.i("vbpano", "networkId:" + tempConfig.networkId);
            mWifiManager.removeNetwork(tempConfig.networkId);
            mWifiManager.saveConfiguration();
        }
        if (Type == 2) // WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "\"".concat("\"");
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 1) // WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"".concat(Password).concat("\"");
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 0) // WIFICIPHER_WPA
        {
            config.preSharedKey = "\"".concat(Password).concat("\"");
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    private void savePrefs(String name, String pwd) {
        SharedPreferences sp = getSharedPreferences("account", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", name);
        editor.putString("pwd", pwd);
        editor.commit();
    }

    private HashMap<String, String> getPrefs() {
        SharedPreferences sp = getSharedPreferences("account", MODE_PRIVATE);
        String name = sp.getString("name", "");
        String pwd = sp.getString("pwd", "");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", name);
        map.put("pwd", pwd);
        return map;
    }

    //解析json数据判断是否连接成功
    private boolean isConnSuccess(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("result");
            if (code == 0) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void toast(String msg) {

        Utils.Toast(rootView,msg);
    }

    private void log(String log) {
        Log.i(Utils.TAG, log);
    }
}
