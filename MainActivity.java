package com.sima.infdefense;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity implements android.webkit.ValueCallback<String>, View.OnApplyWindowInsetsListener {
  public android.view.WindowInsets onApplyWindowInsets(View v, android.view.WindowInsets in){
    float top=0;
    if(Build.VERSION.SDK_INT>=28&&in.getDisplayCutout()!=null)top=in.getDisplayCutout().getSafeInsetTop();
    GameStore.insetTop=top/getResources().getDisplayMetrics().density;
    if(web!=null)web.evaluateJavascript("document.documentElement.style.setProperty('--sat','"+GameStore.insetTop+"px')",null);
    return v.onApplyWindowInsets(in);
  }
  public void onReceiveValue(String v){ if(v!=null&&v.contains("menu")) moveTaskToBack(true); }
  private WebView web;
  @Override protected void onCreate(Bundle b){
    super.onCreate(b);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    if(Build.VERSION.SDK_INT>=28){getWindow().getAttributes().layoutInDisplayCutoutMode=WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;}
    GameStore.vib=(android.os.Vibrator)getSystemService(VIBRATOR_SERVICE);
    GameStore.clip=(android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
    web=new WebView(this);
    web.setBackgroundColor(Color.rgb(10,10,10));
    WebSettings s=web.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setAllowFileAccess(true);
    s.setMediaPlaybackRequiresUserGesture(false);
    web.setOverScrollMode(View.OVER_SCROLL_NEVER);
    web.setWebViewClient(new WebViewClient());
    web.addJavascriptInterface(new GameStore(getSharedPreferences("game",MODE_PRIVATE)),"AndroidStore");
    web.setOnApplyWindowInsetsListener(this);
    setContentView(web);
    hideBars();
    web.loadUrl("file:///android_asset/index.html");
  }
  private void hideBars(){
    web.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }
  @Override public void onWindowFocusChanged(boolean f){super.onWindowFocusChanged(f);if(f)hideBars();}
  @Override protected void onPause(){web.evaluateJavascript("document.getElementById('bPause')&&window.__dbg&&__dbg.run&&!__dbg.run.over&&!document.getElementById('ov').classList.contains('on')&&document.getElementById('bPause').click()",null);web.onPause();super.onPause();}
  @Override protected void onResume(){super.onResume();web.onResume();hideBars();}
  @Override public void onBackPressed(){
    web.evaluateJavascript("(function(){var o=document.getElementById('ov');if(o.classList.contains('on')){var b=o.querySelector('[data-m=resume]');if(b){b.click();return 'ok'}return 'menu'}var p=document.getElementById('bPause');if(window.__dbg&&__dbg.run&&!__dbg.run.over){p.click();return 'ok'}return 'menu'})()",
      this);
  }
}
