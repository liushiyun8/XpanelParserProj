package com.zff.xpanel.parser.ui;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zff.xpanel.parser.R;
import com.zff.xpanel.parser.io.ConnectManager;
import com.zff.xpanel.parser.io.ConnectThread;
import com.zff.xpanel.parser.io.HandlerMsgConstant;
import com.zff.xpanel.parser.io.ReadDataWrapper;
import com.zff.xpanel.parser.io.ReadThread;
import com.zff.xpanel.parser.io.WriteDataWrapper;
import com.zff.xpanel.parser.io.WriteThread;
import com.zff.xpanel.parser.io.ReadDataWrapper.SignalType;
import com.zff.xpanel.parser.util.PageXmlParser;
import com.zff.xpanel.parser.util.Properties;
import com.zff.xpanel.parser.util.PropertiesXmlParser;
import com.zff.xpanel.parser.util.Constant;
import com.zff.xpanel.parser.view.inflater.InflaterPage;
import com.zff.xpanel.parser.view.Page;
import com.zff.xpanel.parser.view.Page.LinkageEvent;
import com.zff.xpanel.parser.view.Subpage;
import com.zff.xpanel.parser.view.ViewArgs;
import com.zff.xpanel.parser.cache.Pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TextView;

import org.videolan.vlc.widget.VerticalSeekBar;

public class ThirdActivity extends Activity{

	private final String TAG = "ThirdActivity";

	private ImageView mStateImg;
	private TextView mStateTv;
	private TextView primptTv;
	private SlidingDrawer slidingDrawer;
	private AbsoluteLayout container;
	private AbsoluteLayout pageLayout;


	private int posContainerOfPage = 0;
	private boolean isAddSubpage = false;
	
	private InflaterPage inflaterPage;
	private Page page;
	
	private MyBtnOnClickListener myBtnOnClickListener;
	private MyBtnOnTouchListener myBtnOnTouchListener;
	private MySeekbarChangeListener mySeekbarChangeListener;
	private MyOnEditorActionListener myOnEditorActionListener;


	private enum EventType{
		TOUCH_DOWN, TOUCH_UP, CLICK
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(HandlerMsgConstant.SOCKET_CONNECTED_SUCCESS == msg.what){
				updateConnectStateView("connected");
				updateConnectStateImg(getResources().getDrawable(R.drawable.round_green));
			}else if(HandlerMsgConstant.SOCKET_CONNECTED_FAIL == msg.what){
				updateConnectStateView("connect fail");
				updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
			}else if(HandlerMsgConstant.SOCKET_CONNECT_UNKNOW_HOST == msg.what){
				updateConnectStateView("connect unknow host");
				updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
			}else if(HandlerMsgConstant.SOCKET_CONNECT_TIMEOUT == msg.what){
				updateConnectStateView("connect timeout");
				updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
			}else if(HandlerMsgConstant.READ_MSG_WHAT == msg.what){
				ReadDataWrapper rdw = (ReadDataWrapper)msg.obj;
				int joinNum = rdw.getJoinNum();
			}else if(HandlerMsgConstant.READ_STOP_MSG_WHAT == msg.what){
				updateConnectStateView("read stop, disconnected");
				updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
			}else if(HandlerMsgConstant.SOCKET_DISCONNECTED == msg.what){
				updateConnectStateView("disconnected");
				updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate-->");
		setContentView(R.layout.activity_third);
		
		primptTv = (TextView)findViewById(R.id.textView);
		mStateImg = findViewById(R.id.state_img);
		mStateTv = (TextView)findViewById(R.id.state_tv);
		slidingDrawer = findViewById(R.id.sliding_drawer);
		container = (AbsoluteLayout)findViewById(R.id.page_container);
		
		myBtnOnClickListener = new MyBtnOnClickListener();
		myBtnOnTouchListener = new MyBtnOnTouchListener();
		mySeekbarChangeListener = new MySeekbarChangeListener();
		myOnEditorActionListener = new MyOnEditorActionListener();
		
		inflaterPage = new InflaterPage(ThirdActivity.this);
		inflaterPage.setBtnOnClickListener(myBtnOnClickListener);
		inflaterPage.setBtnTouchListener(myBtnOnTouchListener);
		inflaterPage.setSeekbarChangeListener(mySeekbarChangeListener);
		inflaterPage.setOnEditorActionListener(myOnEditorActionListener);
		
		String launcherPage = Properties.getInstant().getLauncherPageName();
		loadData(launcherPage);

		obtainSocket();
		//初始化抽屉的数据
		setSlidingDrawerData();

	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart-->");
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(TAG, "onRestart-->");
		if(Properties.getInstant().isResetLayoutMode){
			loadData(Properties.getInstant().getLauncherPageName());
		}
		//可能在connectActivity中做了连接动作
		obtainSocket();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy-->");
		inflaterPage.videoRelease();
	}


