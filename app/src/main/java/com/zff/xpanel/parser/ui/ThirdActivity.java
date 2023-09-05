package com.zff.xpanel.parser.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.emp.xdcommon.android.log.LogUtils;
import com.luajava.JavaFunction;
import com.luajava.LuaException;
import com.luajava.LuaState;
import com.zff.xpanel.parser.MyEvent;
import com.zff.xpanel.parser.R;
import com.zff.xpanel.parser.cache.Cmds;
import com.zff.xpanel.parser.cache.EventMsg;
import com.zff.xpanel.parser.cache.Layouts;
import com.zff.xpanel.parser.cache.Micros;
import com.zff.xpanel.parser.io.HandlerMsgConstant;
import com.zff.xpanel.parser.io.ReadDataWrapper;
import com.zff.xpanel.parser.io.WriteDataWrapper;
import com.zff.xpanel.parser.servers.ConnectionManager;
import com.zff.xpanel.parser.servers.SocketService;
import com.zff.xpanel.parser.util.Constant;
import com.zff.xpanel.parser.util.MyLog;
import com.zff.xpanel.parser.util.Properties;
import com.zff.xpanel.parser.util.ViewUtil;
import com.zff.xpanel.parser.view.IjkVideoView;
import com.zff.xpanel.parser.view.MyAbsoluteLayout;
import com.zff.xpanel.parser.view.ViewArgs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androlua.LuaContext;
import androlua.LuaDexLoader;
import androlua.LuaManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import pl.droidsonroids.gif.GifImageView;

public class ThirdActivity extends Activity implements LuaContext {

    private final String TAG = "ThirdActivity";

    private ImageView mStateImg;
    private TextView mStateTv;
    private TextView primptTv;
    private SlidingDrawer slidingDrawer;
    private RelativeLayout container;
    private AbsoluteLayout pageLayout;

    public static MyBtnOnClickListener myBtnOnClickListener;
    public static MyBtnOnTouchListener myBtnOnTouchListener;
    public static MySeekbarChangeListener mySeekbarChangeListener;
    public static MyOnEditorActionListener myOnEditorActionListener;
    public static final String pageName = "pagename";
    private Intent service;
    private String currentPageName;
    private HashMap<String, AbsoluteLayout> subLayouts = new HashMap<>();
    private AlertDialog dialog;
    private boolean NOCmd = false;
    private SoundPool soundpool;
    private int id;
    private LuaManager luaManager;
    private LuaState L;
    private LuaDexLoader luaDexLoader;


