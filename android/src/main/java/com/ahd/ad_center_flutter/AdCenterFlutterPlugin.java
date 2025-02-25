package com.ahd.ad_center_flutter;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ahd.ad_center_flutter.Ads.TTAd.TTAdCenter;
import com.ahd.ad_center_flutter.Ads.TTAd.TTBannerViewFactory;
import com.ahd.ad_center_flutter.Ads.TTAd.TTNativeAdViewFactory;
import com.ahd.ad_center_flutter.Ads.TTAd.TTSplashAdViewFactory;
import com.ahd.ad_center_flutter.OpenListener.PlayAdListener;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** AdCenterFlutterPlugin */
public class AdCenterFlutterPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Activity activity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "ad_center_flutter");
    channel.setMethodCallHandler(this);
    flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(
            "com.ahd.TTNativeView", new TTNativeAdViewFactory(flutterPluginBinding.getBinaryMessenger()));
    flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(
            "com.ahd.TTSplashView", new TTSplashAdViewFactory(flutterPluginBinding.getBinaryMessenger()));
    flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(
            "com.ahd.TTBannerView", new TTBannerViewFactory(flutterPluginBinding.getBinaryMessenger()));
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      case "init":
        initAdCenter(call, result);
        break;
      case "display":
        displayAd(call, result);
        break;
      case "preLoadSplash":
        preLoadSplash(call, result);
        break;
      case "preLoadNative":
        preLoadNative(call);
        result.success(true);
        break;
      case "preLoadBanner":
        preLoadBanner(call);
        result.success(true);
        break;
      case "destroy":
        AdCenter.getInstance().onDestroy();
        result.success(true);
        break;
      case "setUserId":
        setUserId(call, result);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  private void setUserId(MethodCall call, Result result) {
    String userId = call.argument("userId");
    AdCenter.getInstance().setUserId(userId);
    result.success(true);
  }

  private void initAdCenter(MethodCall call, Result result) {
    String appName = call.argument("appName");
    String pangolinAndroidAppId = call.argument("pangolinAndroidAppId");
    String pangolinRewardAndroidId = call.argument("pangolinRewardAndroidId");
    String tencentAndroidAppId = call.argument("tencentAndroidAppId");
    String tencentRewardAndroidId = call.argument("tencentRewardAndroidId");
    String ksAndroidAppId = call.argument("ksAndroidAppId");
    String ksRewardAndroidId = call.argument("ksRewardAndroidId");
    //默认渠道号：NORMAL:CSJ
    String channelAndroid = call.argument("channel");
    String appIdAndroid = call.argument("appId");
    String userId = call.argument("userId");
    Boolean _userProMore = call.argument("userProMore");
    String proMoreId = call.argument("proMoreId");
    if (proMoreId == null) {
      proMoreId = "";
    }
    String proMoreJiLiId = call.argument("proMoreJiLiId");
    if (proMoreJiLiId == null) {
      proMoreJiLiId = "";
    }
    boolean userProMore = false;
    if (_userProMore != null) {
      userProMore = _userProMore;
    }

    AdCenter.APPNAME = appName;
    AdCenter.TOUTIAOCATID = pangolinAndroidAppId;
    AdCenter.JILICATID = pangolinRewardAndroidId;
    AdCenter.APPCATID = tencentAndroidAppId;
    AdCenter.EDITPOSID = tencentRewardAndroidId;
    AdCenter.KUAISHOUCATID = ksAndroidAppId;
    AdCenter.KUAISHOUPOSID = ksRewardAndroidId;
    AdCenter.ProMoreId = proMoreId;
    AdCenter.ProMoreJiLiId = proMoreJiLiId;

    AdCenter.getInstance().initAd(activity, channelAndroid, appIdAndroid, userId, userProMore, result);
  }

  private void preLoadSplash(MethodCall call, Result result) {
    String codeId = call.argument("androidCodeId");
    Boolean userGroMore = call.argument("userGroMore");
    boolean ugm = false;
    if (userGroMore != null) {
      ugm = userGroMore;
    }
    TTAdCenter.getInstance().preLoadSplashAd(codeId, ugm, result);
  }

  private void preLoadBanner(MethodCall call) {
    String codeId = call.argument("androidCodeId");
    Double width = (Double) call.argument("width");
    Double height = (Double) call.argument("height");
    float fWidth = 640;
    float fHeight = 70;
    if (width != null) {
      fWidth = width.floatValue();
    }
    if (height != null) {
      fHeight = height.floatValue();
    }
    TTAdCenter.getInstance().preLoadBannerAdView(codeId, fWidth, fHeight);
  }

  private void preLoadNative(MethodCall call) {
    String codeId = call.argument("androidCodeId");
    Double width = (Double) call.argument("width");
    Double height = (Double) call.argument("height");
    Integer adType = call.argument("adType");
    Boolean useGroMore = call.argument("useGroMore");
    float fWidth = 640;
    float fHeight = 70;
    if (width != null) {
      fWidth = width.floatValue();
    }
    if (height != null) {
      fHeight = height.floatValue();
    }
    int type = 0;
    if (adType != null) {
      type = adType;
    }
    boolean ugm = false;
    if (useGroMore != null) {
      ugm = useGroMore;
    }
    TTAdCenter.getInstance().preLoadNativeAdView(codeId, fWidth, fHeight, type, ugm);
  }

  private boolean isResultUsed = false;

  private void displayAd(MethodCall call, final Result result) {
    isResultUsed = false;
    String functionId = call.argument("functionId");
    AdCenter.getInstance().displayAd(functionId, new PlayAdListener() {
      @Override
      public void onSuccess(final boolean isClick) {
        Log.e("onSuccess:", "广告播放成功");
        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if (isResultUsed) {
              return;
            }
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", "success");
            resultMap.put("message", "广告播放成功");
            resultMap.put("adClick", isClick);
            result.success(resultMap);
            isResultUsed = true;
          }
        });
      }

      @Override
      public void onFailed(final int errorCode, final String message) {
        Log.e("errorCode:" + errorCode, message);
        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if (isResultUsed) {
              return;
            }
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", "error");
            resultMap.put("errorCode", errorCode);
            resultMap.put("message", message);
            result.success(resultMap);
            isResultUsed = true;
          }
        });
      }
    });
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }
}
