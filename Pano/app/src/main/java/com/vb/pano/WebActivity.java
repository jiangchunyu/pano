package com.vb.pano;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.vb.pano.util.Utils;


public class WebActivity extends Activity {

    //TAG
    public static final String TAG="vbpano";
    //浏览器控件
    private WebView webView;
    private Button backBtn;
    private TextView titleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initTopBar();
        String panoId=getIntent().getStringExtra("panoId");
        boolean upload=getIntent().getBooleanExtra("upload",true);
        String url = Utils.getPanoUrl(upload)+panoId;
        //String url="http://wap.visualbusiness.cn/pano/guestpano/index.html?panoId=42A64C6257BE46D9964112C3866F8E0F";
         Utils.log(url);
        initView(url);
    }
    private void initTopBar()
    {
        backBtn=(Button)findViewById(R.id.backBtn);
        titleView=(TextView)findViewById(R.id.title);
        String title=getIntent().getStringExtra("name");
        if(TextUtils.isEmpty(title))
        {
            titleView.setText(title);
        }
        Drawable drawable=getResources().getDrawable(R.drawable.backbtn);
        drawable.setBounds(0,0,40,60);
        backBtn.setCompoundDrawables(drawable,null,null,null);
        backBtn.setCompoundDrawablePadding(-100);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView(String url)
    {
        webView=(WebView)findViewById(R.id.webview);
        WebSettings webSettings=webView.getSettings();
       // webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
      //  webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.i(TAG, "onProgressChanged: " + newProgress);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(webView.canGoBack())
            {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
