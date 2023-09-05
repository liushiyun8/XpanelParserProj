package com.zff.xpanel.parser.util;

import android.os.Message;
import android.util.Log;

import com.emp.xdcommon.android.log.LogUtils;
import com.emp.xdcommon.common.io.FileUtils;
import com.zff.utils.DownAndSaveUtils;
import com.zff.xpanel.parser.io.HandlerMsgConstant;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadUtils {
    public static final String TAG = "DownloadUtils";

    public static void downUIFile(String url) {
        OkHttpClient client = new OkHttpClient();
        //注册
        String regiterUrl = url + "?uid=" + DeviceUtil.getDeviceId();
        Request registerRequest = new Request.Builder()
                .url(regiterUrl)
                .get()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            try {
                client.newCall(registerRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        emitter.onNext(false);
                        emitter.onComplete();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        emitter.onNext(true);
                        emitter.onComplete();
                    }
                });
            } catch (Exception e) {
                emitter.onNext(false);
                emitter.onComplete();
            }
        })
                .map(aBoolean -> {
                    if (aBoolean) {
                        try {
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Log.d(TAG, "IOException: " + e);
                                    Message msg = Message.obtain();
                                    msg.what = HandlerMsgConstant.DOWNLAOD_RES;
                                    msg.arg1 = 1;
                                    EventBus.getDefault().post(msg);
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    String file = response.body().string();
                                    File projectDir = new File(Constant.PROJECT_DIR);
                                    if (!projectDir.exists()) {
                                        projectDir.mkdirs();
                                    }
                                    file=file.replace("\\","/");
                                    FileUtils.cleanDirectory(projectDir);
                                    FileUtils.writeStringToFile(new File(Constant.PROJECT_DIR + File.separator + "home.gui"), file);
                                    DownAndSaveUtils.parseAndsave(url, file, null);
                                    Properties.getInstant().clearLauncherPageName();
                                }
                            });
                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: " + e);
                            Message msg = Message.obtain();
                            msg.what = HandlerMsgConstant.DOWNLAOD_RES;
                            msg.arg1 = 1;
                            EventBus.getDefault().post(msg);
                        }
                    }
                    return aBoolean;
                })
                .subscribe(s -> {
                    LogUtils.e(TAG, "结果:" + s);
                });


    }




}
