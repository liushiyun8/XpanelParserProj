package com.zff.xpanel.parser.view.inflater;

import java.util.List;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.vlc.widget.VerticalSeekBar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.zff.xpanel.parser.util.Properties;
import com.zff.xpanel.parser.util.Constant;
import com.zff.xpanel.parser.view.ButtonArgs;
import com.zff.xpanel.parser.view.MyColor;
import com.zff.xpanel.parser.view.EditTextArgs;
import com.zff.xpanel.parser.view.GaugeArgs;
import com.zff.xpanel.parser.view.ImgViewArgs;
import com.zff.xpanel.parser.view.ListViewArgs;
import com.zff.xpanel.parser.view.Page;
import com.zff.xpanel.parser.view.SliderArgs;
import com.zff.xpanel.parser.view.Subpage;
import com.zff.xpanel.parser.view.TextViewArgs;
import com.zff.xpanel.parser.view.Theme;
import com.zff.xpanel.parser.view.VideoArgs;
import com.zff.xpanel.parser.view.ViewArgs;
import com.zff.xpanel.parser.view.ViewArgs.Type;
import com.zff.xpanel.parser.view.WebViewArgs;

public class InflaterPage {

	private final String TAG = "InflaterPage";

	//标识顶部透明拖动条，用于区分显示真实进度拖动条
	public static final int DIF_TOP_SEEK_BAR_FLAG = 10000;
	
	private Context context;
	private Properties properties;
	
	private OnClickListener mBtnOnClickListener = new MyViewOnClickListener();
	private OnTouchListener mBtnOnTouchListener = new MyViewOnTouchListener();
	private OnSeekBarChangeListener mOnSeekBarChangeListener;
	private OnEditorActionListener mOnEditorActionListener;
	private OnClickListener mTextViewOnClickListener = new MyViewOnClickListener();
	private OnClickListener mImgOnClickListener = new MyViewOnClickListener();

	private LibVLC mLibVLC;
	private MediaPlayer mmediaPlayer;
	private VideoView mVideoView;
	
	
	public InflaterPage(Context context) {
		super();
		this.context = context;
		this.properties = Properties.getInstant(context);
	}

	public void setTextViewOnClickListener(OnClickListener l){
		this.mTextViewOnClickListener = l;
	}
	public void setImgOnClickListener(OnClickListener l){
		this.mImgOnClickListener = l;
	}
	public void setBtnOnClickListener(OnClickListener btnOnClickListener) {
		this.mBtnOnClickListener = btnOnClickListener;
	}
	
	public void setBtnTouchListener(OnTouchListener onTouchListener){
		this.mBtnOnTouchListener = onTouchListener;
	}

	public OnTouchListener getBtnTouchListener(){
		return mBtnOnTouchListener;
	}
	
	public void setSeekbarChangeListener(OnSeekBarChangeListener l){
		mOnSeekBarChangeListener = l;
	}
	public OnSeekBarChangeListener getSeekbarChangeListener(){
		return mOnSeekBarChangeListener;
	}
	public void setOnEditorActionListener(OnEditorActionListener l){
		mOnEditorActionListener = l;
	}


	public AbsoluteLayout getAbsoluteLayout(Page page){
		AbsoluteLayout absLayout = new AbsoluteLayout(context);
		if(page == null){
			return absLayout;
		}
		
		int layoutX = (int) (page.getX() * properties.getLayoutWithRatio());
		int layoutY = (int) (page.getY() * properties.getLayoutHightRatio());
		int layoutW = (int) (properties.getDesignerWidth() * properties.getLayoutWithRatio());
		int layoutH = (int) (properties.getDesignerHight() * properties.getLayoutHightRatio());
		AbsoluteLayout.LayoutParams ablParams = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, layoutX, layoutY);
		absLayout.setLayoutParams(ablParams);
		//absLayout.setBackgroundColor(0x9ac865a9);
		Theme theme = page.getTheme();
		Theme.Value themeValue = null;
		if(theme != null){
			themeValue = theme.getInactiveValue();
		}
		if(themeValue != null){
			absLayout.setBackground(new BitmapDrawable(Constant.IMG_RES_DIR+"/"+themeValue.backgoundImg));
			//absLayout.setBackgroundColor(0x9ac865a9);
		}
		
