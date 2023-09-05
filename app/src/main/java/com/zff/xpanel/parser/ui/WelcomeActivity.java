package com.zff.xpanel.parser.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emp.xdcommon.android.log.LogUtils;
import com.zff.ftp.ZffFtpServer;
import com.zff.utils.AssetUtils;
import com.zff.utils.DecompressJar;
import com.zff.utils.FileTools;
import com.zff.utils.NetworkTools;
import com.zff.utils.PermissionsChecker;
import com.zff.xpanel.parser.R;
import com.zff.xpanel.parser.cache.Layouts;
import com.zff.xpanel.parser.cache.Pages;
import com.zff.xpanel.parser.cache.ProjectCache;
import com.zff.xpanel.parser.cache.Subpages;
import com.zff.xpanel.parser.io.HandlerMsgConstant;
import com.zff.xpanel.parser.util.Constant;
import com.zff.xpanel.parser.util.DownloadUtils;
import com.zff.xpanel.parser.util.GuiFileResolve;
import com.zff.xpanel.parser.util.MicroXmlParser;
import com.zff.xpanel.parser.util.Properties;
import com.zff.xpanel.parser.util.PropertiesXmlParser;
import com.zff.xpanel.parser.util.ScriptXmlParser;
import com.zff.xpanel.parser.util.SharedPreferenceUtils;
import com.zff.xpanel.parser.util.SystemsXmlParser;
import com.zff.xpanel.parser.util.ThemeXmlParser;
import com.zff.xpanel.parser.view.inflater.Inflater;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "WelcomeActivity";

    private static final int REQUEST_CODE = 200; // 请求码
    private final int MSG_WHAT_PROGRESS = 100;

    private ViewGroup mRootView;
    private ProgressBar mProgressBar;
    private TextView mTipsTv;

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_BOOT_COMPLETED
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    private int mProgress = 0;
    private boolean isStopProgress = false;
    //ftp相关的上传和加压缩还有解析gui文件是否成功
    private boolean isSuccessFtpUploadResolve = true;
    //异步任务是否完成
    private boolean isFinishAsyncTask = false;

    private DecompressJar mDecompressJar;
    private ZffFtpServer zffFtpServer;

    private ServiceConnection mConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName comp) {
            // TODO Auto-generated method stub
            zffFtpServer.setFtpCallback(null);
            zffFtpServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName comp, IBinder binder) {
            // TODO Auto-generated method stub
            zffFtpServer = ((ZffFtpServer.MyBinder) binder).getService();
            zffFtpServer.setFtpCallback(new MyFtpServerCallback());
        }
    };

    int count = 0;
    int failCount = 0;
    StringBuilder failUrl = new StringBuilder();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message msg) {
        if (msg.what == HandlerMsgConstant.DOWNLAOD_MSG) {
            if (msg.arg2 == 0) {  //下载成功
                count++;
            } else {     //下载失败
                failCount++;
                failUrl.append(msg.obj).append("\n");
                mTipsTv.setText("下载图片失败" + failCount + "个,失败URL: " + failUrl.toString());
            }
            Log.i(TAG, "count: "+count+",all:"+msg.arg1);
            if (count >= msg.arg1) {
                parseData();
            }
        } else if (msg.what == HandlerMsgConstant.DOWNLAOD_RES) {
            if (msg.arg1 == 1) {
                stopLoadProgress();
                mTipsTv.setText("下载失败，请检查下载地址是否正确...");
            } else {
                mTipsTv.setText("正在下载，请稍等...");
            }
        }
//        else if (msg.what == HandlerMsgConstant.REGISTER_RES) {
//            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("注册提示:").setMessage("此设备未注册,请先注册！")
//                    .create();
//            dialog.show();
//        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            int arg1 = msg.arg1;
            switch (what) {
                case MSG_WHAT_PROGRESS:
                    if (!isSuccessFtpUploadResolve) {
                        return;
                    }
                    if (!isStopProgress && arg1 >= 0 && arg1 <= 100) {
                        mProgressBar.setProgress(arg1);
                        delaySendProgressMsg();
                    } else if (arg1 >= 100) {
                        if (!isFinishAsyncTask || isStopProgress) {
                            return;
                        }
                        //进入下一页面
                        LogUtils.e(TAG, "进去了");
                        intoThirdActivity();
                    }
                    break;
            }
        }
    };
    private View set;
    private SharedPreferenceUtils sharedPreferenceUtils;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void iniView() {
        mRootView = findViewById(R.id.welcome_root_layout);
        mProgressBar = findViewById(R.id.welcome_progress_bar);
        mTipsTv = findViewById(R.id.welcome_tip_tv);
        set = findViewById(R.id.setBtn);

        mRootView.setOnClickListener(this);
        set.setOnClickListener(this);

        sharedPreferenceUtils = new SharedPreferenceUtils(this);
    }

    @Override
    protected void iniData() {
        mPermissionsChecker = new PermissionsChecker(this);
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        } else {
//            loadData();
        }
    }

    @Override
    protected void onStop() {
        stopLoadProgress();
        closeService();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE) {
            if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                finish();
            } else if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
                loadData();
            }
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    /**
     * 加载数据，真正数据的解析开始
     */
    private void loadData() {
        count = 0;
        failCount = 0;
        LogUtils.e(TAG, "loadData");
        mTipsTv.setText("正在加载，请稍等...");
        if (sharedPreferenceUtils.getHttpEnableToSp()) {
            String site = sharedPreferenceUtils.getSp("http");
            if (!TextUtils.isEmpty(site)) {
                DownloadUtils.downUIFile(site);
            }
        } else {
            parseData();


//            try {
//                AssetUtils.copyAssetFileToFile(this,Constant.FTP_UPLOAD_DIR+File.separator+"page.zip","page.zip");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            decompressZip()
        }
        startLoadProgress();
    }

    private void parseData() {
        mTipsTv.setText("正在解析文件，请稍等...");
        Log.i(TAG, "正在解析文件 ");
        String launcherPage = Properties.getInstant().getLauncherPageName();
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            boolean res;
            if (TextUtils.isEmpty(launcherPage)) {
                res = resolveGuiFile();
            } else res = true;
            Pages.getInstant().clear();
            Subpages.getInstant().clear();
            Layouts.getInstance().clear();
            emitter.onNext(res);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        Properties properties = new PropertiesXmlParser().parseAllProper();
                        LogUtils.e(TAG, "properties.isRegister:" + properties.isRegister());
//                        if(!properties.isRegister()){
//                            Message msg = new Message();
//                            msg.what = HandlerMsgConstant.REGISTER_RES;
//                            EventBus.getDefault().post(msg);
//                            return;
//                        }
                        new ThemeXmlParser().parseThemeAll();
                        new SystemsXmlParser().parseAllSystems();
                        new MicroXmlParser().parseAllMicros();
                        new ScriptXmlParser().parse();
                        Inflater inflater = new Inflater(WelcomeActivity.this);
                        inflater.setListner(new Inflater.IproListner() {
                            @Override
                            public void process(int pro) {
                                LogUtils.e("inflater", "处理进度%" + pro);
                            }

                            @Override
                            public void finished() {
                                LogUtils.e("inflater", "finished");
                                isFinishAsyncTask = true;
                                Message msg = new Message();
                                msg.what = MSG_WHAT_PROGRESS;
                                msg.arg1 = 100;
                                mHandler.sendMessageDelayed(msg, 35);
                            }
                        });
                        inflater.parseAllLayoutView(Constant.PAGES_DIR);
                    } else {
                        mTipsTv.setText("没有发现文件，正在解析默认配置文件");
                        try {
                            AssetUtils.copyAssetFileToFile(this, Constant.FTP_UPLOAD_DIR + File.separator + "page.zip", "page.zip");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        decompressZip();
                    }
                },error->{
                    LogUtils.e(TAG,"parse error:"+error.getMessage());
                });
    }

    private void refreshTipTv(String message) {
        mTipsTv.setText(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcome_root_layout:
                stopLoadProgress();
                enableSet();
//                if(!Properties.getInstant().isRegister()){
//                    Message msg = new Message();
//                    msg.what = HandlerMsgConstant.REGISTER_RES;
//                    EventBus.getDefault().post(msg);
//                    return;
//                }
//                    startFtpServer();
                break;
            case R.id.setBtn:
                startActivity(new Intent(this, ConnectActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void enableSet() {
        set.setVisibility(View.VISIBLE);
    }

    private void intoThirdActivity() {
        mHandler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(this, ThirdActivity.class);
        startActivity(intent);
//        finish();
    }

    /**
     * 开始加载进度条
     */
    private void startLoadProgress() {
        isStopProgress = false;
        mProgress = 0;
        delaySendProgressMsg();
    }

    private void stopLoadProgress() {
        isStopProgress = true;
        isFinishAsyncTask = false;
    }

    private void delaySendProgressMsg() {
        if (isStopProgress || mProgress > 120) {
            return;
        }
        mProgress += 1;
        Message msg = new Message();
        msg.what = MSG_WHAT_PROGRESS;
        msg.arg1 = mProgress;
        mHandler.sendMessageDelayed(msg, 35);
    }

    private void startFtpServer() {
        String ip = NetworkTools.getIp(this);
        if (TextUtils.isEmpty(ip)) {
            refreshTipTv("获取不到IP，请连接网络");
        } else {
            String str = "请在“我的电脑”或者IE浏览器上输入网址访问FTP服务\n" +
                    "ftp://" + ip + ":2221\n" +
                    "账号:admin\n" +
                    "密码:123456";
            refreshTipTv(str);
        }

        openService();
    }

    private void openService() {
        Intent service = new Intent(this, ZffFtpServer.class);
        //startService(service);
        bindService(service, mConn, Context.BIND_AUTO_CREATE);
    }

    private void closeService() {
        if (zffFtpServer == null) {
            return;
        }
        unbindService(mConn);
        //stopService(new Intent(this, ZffFtpServer.class));
    }

    /**
     * 删除工程目录下的文件
     *
     * @return
     */
    private boolean deleteProjectDir() {
        File dir = new File(Constant.PROJECT_DIR);
        if (!dir.exists()) {
            return true;
        }
        return FileTools.deleteDir(dir);
    }

    /**
     * 解压zip文件
     */
    private void decompressZip() {
        File ftpDir = new File(Constant.FTP_UPLOAD_DIR);
        if (ftpDir.exists()) {
            File zipFile = FileTools.lookupFile(ftpDir, ".zip");
            if (zipFile != null) {
                if (mDecompressJar == null) {
                    mDecompressJar = new DecompressJar();
                }
                mDecompressJar.decompressJarTask(zipFile, Constant.DECOMPRESS_DIR, new MyDecompressAsyncCallback());
            } else {
                Log.i(TAG, "未发现zip压缩文件");
            }
        } else {
            Log.i(TAG, "ftp 目录不存在");
        }
    }

    /**
     * 解析gui文件
     */
    private boolean resolveGuiFile() {
        boolean res = false;
        File dir = new File(Constant.PROJECT_DIR);
        File guiFile = FileTools.lookupFile(dir, ".gui");
        if (guiFile != null && guiFile.exists()) {
            Log.i(TAG, "正在解析 " + guiFile.getName());
            GuiFileResolve guiFileResolve = new GuiFileResolve();
            boolean success = guiFileResolve.resolveGuiFile(guiFile.getPath());
            if (success) {
                Log.i(TAG, guiFile.getName() + "解析完成");

            } else {
                Log.i(TAG, guiFile.getName() + "解析失败");
            }
            res = success;
        } else {
            Log.i(TAG, "没有发现 gui文件");
        }
        return res;
    }

    private class MyDecompressAsyncCallback implements DecompressJar.AsyncCallback {

        @Override
        public void onDecompressProgress(String message) {
            Log.i(TAG, "" + message);
        }

        @Override
        public void onDecompressResult(String message) {
            Log.i(TAG, "" + message);
            parseData();
        }
    }

    private class MyFtpServerCallback implements ZffFtpServer.FtpServerCallback {

        @Override
        public void onUploadStart() {
            Log.i(TAG, "onUploadStart-->");
        }

        @Override
        public void onUploadEnd() {
            Log.i(TAG, "onUploadEnd-->");
            boolean success = deleteProjectDir();
            ProjectCache.clearCache();
            decompressZip();
            startLoadProgress();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
