package com.zff.utils;

import android.os.Message;
import android.util.Log;

import com.emp.xdcommon.common.io.FileUtils;
import com.zff.xpanel.parser.io.HandlerMsgConstant;
import com.zff.xpanel.parser.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownAndSaveUtils {

    public static void parseAndsave(String url,String code,IDownCall call){
        Set<String> imageSrc = getImageSrc(code);
        OkHttpClient okHttpClient = new OkHttpClient();
        int size = imageSrc.size();
        AtomicInteger total = new AtomicInteger(size);
        for (String src : imageSrc) {
            Log.e("df", src);
            Request request = new Request.Builder()
                    .url(url+"/"+encodeUrl(src))
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    if(tryCount-->=0){
//                        okHttpClient.newCall(call.request()).enqueue(this);
//                        return;
//                    }
                    Log.e("fail",e+"");
                    Message msg = Message.obtain();
                    msg.what= HandlerMsgConstant.DOWNLAOD_MSG;
                    msg.arg1=size;
                    msg.arg2=1;
                    msg.obj=call.request().url();
                    EventBus.getDefault().post(msg);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.e("success",response+"");
                    FileUtils.writeByteArrayToFile(new File(Constant.PROJECT_DIR+File.separator+src),response.body().bytes());
                    Log.e("success","writeByteArrayToFile:"+src);
                    Message msg = Message.obtain();
                    msg.what= HandlerMsgConstant.DOWNLAOD_MSG;
                    msg.arg1=size;
                    msg.arg2=0;
                    EventBus.getDefault().post(msg);
                }
            });
        }
    }

    public interface IDownCall{
        public void finish();
    }

    public static Set<String> getImageSrc(String htmlCode) {
        Set<String> imageSrcList = new HashSet<>();
        Pattern p = Pattern.compile("[^(|>]*\\.png|[^(|>]*\\.jpg|[^(|>]*\\.gif|[^\"]*\\.lua", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlCode);
        String quote = null;
        String src = null;
        while (m.find()) {
            quote = m.group();

            // src=https://sms.reyo.cn:443/temp/screenshot/zY9Ur-KcyY6-2fVB1-1FSH4.png
//            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("\\s+")[0] : m.group(2);
            src = quote;
            imageSrcList.add(src);

        }
        return imageSrcList;
    }

    private static String encodeUrl(String urlStr) {
        urlStr =urlStr.contains("%")?urlStr.replace("%", "%25"):urlStr;
        urlStr =urlStr.contains("+")?urlStr.replace("+", "%2B"):urlStr;
        urlStr =urlStr.contains(" ")?urlStr.replace(" ", "%20"):urlStr;
        urlStr =urlStr.contains("/")?urlStr.replace("/", "%2F"):urlStr;
        urlStr =urlStr.contains("?")?urlStr.replace("?", "%3F"):urlStr;
        urlStr =urlStr.contains("#")?urlStr.replace("#", "%23"):urlStr;
        urlStr =urlStr.contains("&")?urlStr.replace("&", "%26"):urlStr;
        urlStr =urlStr.contains("=")?urlStr.replace("=", "%3D"):urlStr;
        return urlStr;
    }
}