		List<ViewArgs> listData = page.getViewArgsList();
		if(listData == null || listData.size() <1){
			return absLayout;
		}
		int size = listData.size();
		ViewArgs viewArgs = null;
		for(int i=0; i<size; i++){
			viewArgs = listData.get(i);
			int w = (int) (viewArgs.getW() * properties.getLayoutWithRatio());
			int h = (int) (viewArgs.getH() * properties.getLayoutHightRatio());
			int x = (int) (viewArgs.getX() * properties.getLayoutWithRatio());
			int y = (int) (viewArgs.getY() * properties.getLayoutHightRatio());
			LayoutParams params = new LayoutParams(w, h, x, y);
			if(viewArgs.getType() == ViewArgs.Type.SUBPAGE){
				//absLayout.addView(newSubpage((Subpage)view Args), params);;
			}else if(ViewArgs.Type.LIST == viewArgs.getType()){
                ListViewArgs lvArgs = (ListViewArgs)viewArgs;
				//AbsoluteLayout listView = newListView((ListViewArgs)viewArgs);
				View listView = null;
				if(ListViewArgs.ORIENTATION_HORIZONTAL.equals(lvArgs.getOrientation())){
				    listView = newHorScrollView(lvArgs);
                }else {
				    listView = newScrollView(lvArgs);
                }
				if(listView != null){
					absLayout.addView(listView, params);
				}
			}
			else{
				View child = newView(viewArgs, x, y, w, h);
				if(child != null){					
					absLayout.addView(child, params);
				}
			}
		}
		return absLayout;
	}
	
	public ViewGroup getViewGroup(){
		return null;
	}
	

	private AbsoluteLayout newListView(ListViewArgs lva){
	    //lva.getOrientation()
		Subpage header = lva.getHeaderSub();
		if(header != null){
			return getAbsoluteLayout(header);
		}else{
			return null;
		}
	}
	private HorizontalScrollView newHorScrollView(ListViewArgs lva){
        HorizontalScrollView hScrollView = new HorizontalScrollView(context);
        hScrollView.addView(loadScrollLinearLayout(false, lva));
        return hScrollView;
    }
    private ScrollView newScrollView(ListViewArgs lva){
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(loadScrollLinearLayout(true, lva));
        return scrollView;
    }
    private LinearLayout loadScrollLinearLayout(boolean isScrollView, ListViewArgs lva){
        LinearLayout linearLayout = new LinearLayout(context);
        if(isScrollView){
			linearLayout.setOrientation(LinearLayout.VERTICAL);
		}else {
			if(ListViewArgs.ORIENTATION_HORIZONTAL.equals(lva.getOrientation())){
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			}else {
				linearLayout.setOrientation(LinearLayout.VERTICAL);
			}
		}
        Subpage header = lva.getHeaderSub();
        if(header != null) {
            linearLayout.addView(getAbsoluteLayout(header));
        }
        Subpage title = lva.getTitleSub();
        if(header != null) {
            linearLayout.addView(getAbsoluteLayout(title));
        }
        Subpage contentSub = lva.getContentSub();
        if(header != null) {
            linearLayout.addView(getAbsoluteLayout(contentSub));
        }
        Subpage footerSub = lva.getFooterSub();
        if(header != null) {
            linearLayout.addView(getAbsoluteLayout(footerSub));
        }
        return linearLayout;
    }
	public AbsoluteLayout newSubpage(Subpage sbp){
		return getAbsoluteLayout(sbp);
	}
	
	public View newView(ViewArgs viewArgs, int x, int y, int w, int h){
		ViewArgs.Type type = viewArgs.getType();
		if(type == null){
			type = Type.TEXT;
		}
		switch(type){
		default:
		case TEXT:
			TextViewArgs tvArgs = (TextViewArgs)viewArgs;
			return newTextView(tvArgs);
		case BUTTON:
			ButtonArgs btnArgs = (ButtonArgs)viewArgs;
			return newButtonView(btnArgs);
		case IMG:
			ImgViewArgs imgViewArgs = (ImgViewArgs)viewArgs;
			return newImageView(imgViewArgs);
		case VIDEO:
			VideoArgs videoArgs = (VideoArgs)viewArgs;
			return newSurfaceView(videoArgs);
			//mVideoView = newVideoView(videoArgs);
			//return mVideoView;
		case PROGRESS_BAR:
			GaugeArgs gaugeArgs = (GaugeArgs)viewArgs;
			if(h > w){
				return newVerticalProgressBar(gaugeArgs);
			}else {
				return newProgressBar(gaugeArgs);
			}
		case SEEK_BAR:
			SliderArgs sliderArgs = (SliderArgs)viewArgs;
			AbsoluteLayout seekBarParent = new AbsoluteLayout(context);
			LayoutParams params = new LayoutParams(w, h, 0, 0);
			if(h > w){
				VerticalSeekBar verticalSeekBar = newVerticalSeekBar(sliderArgs);
				//verticalSeekBar.setEnabled(false);
				//verticalSeekBar.setProgress(60);
				seekBarParent.addView(verticalSeekBar, params);
				return seekBarParent;
			}else {
				SeekBar seekBar = newSeekBar(sliderArgs);
				seekBarParent.addView(seekBar, params);
				return seekBarParent;
				//return seekBar;
			}
		case WEB_VIEW:
			WebViewArgs webArgs = (WebViewArgs)viewArgs;
			return newWebview(webArgs);		
		}
	}
	
	
	private TextView newTextView(TextViewArgs tvArgs){
		TextView textView = new TextView(context);
		ViewArgs.Tag tag = new ViewArgs.Tag();
		tag.jId = tvArgs.getjId();
		tag.flip = tvArgs.getFlip();
		textView.setTag(tag);
		textView.setText(tvArgs.getText());
		//textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setGravity(Gravity.CENTER);
		
		Theme theme = tvArgs.getTheme();
		Theme.Value themeValue = null;
		if(theme != null){			
			if(tvArgs.isSelected()){
				themeValue = theme.getActiveValue();
			}else{
				themeValue = theme.getInactiveValue();
			}
		}
		if(themeValue != null){				
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, themeValue.fontSize * properties.getTextSizeRatio());
			textView.setTextColor(MyColor.getColorValue(themeValue.fontColor));
			textView.setBackground(new BitmapDrawable(Constant.IMG_RES_DIR+"/"+themeValue.backgoundImg));
		}
		//textView.setBackgroundColor(0x9ac865a9);
		textView.setOnClickListener(mTextViewOnClickListener);
		return textView;
	}
	//输入框
	private EditText newEditView(EditTextArgs editArgs){
		EditText editText = new EditText(context);
		ViewArgs.Tag tag = new ViewArgs.Tag();
		tag.jId = editArgs.getjId();
		tag.flip = editArgs.getFlip();
		editText.setTag(tag);
		editText.setText(editArgs.getText());
		editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		if(editArgs.isPass()){
			editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
		editText.setOnEditorActionListener(mOnEditorActionListener);
//		editText.setOnEditorActionListener(new OnEditorActionListener() {
//			
//			@Override
//			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//				if(actionId == EditorInfo.IME_ACTION_DONE){
//					return true;
//				}
//				return false;
//			}
//		});
		//editText.setHint(editArgs.getHint());
		//editText.setGravity(Gravity.CENTER_HORIZONTAL);
		//editText.setGravity(Gravity.CENTER);
		
		Theme theme = editArgs.getTheme();
		Theme.Value themeValue = null;
		if(theme != null){			
			if(editArgs.isSelected()){
				themeValue = theme.getActiveValue();
			}else{
				themeValue = theme.getInactiveValue();
			}
		}
		if(themeValue != null){				
			editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, themeValue.fontSize * properties.getTextSizeRatio());
			editText.setTextColor(MyColor.getColorValue(themeValue.fontColor));
			editText.setBackground(new BitmapDrawable(Constant.IMG_RES_DIR+"/"+themeValue.backgoundImg));
		}else{			
			editText.setBackground(null);
		}
		//editText.setBackgroundColor(0x9ac865a9);
		return editText;
	}

	private ImageView newImageView(ImgViewArgs imgViewArgs){
		ImageView imgView = new ImageView(context);
		//imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
		
		ImgViewArgs.ImageDrawable drawable = imgViewArgs.getImgDrawable();
		if(drawable != null){				
			//imgView.setImageBitmap(BitmapFactory.decodeFile(Constant.IMG_RES_DIR+"/"+drawable.imgPath));
			imgView.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(Constant.IMG_RES_DIR+"/"+drawable.imgPath)));
		}
		imgView.setOnClickListener(mImgOnClickListener);
		return imgView;
	}
	private Button newButtonView(ButtonArgs btnArgs){
		Button button = new Button(context);
		ViewArgs.Tag tag = new ViewArgs.Tag();
		tag.jId = btnArgs.getjId();
		tag.flip = btnArgs.getFlip();
		button.setTag(tag);
		//button.setGravity(Gravity.CENTER_HORIZONTAL);
		button.setGravity(Gravity.CENTER);
		button.setPadding(0, 0, 0, 0);
		//button.setText(btnArgs.getText());
		
		ButtonArgs.Content btnContent = null;
		if(btnArgs.isSelected()){
			btnContent = btnArgs.getActiveContent();
		}else{
			btnContent = btnArgs.getInactiveContent();
		}
		if(btnContent != null){				
			//button.setCompoundDrawablesWithIntrinsicBounds(btnContent.imgLeft, btnContent.imgTop, btnContent.imgRight, btnContent.imgBottom);
			String filePath = Constant.IMG_RES_DIR+"/"+ btnContent.imgPath;
			//BitmapFactory.decodeFile(Tools.IMG_RES_DIR+"/"+drawable.imgPath)
			Drawable topDraw = new BitmapDrawable(filePath);
			int right = (int) (btnContent.imgW *  properties.getLayoutWithRatio());
			int bottom = (int) (btnContent.imgH * properties.getLayoutHightRatio());
//			int right = (int) (btnContent.imgW);
//			int bottom = (int) (btnContent.imgH);
			topDraw.setBounds(0, 0, right, bottom);
			
			//button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, topDraw, null, null);
			button.setCompoundDrawables(null, topDraw, null, null);
			
			button.setText(btnContent.text);
		}
		
		Theme theme = btnArgs.getTheme();
		if(theme != null){			
			Theme.Value activeThemeValue = theme.getActiveValue();
			Theme.Value inactiveThemeValue = theme.getInactiveValue();
			
			int[][] states = new int[2][];
			states[0] = new int[]{android.R.attr.state_selected};
			states[1] = new int[]{};
			int[] colors = new int[2];
			StateListDrawable sld = new StateListDrawable();
			if(activeThemeValue != null){
				colors[0] = MyColor.getColorValue(activeThemeValue.fontColor);
				sld.addState(new int[]{android.R.attr.state_selected},
						new BitmapDrawable(Constant.IMG_RES_DIR+"/"+ activeThemeValue.backgoundImg));
				if(btnArgs.isMockPress()){
					sld.addState(new int[]{android.R.attr.state_pressed},					
							new BitmapDrawable(Constant.IMG_RES_DIR+"/"+ activeThemeValue.backgoundImg));
				}
			}
			if(inactiveThemeValue != null){
				button.setTextSize(TypedValue.COMPLEX_UNIT_PX, inactiveThemeValue.fontSize * properties.getTextSizeRatio());
//				colors[1] = MyColor.getColorValue(inactiveThemeValue.fontColor);
				colors[1] = MyColor.getColorValue(inactiveThemeValue.fontColor);
				sld.addState(new int[]{}, new BitmapDrawable(Constant.IMG_RES_DIR+"/"+ inactiveThemeValue.backgoundImg));
			}
			ColorStateList csl = new ColorStateList(states, colors);
			button.setTextColor(csl);
			button.setBackground(sld);
		}
		
		//ColorUtils.RGBToHSL(arg0, arg1, arg2, arg3);
		//android.graphics.MyColor color = new android.graphics.MyColor();
		//ColorStateList csl = new ColorStateList(states, colors)
		button.setOnClickListener(mBtnOnClickListener);
		button.setOnTouchListener(mBtnOnTouchListener);
//		button=new Button(context);
//		button.setText("hello");
		return button;
	}
	
	private ProgressBar newProgressBar(GaugeArgs gaugeArgs){
		ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
		ViewArgs.Tag tag = new ViewArgs.Tag();
		tag.jId = gaugeArgs.getjId();
		pb.setTag(tag);
		//pb.setMax(gaugeArgs.getmMax());
		pb.setProgress(20);
		return pb;
	}
	private SeekBar newSeekBar(SliderArgs sliderArgs){
		SeekBar sb = new SeekBar(context);
		ViewArgs.Tag tag = new ViewArgs.Tag();
		tag.jId = sliderArgs.getjId();
		sb.setTag(tag);
		sb.setMax(sliderArgs.getmMax());
		SliderArgs.Indicator indicator = sliderArgs.getIndicator();
		if(indicator != null){
			String drawPath = Constant.IMG_RES_DIR+"/"+ indicator.imgPath;
			Bitmap srcBmp = BitmapFactory.decodeFile(drawPath);
			Bitmap dstBmp = Bitmap.createScaledBitmap(srcBmp, sliderArgs.getH(), sliderArgs.getH(), false);
			sb.setThumb(new BitmapDrawable(dstBmp));
		}else {
			sb.setThumb(null);
		}
		//sb.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		return sb;
	}
	//竖直进度条
	private VerticalSeekBar newVerticalProgressBar(GaugeArgs gaugeArgs){
		VerticalSeekBar verticalSeekBar = new VerticalSeekBar(context);
		ViewArgs.Tag tag = new ViewArgs.Tag();
		tag.jId = gaugeArgs.getjId();
		verticalSeekBar.setTag(tag);
		verticalSeekBar.setMax(gaugeArgs.getmMax());
		verticalSeekBar.setThumb(null);
		return verticalSeekBar;
	}
	//竖直拖动条
	private VerticalSeekBar newVerticalSeekBar(SliderArgs sliderArgs){
		VerticalSeekBar verticalSeekBar = new VerticalSeekBar(context);
		ViewArgs.Tag tag = new ViewArgs.Tag();
		tag.jId = sliderArgs.getjId();
		verticalSeekBar.setTag(tag);
		verticalSeekBar.setMax(sliderArgs.getmMax());
		SliderArgs.Indicator indicator = sliderArgs.getIndicator();
		if(indicator != null){
			String drawPath = Constant.IMG_RES_DIR+"/"+ indicator.imgPath;
			Bitmap srcBmp = BitmapFactory.decodeFile(drawPath);
			Bitmap dstBmp = Bitmap.createScaledBitmap(srcBmp, sliderArgs.getH(), sliderArgs.getH(), false);
			verticalSeekBar.setThumb(new BitmapDrawable(dstBmp));
		}else {
			verticalSeekBar.setThumb(null);
		}
		//verticalSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		return verticalSeekBar;
	}

	
	private WebView newWebview(WebViewArgs webArgs){
		WebView webView = new WebView(context);
		webView.loadUrl(webArgs.getUrl());
		WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        //ws.setCacheMode(mode);
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
		return webView;
	}
	
