package com.zff.xpanel.parser.ui;

import android.Manifest;
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

import com.zff.ftp.ZffFtpServer;
import com.zff.utils.DecompressJar;
import com.zff.utils.FileTools;
import com.zff.utils.NetworkTools;
import com.zff.utils.PermissionsChecker;
import com.zff.xpanel.parser.R;
import com.zff.xpanel.parser.cache.ProjectCache;
import com.zff.xpanel.parser.util.Constant;
import com.zff.xpanel.parser.util.GuiFileResolve;
import com.zff.xpanel.parser.util.Properties;

import java.io.File;

public class WelcomeActivity extends BaseActivity {

    private final String TAG = "WelcomeActivity";

    private static final int REQUEST_CODE = 200; // 请求码
    private final int MSG_WHAT_PROGRESS = 100;

    private ViewGroup mRootView;
    private ProgressBar mProgressBar;
    private TextView mTipsTv;

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    private int mProgress = 0;
    private boolean isStopProgress = false;
    //ftp相关的上传和加压缩还有解析gui文件是否成功
    private boolean isSuccessFtpUploadResolve = true;
    //异步任务是否完成
    private boolean isFinishAsyncTask = true;

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
            zffFtpServer = ((ZffFtpServer.MyBinder)binder).getService();
            zffFtpServer.setFtpCallback(new MyFtpServerCallback());
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            int arg1 = msg.arg1;
            switch (what){
                case MSG_WHAT_PROGRESS:
                    if(!isSuccessFtpUploadResolve){
                        return;
                    }
                    if(!isStopProgress && arg1 >= 0 && arg1 <= 100){
                        mProgressBar.setProgress(arg1);
                        delaySendProgressMsg();
                    }else if(arg1 >= 100){
                        if(!isFinishAsyncTask){
                            return;
                        }
                        //进入下一页面
                        intoThirdActivity();
                    }
                    break;
            }
        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void iniView() {
        mRootView = findViewById(R.id.welcome_root_layout);
        mProgressBar = findViewById(R.id.welcome_progress_bar);
        mTipsTv = findViewById(R.id.welcome_tip_tv);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        mRootView.setOnClickListener(myOnClickListener);
    }

    @Override
    protected void iniData() {
        mPermissionsChecker = new PermissionsChecker(this);
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }else {
            loadData();
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
            if(resultCode == PermissionsActivity.PERMISSIONS_DENIED){
                finish();
            }else if(resultCode == PermissionsActivity.PERMISSIONS_GRANTED){
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
    private void loadData(){
        String launcherPage = Properties.getInstant(this).getLauncherPageName();
        if(TextUtils.isEmpty(launcherPage)){
            isFinishAsyncTask = false;
            decompressZip();
        }
        startLoadProgress();
    }

    private void refreshTipTv(String message){
        mTipsTv.setText(message);
    }

    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.welcome_root_layout:
                    stopLoadProgress();
                    startFtpServer();
                    break;
            }
        }
    }

    private void intoThirdActivity(){
        Intent intent = new Intent(this, ThirdActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 开始加载进度条
     */
    private void startLoadProgress(){
        isStopProgress = false;
        mProgress = 0;
        delaySendProgressMsg();
    }
    private void stopLoadProgress(){
        isStopProgress = true;
        isFinishAsyncTask = false;
    }
    private void delaySendProgressMsg(){
        if(isStopProgress || mProgress > 120){
            return;
        }
        mProgress += 1;
        Message msg = new Message();
        msg.what = MSG_WHAT_PROGRESS;
        msg.arg1 = mProgress;
        mHandler.sendMessageDelayed(msg, 35);
    }

    private void startFtpServer(){
        String ip = NetworkTools.getIp(this);
        if(TextUtils.isEmpty(ip)){
            refreshTipTv("获取不到IP，请连接网络");
        }else{
            String str = "请在“我的电脑”或者IE浏览器上输入网址访问FTP服务\n" +
                    "ftp://"+ip+":2221\n" +
                    "账号:admin\n" +
                    "密码:123456";
            refreshTipTv(str);
        }

        openService();
    }

    private void openService(){
        Intent service = new Intent(this, ZffFtpServer.class);
        //startService(service);
        bindService(service, mConn, Context.BIND_AUTO_CREATE);
    }
    private void closeService(){
        if(zffFtpServer == null){
            return;
        }
        unbindService(mConn);
        //stopService(new Intent(this, ZffFtpServer.class));
    }
    /**
     * 删除工程目录下的文件
     * @return
     */
    private boolean deleteProjectDir(){
        File dir = new File(Constant.PROJECT_DIR);
        if(!dir.exists()){
            return true;
        }
        return FileTools.deleteDir(dir);
    }

    /**
     * 解压zip文件
     */
    private void decompressZip(){
        File ftpDir = new File(Constant.FTP_UPLOAD_DIR);
        if(ftpDir.exists()){
            File zipFile = FileTools.lookupFile(ftpDir, ".zip");
            if(zipFile != null){
                if(mDecompressJar == null){
                    mDecompressJar = new DecompressJar();
                }
                mDecompressJar.decompressJarTask(zipFile, Constant.DECOMPRESS_DIR, new MyDecompressAsyncCallback());
            }else{
                Log.i(TAG, "未发现zip压缩文件");
            }
        }else{
            Log.i(TAG, "ftp 目录不存在");
        }
    }

    /**
     * 解析gui文件
     */
    private void resolveGuiFile(){
        File dir = new File(Constant.PROJECT_DIR);
        File guiFile = FileTools.lookupFile(dir, ".gui");
        if(guiFile.exists()){
            Log.i(TAG, "正在解析 "+guiFile.getName());
            GuiFileResolve guiFileResolve = new GuiFileResolve();
            boolean success = guiFileResolve.resolveGuiFile(guiFile.getPath());
            if(success){
                isFinishAsyncTask = true;
                Log.i(TAG,guiFile.getName()+"解析完成");
            }else{
                Log.i(TAG,guiFile.getName()+"解析失败");
            }
        }else{
            Log.i(TAG,"没有发现 gui文件");
        }

    }

    private class MyDecompressAsyncCallback implements DecompressJar.AsyncCallback{

        @Override
        public void onDecompressProgress(String message) {
            Log.i(TAG, ""+message);
        }

        @Override
        public void onDecompressResult(String message) {
            Log.i(TAG, ""+message);
            resolveGuiFile();
        }
    }

    private class MyFtpServerCallback implements ZffFtpServer.FtpServerCallback{

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

}