	private void loadData(final String pageName){
		AsyncTask<String, Integer, Page> task = new AsyncTask<String, Integer, Page>(){

			@Override
			protected Page doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				Properties properties = Properties.getInstant();
				if(!properties.isSetDesignerSize()){
					PropertiesXmlParser pxp = new PropertiesXmlParser();
					pxp.parse(Constant.PROPERTIES_DIR, Constant.PROPERTIES_FILE_NAME);
				}
				
				
				//launcherPage = "main";
				Pages pages = Pages.getInstant();
				Page page = null;
				if(pages.containKey(pageName)){//
					page = pages.getPage(pageName);
				}else{
					PageXmlParser xmlPsr = new PageXmlParser();
					page = xmlPsr.parse(Constant.PAGES_DIR, pageName);
				}
				return pages.getAllPageMap().entrySet().iterator().next().getValue();
			}

			@Override
			protected void onPostExecute(Page result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				
				
				if(result == null){
					primptTv.setText("加载布局失败");
					return;
				}else{
					primptTv.setText("");
					primptTv.setVisibility(View.GONE);
				}
				page = result;
				inflaterPageViewGroup(page);
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				primptTv.setText("正在加载布局");
			}
			
		};
		task.execute(new String[]{});
	}

	//加载主页viewGroup
	private void inflaterPageViewGroup(Page page){
		//先移除上一次的pageLayout，再添加新的
		container.removeView(pageLayout);
		pageLayout = inflaterPage.getAbsoluteLayout(page);
		container.addView(pageLayout);
//		Button button = new Button(this);
//		button.setText("你好啊");
//		container.addView(button);
		posContainerOfPage = container.getChildCount();
	}

	/**
	 * 更新连接状态的文字
	 * @param message
	 */
	private void updateConnectStateView(String message){
		mStateTv.setText(message);
	}
	/**
	 * 更新连接状态的图标
	 * @param drawable
	 */
	private void updateConnectStateImg(Drawable drawable){
		mStateImg.setImageDrawable(drawable);
	}

    /**
     * 更新页面上view的数据
     * @param view
	 * @param jID
     * @param rdw
     */
	private void updateView(View view, String jID, ReadDataWrapper rdw){
		((TextView)view).setText(rdw.getString());
		saveViewStateToSQLite();
	}
	//保存view状态到数据库
	private void saveViewStateToSQLite(){

	}



	
	private class MyOnEditorActionListener implements OnEditorActionListener{
		