//	private VideoView newVideoView(VideoArgs videoArgs){
//		VideoView vv = new VideoView(context);
//		
//		return vv;
//	}
	private SurfaceView newSurfaceView(VideoArgs videoArgs){
		SurfaceView surfaceView = new SurfaceView(context);
//		if(getMediaPlayer().getVLCVout().areViewsAttached()){
//
//		}
		getMediaPlayer().getVLCVout().setVideoView(surfaceView);
		getMediaPlayer().getVLCVout().attachViews();
		if(!TextUtils.isEmpty(videoArgs.getUrl())){
			setMedia(videoArgs.getUrl());
		}
		setSurfaceHolder(surfaceView);
		return surfaceView;
	}
	private VideoView newVideoView(VideoArgs videoArgs){
		VideoView videoView = new VideoView(context);
		Uri uri = Uri.parse(videoArgs.getUrl());
		videoView.setVideoURI(uri);
		return videoView;
	}
	
	//设置视频 surfaceHolder
	private void setSurfaceHolder(SurfaceView surfaceView){
		SurfaceHolder mSurfaceHolder = surfaceView.getHolder();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String chroma = pref.getString("chroma_format", "");
//		if(LibVlcUtil.isGingerbreadOrLater() && chroma.equals("YV12")) {
//            mSurfaceHolder.setFormat(ImageFormat.YV12);
//        } else if (chroma.equals("RV16")) {
//            mSurfaceHolder.setFormat(PixelFormat.RGB_565);
//        } else {
//            mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
//        }
        mSurfaceHolder.addCallback(mSurfaceCallback);
	}
	/**
	 * 视频 vlc
     * attach and disattach surface to the lib
     */
    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if(format == PixelFormat.RGBX_8888)
                Log.d(TAG, "Pixel format is RGBX_8888");
            else if(format == PixelFormat.RGB_565)
                Log.d(TAG, "Pixel format is RGB_565");
            else if(format == ImageFormat.YV12)
                Log.d(TAG, "Pixel format is YV12");
            else
                Log.d(TAG, "Pixel format is other/unknown");
            //mLibVLC.attachSurface(holder.getSurface(), VideoPlayerActivity.this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            //mLibVLC.detachSurface();