    private enum EventType {
        TOUCH_DOWN, TOUCH_UP, CLICK
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("温馨提示").setMessage("请确认是否退出程序还是进入参数设置?")
                .setNegativeButton("退出程序", (dialog, which) -> {
                    finish();
                    EventBus.getDefault().post(new MyEvent(MyEvent.MSG_FINISH));
                    System.exit(0);
                })
                .setPositiveButton("参数设置", (dialog, which) -> intoConnectActivity()).create();
        alertDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCmdEvent(EventMsg msg) {
        LogUtils.e(TAG, msg.toString());
        if (msg.type != 4) {
            ReadDataWrapper readDataWrapper = new ReadDataWrapper(true);
            readDataWrapper.setJoinNum(Integer.parseInt(msg.jid));
            switch (msg.type) {
                case 1:
                    readDataWrapper.setSignalType(ReadDataWrapper.SignalType.DIGIT);
                    readDataWrapper.setPressedBtn("1".equals(msg.value));
                    break;
                case 2:
                    readDataWrapper.setSignalType(ReadDataWrapper.SignalType.ANALOG);
                    readDataWrapper.setAnalogValue(Integer.parseInt(msg.value));
                    break;
                case 3:
                    readDataWrapper.setSignalType(ReadDataWrapper.SignalType.STRING);
                    readDataWrapper.setString(msg.value);
                    break;
            }
            handleData(readDataWrapper);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message msg) {
        LogUtils.e("hello", msg.what + "");
        if (HandlerMsgConstant.READ_MSG_WHAT == msg.what) {
            ReadDataWrapper rdw = (ReadDataWrapper) msg.obj;
            handleData(rdw);
//            ViewArgs.Tag tag = new ViewArgs.Tag();
//            tag.jId=String.valueOf(joinNum);
//            View view = container.findViewWithTag(tag);
//            Log.e("view","findview:"+view);
        }
        if (HandlerMsgConstant.SOCKET_CONNECTED_SUCCESS == msg.what) {
            updateConnectStateView("connected");
            updateConnectStateImg(getResources().getDrawable(R.drawable.round_green));
        } else if (HandlerMsgConstant.SOCKET_CONNECTED_FAIL == msg.what) {
            updateConnectStateView("connect fail");
            updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
        } else if (HandlerMsgConstant.SOCKET_CONNECT_UNKNOW_HOST == msg.what) {
            updateConnectStateView("connect unknow host");
            updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
        } else if (HandlerMsgConstant.SOCKET_CONNECT_TIMEOUT == msg.what) {
            updateConnectStateView("connect timeout");
            updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
        } else if (HandlerMsgConstant.READ_MSG_WHAT == msg.what) {
            ReadDataWrapper rdw = (ReadDataWrapper) msg.obj;
            int joinNum = rdw.getJoinNum();
        } else if (HandlerMsgConstant.READ_STOP_MSG_WHAT == msg.what) {
            updateConnectStateView("read stop, disconnected");
            updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
        } else if (HandlerMsgConstant.SOCKET_DISCONNECTED == msg.what) {
            updateConnectStateView("disconnected");
            updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
        } else if (msg.what == HandlerMsgConstant.REGISTER_RES) {
            dialog.show();
        }

    }

    private void handleData(ReadDataWrapper rdw) {
        handleViews(rdw, pageLayout, true);
        //处理其他界面的vies
        Observable.create((ObservableOnSubscribe<AbsoluteLayout>) emitter -> {
            ConcurrentHashMap<String, AbsoluteLayout> myLayouts = Layouts.getInstance().getMyLayouts();
            for (Map.Entry<String, AbsoluteLayout> entry : myLayouts.entrySet()) {
                if (!entry.getKey().equals(currentPageName)) {
                    emitter.onNext(entry.getValue());
                }
            }
            emitter.onComplete();
        }).subscribeOn(AndroidSchedulers.mainThread())
                .map(layout -> {
                    handleViews(rdw, layout, false);
                    return true;
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    private void handleViews(ReadDataWrapper rdw, ViewGroup layout, boolean isCurrent) {
        int joinNum = rdw.getJoinNum();
        Log.e("view", "joinNum:" + joinNum);
        Object laytag = layout.getTag();
        String pjId = "";
        if (laytag instanceof ViewArgs.Tag) {
            pjId = ((ViewArgs.Tag) laytag).jId;
        }
        if (pjId.equals(String.valueOf(joinNum)) && rdw.isPressedBtn() && rdw.getSignalType() == ReadDataWrapper.SignalType.DIGIT) {
            jumpPageLay((AbsoluteLayout) layout);
            return;
        }
//        ArrayList<View> views = new ArrayList<>();
        ArrayList<View> views = ViewUtil.findViewByTag(layout, String.valueOf(joinNum));
//        layout.findViewsWithText(views, String.valueOf(joinNum), View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
//        Log.e("view", "findview:" + views + ",size:" + views.size());
        LogUtils.e("view", "joinNum:" + joinNum + ",findview:" + views + ",size:" + views.size());
        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);
            Object tag = view.getTag();
            String jId = "";
            String sId = "";
            if (tag instanceof ViewArgs.Tag) {
                jId = ((ViewArgs.Tag) tag).jId;
                sId = ((ViewArgs.Tag) tag).sid;
            }

            if (view instanceof Button) {
                if (rdw.getSignalType() == ReadDataWrapper.SignalType.DIGIT) {
                    if (jId.equals(String.valueOf(joinNum))) {
                        view.setSelected(rdw.isPressedBtn());
                    }
                } else if (rdw.getSignalType() == ReadDataWrapper.SignalType.STRING) {
                    if (sId.equals(String.valueOf(joinNum))) {
                        ((Button) view).setText(rdw.getString());
                    }
                }
            } else if (view instanceof TextView) {
                if (jId.equals(String.valueOf(joinNum)) && rdw.getSignalType() == ReadDataWrapper.SignalType.STRING) {
                    ((TextView) view).setText(rdw.getString());
                }
            } else if (view instanceof ProgressBar) {
                if (jId.equals(String.valueOf(joinNum)) && rdw.getSignalType() == ReadDataWrapper.SignalType.ANALOG) {
                    ((ProgressBar) view).setProgress(rdw.getAnalogValue());
                }
            } else if (view instanceof ImageView) {
                if (jId.equals(String.valueOf(joinNum)) && rdw.getSignalType() == ReadDataWrapper.SignalType.STRING) {
                    view.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(Constant.IMG_RES_DIR + "/" + rdw.getString())));
                }
            } else if (view instanceof WebView) {
                if (sId.equals(String.valueOf(joinNum)) && rdw.getSignalType() == ReadDataWrapper.SignalType.DIGIT) {
                    ((WebView) view).reload();
                } else if (String.valueOf(joinNum).equals(jId) && rdw.getSignalType() == ReadDataWrapper.SignalType.STRING)
                    ((WebView) view).loadUrl(rdw.getString());
            } else if (view instanceof IjkVideoView) {
                if (String.valueOf(joinNum).equals(String.valueOf(jId)) && rdw.getSignalType() == ReadDataWrapper.SignalType.STRING) {
                    ((IjkVideoView) view).setVideoPath(rdw.getString());
                }
            } else if (view instanceof MyAbsoluteLayout) {
                LogUtils.e("TAGGGGGG1", "MyAbsoluteLayout:" + view + ",jId:" + jId);
                if (jId.equals(String.valueOf(joinNum)) && rdw.getSignalType() == ReadDataWrapper.SignalType.DIGIT) {
                    if (rdw.isPressedBtn()) {
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

//    private Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//        }
//
//    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate-->");
        if (Properties.getInstant().getMoshe() == 0) { //横屏模式
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else {                                   //竖屏模式
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        setContentView(R.layout.activity_third);
        GifImageView bgIv = findViewById(R.id.bgIv);
        File file = new File(Constant.IMG_RES_DIR + "/bg.gif");
        if (file.exists()) {
            bgIv.setVisibility(View.VISIBLE);
            bgIv.setImageURI(Uri.fromFile(file));
        }
        dialog = new AlertDialog.Builder(this).setTitle("系统提示:").setMessage("此设备未注册,请先注册！")
                .create();
        EventBus.getDefault().register(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        String mypageName = getIntent().getStringExtra(pageName);


        primptTv = (TextView) findViewById(R.id.textView);
        mStateImg = findViewById(R.id.state_img);
        mStateTv = (TextView) findViewById(R.id.state_tv);
        slidingDrawer = findViewById(R.id.sliding_drawer);
        container = findViewById(R.id.page_container);

        myBtnOnClickListener = new MyBtnOnClickListener();
        myBtnOnTouchListener = new MyBtnOnTouchListener();
        mySeekbarChangeListener = new MySeekbarChangeListener();
        myOnEditorActionListener = new MyOnEditorActionListener();

//        inflaterPage = new InflaterPage(ThirdActivity.this);
//        inflaterPage.setBtnOnClickListener(myBtnOnClickListener);
//        inflaterPage.setBtnTouchListener(myBtnOnTouchListener);
//        inflaterPage.setSeekbarChangeListener(mySeekbarChangeListener);
//        inflaterPage.setOnEditorActionListener(myOnEditorActionListener);

        String launcherPage;
        Log.e(TAG, "mypageName:" + mypageName);
        if (!TextUtils.isEmpty(mypageName)) {
            launcherPage = mypageName;
        } else {
            launcherPage = Properties.getInstant().getLauncherPageName();
        }
        Log.e(TAG, "launcherPage:" + launcherPage);
        if (!TextUtils.isEmpty(MyApp.getApp().getCurrentPage())) {
            launcherPage = MyApp.getApp().getCurrentPage();
        }
        loadData(launcherPage);
        obtainSocket();
        //初始化抽屉的数据
        setSlidingDrawerData();

        service = new Intent(this, SocketService.class);

        if (Build.VERSION.SDK_INT > 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频数量
            builder.setMaxStreams(5);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_SYSTEM);//STREAM_MUSIC
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            soundpool = builder.build();
        } else {
            soundpool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 0);
        }
        id = soundpool.load(this, R.raw.click, 1);
        initLua();
    }

    private void initLua() {
        if (!TextUtils.isEmpty(Properties.getInstant().getLuaScript())) {
            File luaFile = new File(Constant.PROJECT_DIR + File.separator + Properties.getInstant().getLuaScript());
            Log.e(TAG, "lua:" + luaFile.getAbsolutePath());
            if (luaFile.exists()) {
                Log.e(TAG, "lua:" + luaFile.getAbsolutePath());
                loadLuaContent(luaFile.getAbsolutePath());
            }
        }
    }

    public ArrayList<ClassLoader> getClassLoaders() {
        return luaDexLoader.getClassLoaders();
    }

    public HashMap<String, String> getLibrarys() {
        return luaDexLoader.getLibrarys();
    }

    private void initLuaEnv() {
        if (luaManager == null) {
            luaManager = LuaManager.getInstance();
            luaManager.init(this);
        }

        if (L == null) {
            L = luaManager.initLua(this);
            // 注册全局函数 print
        }


        if (luaDexLoader == null) {
            luaDexLoader = new LuaDexLoader();
            try {
                luaDexLoader.loadLibs();
            } catch (LuaException e) {
                e.printStackTrace();
            }
        }

        // copy assets files to sdcard

        try {
            String outputDir = luaManager.getLuaDir();
            String[] files = getAssets().list("lua");
            if (files == null) {
                return;
            }
            for (String file : files) {
                copyFile(getAssets().open("lua/" + file), outputDir + "/" + file);
            }
            luaManager.appendLuaDir(L, outputDir);
            JavaFunction print = new MyLog(L);
            print.register("log");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LuaException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(InputStream inStream, String newPath) throws IOException {
        int len;
        FileOutputStream fs = new FileOutputStream(newPath);
        byte[] buffer = new byte[4096];
        while ((len = inStream.read(buffer)) != -1) {
            fs.write(buffer, 0, len);
        }
        fs.flush();
        fs.close();
        inStream.close();
    }

    private void loadLuaContent(String url) {
        try {
            if (L == null) {
                initLuaEnv();
            }
            luaManager.doFile(L, url);
            luaManager.runFunc(L, "init");
        } catch (LuaException e) {
            e.printStackTrace();
        }
    }

    private void runLuaBtnUp(String j) {
        if (L != null) {
            luaManager.runFunc(L, "ButtonUpRsp", j);
        }
    }

    private void runLuaBtnDown(String j) {
        if (L != null) {
            luaManager.runFunc(L, "ButtonDwonRsp", j);
        }
    }

    private void runLuaSb(String j, int value) {
        if (L != null) {
            luaManager.runFunc(L, "SeekbarRsp", j, value);
        }
    }

    private void runLuaTxt(String j, String value) {
        if (L != null) {
            luaManager.runFunc(L, "AutoTouchRsp", j, value);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart-->");
        startService(service);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart-->");
        String pageName = MyApp.getApp().getCurrentPage();
        AbsoluteLayout pageLayoutnew = Layouts.getInstance().getLayout(pageName);
        if (pageLayoutnew == null) {
            finish();
            EventBus.getDefault().post(new MyEvent(MyEvent.MSG_FINISH));
            startActivity(new Intent(this, WelcomeActivity.class));
        }
//        if (Properties.getInstant().isResetLayoutMode) {
//            loadData(Properties.getInstant().getLauncherPageName());
//        }
        //可能在connectActivity中做了连接动作
        service = new Intent(this, SocketService.class);
        startService(service);
        obtainSocket();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ConnectionManager.getInstance().sendSyncPackat();
        Log.i(TAG, "onResume-->");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause-->");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop-->");
        stopService(service);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i(TAG, "onDestroy-->");
        if (L != null) {
            L.close();
        }
        container.removeAllViews();
        myBtnOnClickListener = null;
        myBtnOnTouchListener = null;
        mySeekbarChangeListener = null;
        myOnEditorActionListener = null;
        EventBus.getDefault().unregister(this);
        stopService(service);
//        inflaterPage.videoRelease();
    }


    private void loadData(final String pageName) {
        this.currentPageName = pageName;
        MyApp.getApp().setCurrentPage(currentPageName);
        AbsoluteLayout pageLayoutnew = Layouts.getInstance().getLayout(pageName);
        replaceView(pageLayoutnew);
        if (pageLayoutnew == null) {
            primptTv.setText("加载布局失败");
        } else {
            primptTv.setText("");
            primptTv.setVisibility(View.GONE);
        }
//        AsyncTask<String, Integer, Page> task = new AsyncTask<String, Integer, Page>() {
//
//            @Override
//            protected Page doInBackground(String... arg0) {
//                // TODO Auto-generated method stub
//                Properties properties = Properties.getInstant();
//                if (!properties.isSetDesignerSize()) {
//                    PropertiesXmlParser pxp = new PropertiesXmlParser();
//                    pxp.parse(Constant.PROPERTIES_DIR, Constant.PROPERTIES_FILE_NAME);
//                }
//
//
//                //launcherPage = "main";
//                Pages pages = Pages.getInstant();
//                Page page = null;
//                if (pages.containKey(pageName)) {//
//                    page = pages.getPage(pageName);
//                } else {
//                    PageXmlParser xmlPsr = new PageXmlParser();
//                    page = xmlPsr.parse(Constant.PAGES_DIR, pageName);
//                }
//                return page;
//            }
//
//            @Override
//            protected void onPostExecute(Page result) {
//                // TODO Auto-generated method stub
//                super.onPostExecute(result);
//
//
//                if (result == null) {
//                    primptTv.setText("加载布局失败");
//                    return;
//                } else {
//                    primptTv.setText("");
//                    primptTv.setVisibility(View.GONE);
//                }
//                page = result;
//                inflaterPageViewGroup(page);
//            }
//
//            @Override
//            protected void onPreExecute() {
//                // TODO Auto-generated method stub
//                super.onPreExecute();
//                primptTv.setText("正在加载布局");
//            }
//
//        };
//        task.execute(new String[]{});
    }

    private void replaceView(AbsoluteLayout pageLayoutnew) {
        if (pageLayoutnew != null) {
            container.removeAllViews();
            if (pageLayoutnew.getParent() != null)
                ((ViewGroup) pageLayoutnew.getParent()).removeView(pageLayoutnew);
            container.addView(pageLayoutnew);
            pageLayout = pageLayoutnew;
            handleSubLay();
        }
    }

    private void handleSubLay() {
        if (pageLayout != null) {
            subLayouts.clear();
            for (int i = 0; i < pageLayout.getChildCount(); i++) {
                View child = pageLayout.getChildAt(i);
                if (child instanceof MyAbsoluteLayout) {
//                    if(child.getTag()!=null)
                    subLayouts.put(((ViewArgs.Tag) child.getTag()).jId, (AbsoluteLayout) child);
                }
            }
        }
    }


    private void jumpPageLay(AbsoluteLayout layout) {
        //先移除上一次的pageLayout，再添加新的
        currentPageName = Layouts.getInstance().getNameByLayout(layout);
        MyApp.getApp().setCurrentPage(currentPageName);
        replaceView(layout);
    }

    /**
     * 更新连接状态的文字
     *
     * @param message
     */
    private void updateConnectStateView(String message) {
        mStateTv.setText(message);
    }

    /**
     * 更新连接状态的图标
     *
     * @param drawable
     */
    private void updateConnectStateImg(Drawable drawable) {
        mStateImg.setImageDrawable(drawable);
    }

    /**
     * 更新页面上view的数据
     *
     * @param view
     * @param jID
     * @param rdw
     */
    private void updateView(View view, String jID, ReadDataWrapper rdw) {
        ((TextView) view).setText(rdw.getString());
        saveViewStateToSQLite();
    }

    //保存view状态到数据库
    private void saveViewStateToSQLite() {

    }


    public class MyOnEditorActionListener implements OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                onEditDone((EditText) view);
                return true;
            }
            return false;
        }
    }

    public class MySeekbarChangeListener implements OnSeekBarChangeListener {

        boolean fromUser;
        long lastTime;

        @Override
        public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
            this.fromUser = fromUser;
//            if (fromUser) {
            if (seekbar.getTag() != null && System.currentTimeMillis() - lastTime >= 200) {
                ViewArgs.Tag argTag = (ViewArgs.Tag) seekbar.getTag();
                sendSeekbarProgressCmd(argTag.jId, progress);
                lastTime = System.currentTimeMillis();
                runLuaSb(argTag.jId, progress);
            }
//            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekbar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekbar) {
            int progress = seekbar.getProgress();
            ViewArgs.Tag argTag = (ViewArgs.Tag) seekbar.getTag();
            sendSeekbarProgressCmd(argTag.jId, progress);
            runLuaSb(argTag.jId, progress);
//            if (fromUser) {
//                if (seekbar.getTag() != null) {
//                    ViewArgs.Tag argTag = (ViewArgs.Tag) seekbar.getTag();
//                    sendSeekbarProgressCmd(argTag.jId, progress);
//                }
//            } else {
//                //竖直滑动条控件比较特殊
//                if (seekbar instanceof VerticalSeekBar) {
//                    ViewArgs.Tag argTag = (ViewArgs.Tag) seekbar.getTag();
//                    sendSeekbarProgressCmd(argTag.jId, progress);
//                }
//            }
        }

    }

    private class MyTextViewOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            onClickTextView((TextView) view);
        }
    }

    private class MyImgOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            onTouchImg(view, EventType.TOUCH_UP);
        }
    }

