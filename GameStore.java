package com.sima.infdefense;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.webkit.JavascriptInterface;
public class GameStore {
  public static float insetTop=0;
  public static Vibrator vib;
  public static ClipboardManager clip;
  private final SharedPreferences p;
  public GameStore(SharedPreferences p){this.p=p;}
  @JavascriptInterface public float insetTop(){return insetTop;}
  @JavascriptInterface public String get(String k){return p.getString(k,null);}
  @JavascriptInterface public void set(String k,String v){p.edit().putString(k,v).commit();}
  @JavascriptInterface public void del(String k){p.edit().remove(k).commit();}
  @JavascriptInterface public void copy(String t){try{if(clip!=null)clip.setPrimaryClip(ClipData.newPlainText("save",t));}catch(Exception e){}}
  private static long[] pat(String k){
    if(k.equals("hit"))return new long[]{0,40};
    if(k.equals("boss"))return new long[]{0,60,80,60,80,140};
    if(k.equals("win"))return new long[]{0,30,60,30,60,30,60,90};
    if(k.equals("over"))return new long[]{0,300};
    if(k.equals("heavy"))return new long[]{0,35};
    if(k.equals("double"))return new long[]{0,18,60,18};
    if(k.equals("click"))return new long[]{0,20};
    return new long[]{0,12};
  }
  @JavascriptInterface public void haptic(String k,int strength){
    if(vib==null||strength<=0)return;
    try{
      if(Build.VERSION.SDK_INT>=29&&strength>=1){
        int e=-1;
        if(k.equals("tick"))e=VibrationEffect.EFFECT_TICK;
        else if(k.equals("click"))e=VibrationEffect.EFFECT_CLICK;
        else if(k.equals("heavy"))e=VibrationEffect.EFFECT_HEAVY_CLICK;
        else if(k.equals("double"))e=VibrationEffect.EFFECT_DOUBLE_CLICK;
        if(e>=0){vib.vibrate(VibrationEffect.createPredefined(e));return;}
      }
      long[] t=pat(k);
      if(strength==1)for(int i=1;i<t.length;i+=2)t[i]=Math.max(8,t[i]*6/10);
      if(Build.VERSION.SDK_INT>=26){
        int[] a=new int[t.length];
        for(int i=0;i<t.length;i++)a[i]=(i%2==1)?(strength==1?90:220):0;
        if(vib.hasAmplitudeControl())vib.vibrate(VibrationEffect.createWaveform(t,a,-1));
        else vib.vibrate(VibrationEffect.createWaveform(t,-1));
      }else vib.vibrate(t,-1);
    }catch(Exception x){}
  }
}