//			getMediaPlayer().stop();
//			getMediaPlayer().release();
//			getMediaPlayer().getVLCVout().detachViews();
        }
    };
	
    public void videoPlay(){
		getMediaPlayer().play();
		//mVideoView.start();
    	Toast.makeText(context, "video play", Toast.LENGTH_SHORT).show();
    }
    public void videoPause(){
		getMediaPlayer().pause();
		Toast.makeText(context, "video pause", Toast.LENGTH_SHORT).show();
	}
    public void videoStop(){
    	getMediaPlayer().stop();
		//mVideoView.pause();
    	Toast.makeText(context, "video stop", Toast.LENGTH_SHORT).show();
    }
    public void videoRelease(){
    	getMediaPlayer().release();
    	getLibVLC().release();
    	getMediaPlayer().getVLCVout().detachViews();
		Toast.makeText(context, "video release", Toast.LENGTH_SHORT).show();
	}
	private LibVLC getLibVLC() {
		if (mLibVLC == null) {
			mLibVLC = new LibVLC(context);
		}
		return mLibVLC;
	}

	private MediaPlayer getMediaPlayer() {
		if (mmediaPlayer == null) {
			mmediaPlayer = new MediaPlayer(getLibVLC());
		}
		return mmediaPlayer;
	}
	private void setMedia(String urlStr) {
		Uri uri = Uri.parse(urlStr);
		Media media = new Media(getLibVLC(), uri);
//		String[] options = null;
//		for(String option : options){
//			media.addOption(option);
//		}
		getMediaPlayer().setMedia(media);
		media.release();
	}
	
	private class MyViewOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			if(view.getTag() != null){				
				ViewArgs.Tag argTag = (ViewArgs.Tag)view.getTag();
				String jId = argTag.jId;
				Log.i(TAG, "onClick-->jId="+jId);
				Toast.makeText(view.getContext(), "jId="+jId, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class MyViewOnTouchListener implements OnTouchListener{
		
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// TODO Auto-generated method stub
			int action = event.getAction();
			ViewArgs.Tag argTag = (ViewArgs.Tag)view.getTag();
			String jId = argTag.jId;
			switch(action){
			case MotionEvent.ACTION_DOWN:
				Log.i(TAG, "onTouch-->down, jId="+jId);
				break;
			case MotionEvent.ACTION_UP:
				Log.i(TAG, "onTouch-->up, jId="+jId);
				break;
			}
			return false;
		}
		
	}
}