		@Override
		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			if(actionId == EditorInfo.IME_ACTION_DONE){
				onEditDone((EditText)view);
				return true;
			}
			return false;
		}
	}
	
	private class MySeekbarChangeListener implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
			if(fromUser){
				if(seekbar.getTag() != null){
					ViewArgs.Tag argTag = (ViewArgs.Tag)seekbar.getTag();
					sendSeekbarProgressCmd(argTag.jId, progress);
				}
			}else {
				//竖直滑动条控件比较特殊
				if(seekbar instanceof VerticalSeekBar){
					ViewArgs.Tag argTag = (ViewArgs.Tag)seekbar.getTag();
					sendSeekbarProgressCmd(argTag.jId, progress);
				}
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekbar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekbar) {
		}
		
	}
	
	private class MyTextViewOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			onClickTextView((TextView)view);
		}
	}
	private class MyImgOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			onTouchImg(view, EventType.TOUCH_UP);
		}
	}
	
	private class MyBtnOnClickListener implements OnClickListener{

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
			btnEvent(view, EventType.CLICK);
		}
	}
	private class MyBtnOnTouchListener implements OnTouchListener{
		
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// TODO Auto-generated method stub
			int action = event.getAction();
			switch(action){
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
	 * @param view 按钮
	 * @param event 事件
	 */
	private void btnEvent(View view, EventType event){
		if(view.getTag() != null){
			ViewArgs.Tag argTag = (ViewArgs.Tag)view.getTag();
			if(argTag != null){
				String flip = argTag.flip;
				String jId = argTag.jId;
				Log.i(TAG, "btnEvent-->"+event+", jId="+jId +", flip="+flip);
				//如果有效则表示要打开下一个主页，否则不打开主页
				if(ViewArgs.isValiableFlip(flip)){
					if(event == EventType.CLICK){
						loadData(flip);
					}
				}else{					
					//jId=0表示没有设置jId
					if("0".equals(jId)){
						Log.i(TAG, "btnEvent-->jId=0, 表示该view没有设置jId");
					}else{
						Subpage subpage = page.getSubpageArgs(jId);
						if(subpage != null){
							if(event == EventType.CLICK){
								repalceSubpageLayout(subpage);
							}
						}else{
							if(event == EventType.TOUCH_DOWN){
								sendPressCmd(jId);
							}else if(event == EventType.TOUCH_UP){
								sendUpCmd(jId);
							}
						}
					}
				}
			}
		}
	}
	
	private void onTouchImg(View view, EventType event){
		if(view.getTag() != null){
			ViewArgs.Tag argTag = (ViewArgs.Tag)view.getTag();
			String jId = argTag.jId;
			if(event == EventType.TOUCH_DOWN){
				sendPressCmd(jId);
			}else if(event == EventType.TOUCH_UP){
				sendUpCmd(jId);
			}
		}
	}
	
	private void onClickTextView(TextView view){
		if(view.getTag() != null){
			ViewArgs.Tag argTag = (ViewArgs.Tag)view.getTag();
			String jId = argTag.jId;
			String message = view.getText().toString();
			sendString(jId, message);
		}
	}
	private void onEditDone(EditText view){
		if(view.getTag() != null){
			ViewArgs.Tag argTag = (ViewArgs.Tag)view.getTag();
			String jId = argTag.jId;
			String message = view.getEditableText().toString();
			sendString(jId, message);
		}
	}
	
	/**
	 * 替换subpage布局
	 * @param subpage
	 */
	private void repalceSubpageLayout(Subpage subpage){
		if(pageLayout != null){
			if(isAddSubpage){
				pageLayout.removeViewAt(posContainerOfPage);
				pageLayout.addView(inflaterPage.newSubpage(subpage));
				isAddSubpage = true; 
			}else{
				pageLayout.addView(inflaterPage.newSubpage(subpage));
				isAddSubpage = true;
			}
			posContainerOfPage = pageLayout.getChildCount()-1;
		}
	}

	/**
	 * 添加subpage布局到主页
	 * @param subpage
	 */
	private void addSubpageLayout(Subpage subpage){
		if(pageLayout != null){
			AbsoluteLayout subpageLayout = inflaterPage.newSubpage(subpage);
			//subpageLayout.setTag();
			pageLayout.addView(subpageLayout);
		}
	}

	/**
	 * 移除subpage布局
	 * @param jID
	 * @return true:移除成功
	 */
	private boolean removeSubpageLayout(String jID, AbsoluteLayout subpageLayout){
		if(pageLayout != null && subpageLayout != null){
			pageLayout.removeView(subpageLayout);
			return true;
		}
		return false;
	}

	/**
	 * 发送手指按下命令
	 * @param jId
	 */
	private void sendPressCmd(String jId){
		int id = 0;
		try{
			id = Integer.parseInt(jId);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		byte[] bbs = WriteDataWrapper.packWriteDigitDataPress(id);
		sendData(bbs);
	}

	/**
	 * 发送手指抬起命令
	 * @param jId
	 */
	private void sendUpCmd(String jId){
		int id = 0;
		try{
			id = Integer.parseInt(jId);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		byte[] bbs = WriteDataWrapper.packWriteDigitDataRelease(id);
		sendData(bbs);
	}
	/**
	 * 发送拖动条进度的命令
	 */
	private void sendSeekbarProgressCmd(String jId, int progress){
		int id = 0;
		try{
			id = Integer.parseInt(jId);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		id -= InflaterPage.DIF_TOP_SEEK_BAR_FLAG;

		byte[] bbs = WriteDataWrapper.packWriteAnalogData(id, progress);
		sendData(bbs);
	}

	/**
	 * 发送字符串
	 * @param jId
	 * @param message
	 */
	private void sendString(String jId, String message){
		int id = 0;
		try{
			id = Integer.parseInt(jId);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		byte[] bbs = null;
		sendData(bbs);
	}
	//发送数据
	private void sendData(byte[] bytes){
		ConnectManager.getInstance().writeDate(bytes);
	}

	/**
	 * 获取socket连接及读写线程
	 */
	private void obtainSocket(){
		if(ConnectManager.getInstance().isConnected()){
			updateConnectStateView("connected");
			updateConnectStateImg(getResources().getDrawable(R.drawable.round_green));
		}else {
			updateConnectStateView("disconnected");
			updateConnectStateImg(getResources().getDrawable(R.drawable.round_red));
		}
	}
	//进入setting页面
	private void intoSeettingActivity(){
		startActivity(new Intent(this, SettingActivity.class));
	}
	//进入tcp连接页面
	private void intoConnectActivity(){
		startActivity(new Intent(this, ConnectActivity.class));
	}

	private void setSlidingDrawerData(){
		ImageView handleImgView = (ImageView) slidingDrawer.getHandle();
		View contentView = slidingDrawer.getContent();
		RecyclerView recyclerView = contentView.findViewById(R.id.sliding_drawer_recycler_view);
		MySlidingDrawerAdapter adapter = new MySlidingDrawerAdapter(this, iniSlidingDrawerData());
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);
	}
	private List<String> iniSlidingDrawerData(){
		ArrayList<String> list = new ArrayList<>();
		list.add("设置");
		list.add("链接");
		list.add("其他");
		return list;
	}
	//抽屉列表item点击事件
	private void slidingDrawerItemClick(int position, String content){
		switch (position){
			case 0:
				intoSeettingActivity();
				break;
			case 1:
				intoConnectActivity();
				break;
			case 2:
				break;
		}
	}

	private class MySlidingDrawerAdapter extends RecyclerView.Adapter<SlidingDrawerVh> {

		private Context mContext;
		private List<String> mListData;

		public MySlidingDrawerAdapter(Context context, List<String> data){
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
	private class SlidingDrawerVh extends RecyclerView.ViewHolder{

		private TextView mNameTv;

		public SlidingDrawerVh(@NonNull View itemView) {
			super(itemView);
			mNameTv = itemView.findViewById(R.id.name_tv);
		}
	}
	
	private void testAdd(){
//		pageLayout.addView(null, 0);
//		pageLayout.removeViewAt(0);
	}
	
}