    public class MyBtnOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
//			if(view.getTag() != null){
//				ViewArgs.Tag argTag = (ViewArgs.Tag)view.getTag();
//				String jId = argTag.jId;
//				View vv = getChildView(container, jId);
//				if(vv == null){
//					String a = "0";
//				}
//			}
            //播放按键音
            int ret = soundpool.play(id, 1, 1, 0, 0, 1);
            Log.e(TAG, "播放声音:" + ret);
            btnEvent(view, EventType.CLICK);
        }
    }

    public class MyBtnOnTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    btnEvent(view, EventType.TOUCH_DOWN);
                    break;
                case MotionEvent.ACTION_UP:
                    btnEvent(view, EventType.TOUCH_UP);
                    break;
            }
            return false;
        }

    }

    /**
     * button事件，包括touch和click事件
     *
     * @param view  按钮
     * @param event 事件
     */
    private void btnEvent(View view, EventType event) {
        Properties properties = Properties.getInstant();
        LogUtils.e(TAG, "properties.isRegister:" + properties.isRegister());
        if (!properties.isRegister()) {
            Message msg = new Message();
            msg.what = HandlerMsgConstant.REGISTER_RES;
            EventBus.getDefault().post(msg);
            return;
        }
        if (view.getTag() != null) {
            ViewArgs.Tag argTag = (ViewArgs.Tag) view.getTag();
            if (argTag != null) {
                String flip = argTag.flip;
                String jId = argTag.jId;
                Log.i(TAG, "btnEvent-->" + event + ", jId=" + jId + ", flip=" + flip);

                //如果有效则表示要打开下一个主页，否则不打开主页
                if (ViewArgs.isValiableFlip(flip)) {
                    if (event == EventType.CLICK) {
                        loadData(flip);
                    }
                }
                if (event == EventType.TOUCH_DOWN) {
                    excuteCmd(argTag, true);
                    runLuaBtnDown(argTag.jId);
                } else if (event == EventType.TOUCH_UP) {
                    excuteCmd(argTag, false);
                    runLuaBtnUp(argTag.jId);
                }
                //jId=0表示没有设置jId
                if ("0".equals(jId)) {
                    Log.i(TAG, "btnEvent-->jId=0, 表示该view没有设置jId");
                } else {
                    if (event == EventType.CLICK) {
                        ConcurrentHashMap<View, String[]> liandongMap = Layouts.getInstance().getLiandongMap();
                        for (Map.Entry<View, String[]> entry : liandongMap.entrySet()) {
                            String[] value = entry.getValue();
                            LogUtils.e(TAG, Arrays.toString(value));
                            for (int i = 0; i < value.length; i++) {
                                if (jId.equals(value[i])) {
                                    View key = entry.getKey();
                                    if (key instanceof WebView) {
                                        WebView webView = (WebView) key;
                                        switch (i) {
                                            case 0:
                                                webView.goBack();
                                                break;
                                            case 1:
                                                webView.goForward();
                                                break;
                                            case 2:
                                                webView.reload();
                                                break;
                                            case 3:
                                                webView.stopLoading();
                                                break;
                                        }
                                    } else if (key instanceof IjkVideoView) {
                                        IjkVideoView videoView = (IjkVideoView) key;
                                        switch (i) {
                                            case 0:
                                                videoView.start();
                                                break;
                                            case 1:
                                                videoView.pause();
                                                break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }

//                        AbsoluteLayout sublay = subLayouts.get(jId);
//                        if (sublay != null) {
//                            if (event == EventType.CLICK) {
//                                for (AbsoluteLayout sub : subLayouts.values()) {
//                                    if (sub != sublay && sub.getVisibility() != View.GONE)
//                                        sub.setVisibility(View.GONE);
//                                }
//                                sublay.setVisibility(View.VISIBLE);
//                            }
//                        }

                    if (event == EventType.TOUCH_DOWN) {
                        sendPressCmd(jId);
                    } else if (event == EventType.TOUCH_UP) {
                        sendUpCmd(jId);
                    }
                }
            }
        }
    }

    private void excuteCmd(ViewArgs.Tag argTag, boolean isDown) {
        if (NOCmd) {
            return;
        }
        if (argTag.cmd != null && argTag.micro == null) {
            if (Cmds.getInstance().getCmd(argTag.cmd) != null) {
                if (isDown)
                    Cmds.getInstance().getCmd(argTag.cmd).excDown();
                else Cmds.getInstance().getCmd(argTag.cmd).excUp();
            }
        }
        if (argTag.micro != null) {
            LogUtils.e(TAG, "micro:" + argTag.micro);
            Micros.MICRO micro = Micros.getInstance().getMicro(argTag.micro);
            if (micro != null) {
                List<Micros.Command> commands = micro.getCommands();
                if (commands != null) {
                    for (int i = 0; i < commands.size(); i++) {
                        LogUtils.e(TAG, "comand:" + commands.get(i));
                        if (isDown)
                            commands.get(i).excDown();
                        else commands.get(i).excUp();
                    }
                }
            }
        }
    }

    private void onTouchImg(View view, EventType event) {
        if (view.getTag() != null) {
            ViewArgs.Tag argTag = (ViewArgs.Tag) view.getTag();
            String jId = argTag.jId;
            if (event == EventType.TOUCH_DOWN) {
                sendPressCmd(jId);
            } else if (event == EventType.TOUCH_UP) {
                sendUpCmd(jId);
            }
        }
    }

    private void onClickTextView(TextView view) {
        if (view.getTag() != null) {
            ViewArgs.Tag argTag = (ViewArgs.Tag) view.getTag();
            String jId = argTag.jId;
            String message = view.getText().toString();
            sendString(jId, message);
        }
    }

    private void onEditDone(EditText view) {
        if (view.getTag() != null) {
            ViewArgs.Tag argTag = (ViewArgs.Tag) view.getTag();
            String jId = argTag.jId;
            String message = view.getEditableText().toString();
            if (!TextUtils.isEmpty(message))
                sendString(jId, message);
            runLuaTxt(argTag.jId, message);
        }
    }

    /**
     * 移除subpage布局
     *
     * @param jID
     * @return true:移除成功
     */
    private boolean removeSubpageLayout(String jID, AbsoluteLayout subpageLayout) {
        if (pageLayout != null && subpageLayout != null) {
            pageLayout.removeView(subpageLayout);
            return true;
        }
        return false;
    }

    /**
     * 发送手指按下命令
     *
     * @param jId
     */
    private void sendPressCmd(String jId) {
        int id = 0;
        try {
            id = Integer.parseInt(jId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        byte[] bbs = WriteDataWrapper.packWriteDigitDataPress(id);
        sendData(bbs);
    }

    /**
     * 发送手指抬起命令
     *
     * @param jId
     */
    private void sendUpCmd(String jId) {
        int id = 0;
        try {
            id = Integer.parseInt(jId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        byte[] bbs = WriteDataWrapper.packWriteDigitDataRelease(id);
        sendData(bbs);
    }

    /**
     * 发送拖动条进度的命令
     */
    private void sendSeekbarProgressCmd(String jId, int progress) {
        int id = 0;
        try {
            id = Integer.parseInt(jId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        byte[] bbs = WriteDataWrapper.packWriteAnalogData(id, progress);
        sendData(bbs);
    }

    /**
     * 发送字符串
     *
     * @param jId
     * @param message
     */
    private void sendString(String jId, String message) {
        int id = 0;
        try {
            id = Integer.parseInt(jId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        byte[] bbs = WriteDataWrapper.packWriteStringData(id, message);
        sendData(bbs);
    }

    //发送数据
    private void sendData(byte[] bytes) {
        ConnectionManager.getInstance().writeToServer(bytes);
//		ConnectManager.getInstance().writeDate(bytes);
    }

    /**
     * 获取socket连接及读写线程
     */
//	private void obtainSocket(){
//		if(ConnectManager.getInstance().isConnected()){
//			updateConnectStateView("connected");
//			updateConnectStateImg(getResources().getDrawable(R.drawable.round_green));
//		}else {
//			updateConnectStateView("disconnected");
//			updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
//		}
//	}

    /**
     * 获取socket连接及读写线程
     */
    private void obtainSocket() {
        if (ConnectionManager.getInstance().isConnnected()) {
            updateConnectStateView("connected");
            updateConnectStateImg(getResources().getDrawable(R.drawable.round_green));
        } else {
            updateConnectStateView("disconnected");
            updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
        }
    }

    //进入setting页面
    private void intoSeettingActivity() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    //进入tcp连接页面
    private void intoConnectActivity() {
        startActivity(new Intent(this, ConnectActivity.class));
    }

    private void setSlidingDrawerData() {
        ImageView handleImgView = (ImageView) slidingDrawer.getHandle();
        View contentView = slidingDrawer.getContent();
        RecyclerView recyclerView = contentView.findViewById(R.id.sliding_drawer_recycler_view);
        MySlidingDrawerAdapter adapter = new MySlidingDrawerAdapter(this, iniSlidingDrawerData());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private List<String> iniSlidingDrawerData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("设置");
//        list.add("链接");
//        list.add("其他");
        return list;
    }

    //抽屉列表item点击事件
    private void slidingDrawerItemClick(int position, String content) {
        switch (position) {
            case 0:
                intoConnectActivity();
                break;
            case 1:
                intoSeettingActivity();
                break;
            case 2:
                break;
        }
    }

    private class MySlidingDrawerAdapter extends RecyclerView.Adapter<SlidingDrawerVh> {

        private Context mContext;
        private List<String> mListData;

        public MySlidingDrawerAdapter(Context context, List<String> data) {
            this.mContext = context;
            this.mListData = data;
        }

        @NonNull
        @Override
        public SlidingDrawerVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_sliding_drawer, viewGroup, false);
            return new SlidingDrawerVh(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SlidingDrawerVh slidingDrawerVh, final int position) {
            slidingDrawerVh.mNameTv.setText(mListData.get(position));
            slidingDrawerVh.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    slidingDrawerItemClick(position, mListData.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mListData == null ? 0 : mListData.size();
        }
    }

    private class SlidingDrawerVh extends RecyclerView.ViewHolder {

        private TextView mNameTv;

        public SlidingDrawerVh(@NonNull View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.name_tv);
        }
    }

    private void testAdd() {
//		pageLayout.addView(null, 0);
//		pageLayout.removeViewAt(0);
    }

    public void Set(int type, int jid, Object value) {
        EventMsg eventMsg = new EventMsg();
        eventMsg.what = EventMsg.CMDMSG;
        eventMsg.type = type;
        eventMsg.jid = String.valueOf(jid);
        eventMsg.value = value.toString();
        onCmdEvent(eventMsg);
//        EventBus.getDefault().post(eventMsg);
    }

    public Object Get(int type, int jid) {
        ConcurrentHashMap<String, AbsoluteLayout> myLayouts = Layouts.getInstance().getMyLayouts();
        for (Map.Entry<String, AbsoluteLayout> entry : myLayouts.entrySet()) {
            ArrayList<View> views = ViewUtil.findViewByTag(type, entry.getValue(), String.valueOf(jid));
            if (views.size() > 0) {
                View view = views.get(0);
                if (view instanceof Button) {
                    return view.isSelected();
                } else if (view instanceof ProgressBar) {
                    return ((ProgressBar) view).getProgress();
                } else if (view instanceof TextView) {
                    return ((TextView) view).getText().toString();
                }
            }
        }
        return null;
    }

}
