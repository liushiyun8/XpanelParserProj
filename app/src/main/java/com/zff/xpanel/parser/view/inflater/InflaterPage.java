package com.zff.xpanel.parser.view.inflater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.VideoView;

import com.emp.xdcommon.android.log.LogUtils;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.widget.PLVideoView;
import com.zff.xpanel.parser.cache.Layouts;
import com.zff.xpanel.parser.ui.ThirdActivity;
import com.zff.xpanel.parser.util.Constant;
import com.zff.xpanel.parser.util.Properties;
import com.zff.xpanel.parser.view.ButtonArgs;
import com.zff.xpanel.parser.view.EditTextArgs;
import com.zff.xpanel.parser.view.GaugeArgs;
import com.zff.xpanel.parser.view.IjkVideoView;
import com.zff.xpanel.parser.view.ImgViewArgs;
import com.zff.xpanel.parser.view.ListViewArgs;
import com.zff.xpanel.parser.view.MyAbsoluteLayout;
import com.zff.xpanel.parser.view.MyColor;
import com.zff.xpanel.parser.view.MyStrokeShape;
import com.zff.xpanel.parser.view.MyVideoView;
import com.zff.xpanel.parser.view.Page;
import com.zff.xpanel.parser.view.SliderArgs;
import com.zff.xpanel.parser.view.Subpage;
import com.zff.xpanel.parser.view.TextViewArgs;
import com.zff.xpanel.parser.view.Theme;
import com.zff.xpanel.parser.view.VideoArgs;
import com.zff.xpanel.parser.view.ViewArgs;
import com.zff.xpanel.parser.view.ViewArgs.Type;
import com.zff.xpanel.parser.view.WebViewArgs;

import org.videolan.libvlc.MediaPlayer;
import org.videolan.vlc.widget.VerticalSeekBar;

import java.io.File;
import java.io.IOException;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class InflaterPage {

    private final String TAG = "InflaterPage";

    //标识顶部透明拖动条，用于区分显示真实进度拖动条
    public static final int DIF_TOP_SEEK_BAR_FLAG = 10000;

    private Context context;
    private Properties properties;

    private OnClickListener mBtnOnClickListener = new MyViewOnClickListener();
    private OnTouchListener mBtnOnTouchListener = new MyViewOnTouchListener();
    private OnSeekBarChangeListener mOnSeekBarChangeListener = new MySeekbarChangeListener();
    private OnEditorActionListener mOnEditorActionListener = new MyOnEditorActionListener();
    private OnClickListener mTextViewOnClickListener = new MyViewOnClickListener();
    private OnClickListener mImgOnClickListener = new MyViewOnClickListener();
    private OnTouchListener mImgOnTouchListener = new MyImgViewOnTouchListener();

    //    private LibVLC mLibVLC;
    private MediaPlayer mmediaPlayer;
    private VideoView mVideoView;


    public InflaterPage(Context context) {
        super();
        this.context = context;
        this.properties = Properties.getInstant();
    }

    public void setTextViewOnClickListener(OnClickListener l) {
        this.mTextViewOnClickListener = l;
    }

    public void setImgOnClickListener(OnClickListener l) {
        this.mImgOnClickListener = l;
    }

    public void setBtnOnClickListener(OnClickListener btnOnClickListener) {
        this.mBtnOnClickListener = btnOnClickListener;
    }

    public void setBtnTouchListener(OnTouchListener onTouchListener) {
        this.mBtnOnTouchListener = onTouchListener;
    }

    public OnTouchListener getBtnTouchListener() {
        return mBtnOnTouchListener;
    }

    public void setSeekbarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    public OnSeekBarChangeListener getSeekbarChangeListener() {
        return mOnSeekBarChangeListener;
    }

    public void setOnEditorActionListener(OnEditorActionListener l) {
        mOnEditorActionListener = l;
    }


    public MyAbsoluteLayout getAbsoluteLayout(Page page) {
        MyAbsoluteLayout absLayout = new MyAbsoluteLayout(context);
        absLayout.setFocusableInTouchMode(true);
        if (page == null) {
            return absLayout;
        }
        absLayout.setTrans(page.getTrans());
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = page.getjId();
        tag.flip = page.getFlip();
        absLayout.setTag(tag);
//        absLayout.setContentDescription(page.getjId());
//        int layoutX = (int) (page.getX() * properties.getLayoutWithRatio());
//        int layoutY = (int) (page.getY() * properties.getLayoutHightRatio());
        int layoutW = (int) (properties.getDesignerWidth() * properties.getLayoutWithRatio());
        int layoutH = (int) (properties.getDesignerHight() * properties.getLayoutHightRatio());
        LayoutParams ablParams = new LayoutParams(layoutW, layoutH, 0, 0);
        absLayout.setLayoutParams(ablParams);
        //absLayout.setBackgroundColor(0x9ac865a9);
        Theme theme = page.getTheme();
        Theme.Value themeValue = null;
        if (theme != null) {
            themeValue = theme.getInactiveValue();
        }
        if (themeValue != null) {
            if (!TextUtils.isEmpty(themeValue.backgoundImg)) {
                if (themeValue.backgoundImg.endsWith(".gif")) {
                    try {
                        absLayout.setBackground(new GifDrawable(Constant.IMG_RES_DIR + "/" + themeValue.backgoundImg));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    absLayout.setBackground(new BitmapDrawable(Constant.IMG_RES_DIR + "/" + themeValue.backgoundImg));
            }
        }

        List<ViewArgs> listData = page.getViewArgsList();
        if (listData == null || listData.size() < 1) {
            return absLayout;
        }
        int size = listData.size();
        ViewArgs viewArgs = null;
        for (int i = 0; i < size; i++) {
            viewArgs = listData.get(i);
            int w = (int) (viewArgs.getW() * properties.getLayoutWithRatio());
            int h = (int) (viewArgs.getH() * properties.getLayoutHightRatio());
            int x = (int) (viewArgs.getX() * properties.getLayoutWithRatio());
            int y = (int) (viewArgs.getY() * properties.getLayoutHightRatio());
            LayoutParams params = new LayoutParams(w, h, x, y);
            if (viewArgs.getType() == Type.SUBPAGE) {
                absLayout.addView(newSubpage((Subpage) viewArgs), params);
            } else if (Type.LIST == viewArgs.getType()) {
                ListViewArgs lvArgs = (ListViewArgs) viewArgs;
                //AbsoluteLayout listView = newListView((ListViewArgs)viewArgs);
                View listView = null;
                if (ListViewArgs.ORIENTATION_HORIZONTAL.equals(lvArgs.getOrientation())) {
                    listView = newHorScrollView(lvArgs);
                } else {
                    listView = newScrollView(lvArgs);
                }
                absLayout.addView(listView, params);
            } else {
                View child = newView(viewArgs, x, y, w, h);
                if (child != null) {
                    if (child instanceof SeekBar) {
                        Log.e(TAG, "height:" + child.getMinimumHeight() + ",width:" + child.getMinimumWidth());
                        if (viewArgs.getW() >= viewArgs.getH() && child.getMinimumHeight() > params.height)
                            params.height = child.getMinimumHeight();
                        if (viewArgs.getW() < viewArgs.getH() && child.getMinimumWidth() > params.width) {
                            params.width = child.getMinimumWidth();
                        }
                    }
                    absLayout.addView(child, params);
                }
            }
        }
        return absLayout;
    }

    public ViewGroup getViewGroup() {
        return null;
    }


    private AbsoluteLayout newListView(ListViewArgs lva) {
        //lva.getOrientation()
        Subpage header = lva.getHeaderSub();
        if (header != null) {
            return getAbsoluteLayout(header);
        } else {
            return null;
        }
    }

    private HorizontalScrollView newHorScrollView(ListViewArgs lva) {
        HorizontalScrollView hScrollView = new HorizontalScrollView(context);
        hScrollView.addView(loadScrollLinearLayout(false, lva));
        return hScrollView;
    }

    private ScrollView newScrollView(ListViewArgs lva) {
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(loadScrollLinearLayout(true, lva));
        return scrollView;
    }

    private LinearLayout loadScrollLinearLayout(boolean isScrollView, ListViewArgs lva) {
        LinearLayout linearLayout = new LinearLayout(context);
        if (isScrollView) {
            linearLayout.setOrientation(LinearLayout.VERTICAL);
        } else {
            if (ListViewArgs.ORIENTATION_HORIZONTAL.equals(lva.getOrientation())) {
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                linearLayout.setOrientation(LinearLayout.VERTICAL);
            }
        }
        Subpage header = lva.getHeaderSub();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (header != null) {
            linearLayout.addView(getAbsoluteLayout(header), params);
        }
        Subpage title = lva.getTitleSub();
        if (header != null) {
            linearLayout.addView(getAbsoluteLayout(title), params);
        }
        Subpage contentSub = lva.getContentSub();
        if (header != null) {
            linearLayout.addView(getAbsoluteLayout(contentSub), params);
        }
        Subpage footerSub = lva.getFooterSub();
        if (header != null) {
            linearLayout.addView(getAbsoluteLayout(footerSub), params);
        }
        return linearLayout;
    }

    public MyAbsoluteLayout newSubpage(Subpage sbp) {
        MyAbsoluteLayout subLay = getAbsoluteLayout(sbp);
        subLay.setFocusableInTouchMode(true);
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = sbp.getjId();
        tag.sid = sbp.getS();
        tag.flip = sbp.getFlip();
        subLay.setTag(tag);
        if (sbp.getV() == 0) {
            subLay.setVisibilityWithoutAnimation(View.GONE);
        } else subLay.setVisibilityWithoutAnimation(View.VISIBLE);
        return subLay;
    }

    public View newView(ViewArgs viewArgs, int x, int y, int w, int h) {
        ViewArgs.Type type = viewArgs.getType();
        if (type == null) {
            type = Type.TEXT;
        }
        switch (type) {
            default:
            case TEXT:
                TextViewArgs tvArgs = (TextViewArgs) viewArgs;
                return newTextView(tvArgs);
            case EDITTEXT:
                EditTextArgs evArgs = (EditTextArgs) viewArgs;
                return newEditView(evArgs);
            case BUTTON:
                ButtonArgs btnArgs = (ButtonArgs) viewArgs;
                return newButtonView(btnArgs);
            case IMG:
                ImgViewArgs imgViewArgs = (ImgViewArgs) viewArgs;
                return newImageView(imgViewArgs);
            case VIDEO:
                VideoArgs videoArgs = (VideoArgs) viewArgs;
//                return newSurfaceView(videoArgs);
                return newJkVideoView(videoArgs);
//                return newPlVideoView(videoArgs);
//                mVideoView = newVideoView(videoArgs);
//                return newVideoView(videoArgs);
            case PROGRESS_BAR:
                GaugeArgs gaugeArgs = (GaugeArgs) viewArgs;
                return newProgressBar(gaugeArgs);
//                if (h > w) {
//                    return newVerticalProgressBar(gaugeArgs);
//                } else {
//                    return newProgressBar(gaugeArgs);
//                }
            case SEEK_BAR:
                SliderArgs sliderArgs = (SliderArgs) viewArgs;
                return newSeekBar(sliderArgs);
//                AbsoluteLayout seekBarParent = new AbsoluteLayout(context);
//                LayoutParams params = new LayoutParams(w, h, 0, 0);
//                if (h > w) {
//                    VerticalSeekBar verticalSeekBar = newVerticalSeekBar(sliderArgs);
//                    //verticalSeekBar.setEnabled(false);
//                    //verticalSeekBar.setProgress(60);
//                    seekBarParent.addView(verticalSeekBar, params);
//                    return seekBarParent;
//                } else {
//                    SeekBar seekBar = newSeekBar(sliderArgs);
//                    seekBarParent.addView(seekBar, params);
//                    return seekBarParent;
//                    //return seekBar;
//                }
            case WEB_VIEW:
                WebViewArgs webArgs = (WebViewArgs) viewArgs;
                return newWebview(webArgs);
        }
    }

    @SuppressLint("NewApi")
    private TextView newTextView(TextViewArgs tvArgs) {
        TextView textView;
        String jId = tvArgs.getjId();
        if ("1001".equals(jId) || "1002".equals(jId) || "1003".equals(jId)) {
            TextClock textClock = new TextClock(context);
            switch (jId) {
                case "1001":
                    textClock.setFormat12Hour("HH:mm:ss");
                    textClock.setFormat24Hour("HH:mm:ss");
                    break;
                case "1002":
                    textClock.setFormat12Hour("EEEE");
                    textClock.setFormat24Hour("EEEE");
                    break;
                case "1003":
                    textClock.setFormat12Hour("yyyy/MM/dd");
                    textClock.setFormat24Hour("yyyy/MM/dd");
                    break;
            }
            textView = textClock;
        } else {
            textView = new AppCompatTextView(context);
        }
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = tvArgs.getjId();
        tag.flip = tvArgs.getFlip();
        textView.setTag(tag);
        textView.setText(tvArgs.getText());
        //textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setGravity(Gravity.CENTER);

        Theme theme = tvArgs.getTheme();
        Theme.Value themeValue = null;
        if (theme != null) {
            if (tvArgs.isSelected()) {
                themeValue = theme.getActiveValue();
            } else {
                themeValue = theme.getInactiveValue();
            }
        }
        if (themeValue != null) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, themeValue.fontSize * properties.getTextSizeRatio());
            textView.setTextColor(MyColor.getColorValue(themeValue.fontColor));
            textView.setTypeface(Typeface.create(themeValue.font_family, themeValue.fontweight));
            textView.setGravity(themeValue.text_align | themeValue.vertical_align);
            textView.setBackground(new BitmapDrawable(Constant.IMG_RES_DIR + "/" + themeValue.backgoundImg));
            if (themeValue.underline) {
                textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                textView.getPaint().setAntiAlias(true);//抗锯齿
            }
        }
        //textView.setBackgroundColor(0x9ac865a9);
        textView.setOnClickListener(mTextViewOnClickListener);
        return textView;
    }

    //输入框
    private EditText newEditView(EditTextArgs editArgs) {
        EditText editText = new EditText(context, null, 0);
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = editArgs.getS();
        tag.flip = editArgs.getFlip();
        editText.setTag(tag);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);

        editText.setText(editArgs.getText());
        editText.setSingleLine();
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText.setImeActionLabel("发送", EditorInfo.IME_ACTION_SEND | EditorInfo.IME_FLAG_NO_FULLSCREEN | EditorInfo.IME_FLAG_NO_EXTRACT_UI);// | EditorInfo.IME_FLAG_NO_FULLSCREEN | EditorInfo.IME_FLAG_NO_EXTRACT_UI
        if (editArgs.isPass()) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        if ("4".equals(editArgs.getF())) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
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
        if (theme != null) {
            if (editArgs.isSelected()) {
                themeValue = theme.getActiveValue();
            } else {
                themeValue = theme.getInactiveValue();
            }
        }
        if (themeValue != null) {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, themeValue.fontSize * properties.getTextSizeRatio());
            editText.setTextColor(MyColor.getColorValue(themeValue.fontColor));
            editText.setTypeface(Typeface.create(themeValue.font_family, themeValue.fontweight));
            editText.setGravity(themeValue.text_align | themeValue.vertical_align);
            if (themeValue.underline) {
                editText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                editText.getPaint().setAntiAlias(true);//抗锯齿
            }
            if (themeValue.borderWidth > 0) {
                MyStrokeShape myStrokeShape = new MyStrokeShape(themeValue.borderWidth, MyColor.getColorValue(themeValue.borderColor));
                ShapeDrawable shapeDrawable = new ShapeDrawable(myStrokeShape);
                shapeDrawable.getPaint().setColor(MyColor.getColorValue(themeValue.backgroundColor));
                editText.setBackground(shapeDrawable);
            } else {
                editText.setBackground(null);
                editText.setBackgroundColor(MyColor.getColorValue(themeValue.backgroundColor));
            }

        } else {
            editText.setBackground(null);
        }
        return editText;
    }

    private ImageView newImageView(ImgViewArgs imgViewArgs) {
//        ImageView imgView = new ImageView(context);
        GifImageView imgView = new GifImageView(context);
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = imgViewArgs.getjId();
        tag.flip = imgViewArgs.getFlip();
        tag.clickthrough = imgViewArgs.getClickthrough();
//        imgView.setContentDescription(tag.jId);
        imgView.setTag(tag);

        ImgViewArgs.ImageDrawable drawable = imgViewArgs.getImgDrawable();
        if (drawable != null) {
            if (!drawable.imgPath.endsWith(".gif")) {
                imgView.setImageBitmap(BitmapFactory.decodeFile(Constant.IMG_RES_DIR + "/" + drawable.imgPath));
            } else {
                try {
                    imgView.setImageDrawable(new GifDrawable(Constant.IMG_RES_DIR + "/" + drawable.imgPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            imgView.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(Constant.IMG_RES_DIR + "/" + drawable.imgPath)));
        }
        imgView.setOnClickListener(mImgOnClickListener);
        imgView.setOnTouchListener(mImgOnTouchListener);
        return imgView;
    }

    private Button newButtonView(ButtonArgs btnArgs) {
        Button button = new Button(context, null, 0);
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = btnArgs.getjId();
        tag.sid = btnArgs.getS();
        tag.flip = btnArgs.getFlip();
        tag.cmd = btnArgs.getCmd();
        tag.micro = btnArgs.getMicro();
        button.setTag(tag);
        //button.setGravity(Gravity.CENTER_HORIZONTAL);
//        button.setPadding(0, 0, 0, 0);
        //button.setText(btnArgs.getText());

        ButtonArgs.Content btnContent = null;
        if (btnArgs.isSelected()) {
            btnContent = btnArgs.getActiveContent();
        } else {
            btnContent = btnArgs.getInactiveContent();
        }
        if (btnContent != null) {
            //button.setCompoundDrawablesWithIntrinsicBounds(btnContent.imgLeft, btnContent.imgTop, btnContent.imgRight, btnContent.imgBottom);
            String filePath = Constant.IMG_RES_DIR + "/" + btnContent.imgPath;
            //BitmapFactory.decodeFile(Tools.IMG_RES_DIR+"/"+drawable.imgPath)
            Drawable topDraw = new BitmapDrawable(filePath);
            int right = (int) (btnContent.imgW * properties.getLayoutWithRatio());
            int bottom = (int) (btnContent.imgH * properties.getLayoutHightRatio());
//			int right = (int) (btnContent.imgW);
//			int bottom = (int) (btnContent.imgH);
            topDraw.setBounds(0, 0, right, bottom);

            //button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, topDraw, null, null);
            button.setCompoundDrawables(null, topDraw, null, null);
            button.setText(btnContent.text);
        }
        Theme theme = btnArgs.getTheme();
        if (theme != null) {
            Theme.Value activeThemeValue = theme.getActiveValue();
            Theme.Value inactiveThemeValue = theme.getInactiveValue();
            int[][] states = new int[3][];
            states[0] = new int[]{android.R.attr.state_selected};
            states[1] = new int[]{android.R.attr.state_pressed};
            states[2] = new int[]{};
            int[] colors = new int[3];
            StateListDrawable sld = new StateListDrawable();
            if (activeThemeValue != null) {
                colors[0] = colors[1] = MyColor.getColorValue(activeThemeValue.fontColor);
                if (btnArgs.isMockPress()) {
                    sld.addState(new int[]{android.R.attr.state_pressed},
                            new BitmapDrawable(Constant.IMG_RES_DIR + "/" + activeThemeValue.backgoundImg));
                }
                sld.addState(new int[]{android.R.attr.state_selected},
                        new BitmapDrawable(Constant.IMG_RES_DIR + "/" + activeThemeValue.backgoundImg));
//				}

            }
            if (inactiveThemeValue != null) {
                button.setTextSize(TypedValue.COMPLEX_UNIT_PX, inactiveThemeValue.fontSize * properties.getTextSizeRatio());
//				colors[1] = MyColor.getColorValue(inactiveThemeValue.fontColor);
                if (btnArgs.getSim() == 0) {
                    colors[1] = MyColor.getColorValue(inactiveThemeValue.fontColor);
                }
                colors[2] = MyColor.getColorValue(inactiveThemeValue.fontColor);
                sld.addState(new int[]{}, new BitmapDrawable(Constant.IMG_RES_DIR + "/" + inactiveThemeValue.backgoundImg));
                button.setGravity(inactiveThemeValue.text_align | inactiveThemeValue.vertical_align);
                button.setTypeface(Typeface.create(inactiveThemeValue.font_family, inactiveThemeValue.fontweight));
                if (inactiveThemeValue.underline) {
                    button.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                    button.getPaint().setAntiAlias(true);//抗锯齿
                }
                button.setPadding((int) (inactiveThemeValue.paddingLeft * properties.getLayoutWithRatio()), (int) (inactiveThemeValue.paddingTop * properties.getLayoutHightRatio()), (int) (inactiveThemeValue.paddingRight * properties.getLayoutWithRatio()), (int) (inactiveThemeValue.paddingBottom * properties.getLayoutHightRatio()));
            }
            ColorStateList csl = new ColorStateList(states, colors);
            button.setTextColor(csl);
            button.setBackground(sld);
        }
        button.setOnClickListener(v -> {
            if (btnArgs.isLockPressed())
                v.setSelected(!v.isSelected());
            mBtnOnClickListener.onClick(v);
        });
        button.setOnLongClickListener(v -> false);

        //ColorUtils.RGBToHSL(arg0, arg1, arg2, arg3);
        //android.graphics.MyColor color = new android.graphics.MyColor();
        //ColorStateList csl = new ColorStateList(states, colors)
        button.setOnTouchListener(mBtnOnTouchListener);
//		button=new Button(context);
//		button.setText("hello");
        return button;
    }

    private ProgressBar newProgressBar(GaugeArgs gaugeArgs) {
        ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = gaugeArgs.getjId();
        pb.setTag(tag);
        Theme theme = gaugeArgs.getTheme();
        if (theme != null) {
            String backgoundImgPro = theme.getActiveValue().backgoundImg;
            String backgoundImgBg = theme.getInactiveValue().backgoundImg;
            Drawable drawablePro = Drawable.createFromPath(Constant.IMG_RES_DIR + "/" + backgoundImgPro);
            Drawable drawableBg = Drawable.createFromPath(Constant.IMG_RES_DIR + "/" + backgoundImgBg);
            ClipDrawable clipDrawablePro;
            if (gaugeArgs.getW() > gaugeArgs.getH())
                clipDrawablePro = new ClipDrawable(drawablePro, Gravity.START, ClipDrawable.HORIZONTAL);
            else
                clipDrawablePro = new ClipDrawable(drawablePro, Gravity.BOTTOM, ClipDrawable.VERTICAL);
            if (drawableBg != null) {
                LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawableBg, clipDrawablePro});
                layerDrawable.setId(0, android.R.id.background);
                layerDrawable.setId(1, android.R.id.progress);
                pb.setProgressDrawable(layerDrawable);
            }
        }
        pb.setMax(gaugeArgs.getmMax());
//        pb.setProgress(20);
        return pb;
    }

    private SeekBar newSeekBar(SliderArgs sliderArgs) {
        SeekBar sb = new SeekBar(context);
        if (sliderArgs.getW() <= sliderArgs.getH()) {
            Log.e(TAG, "sliderArgs.getW():" + sliderArgs.getW() + ",sliderArgs.getH()" + sliderArgs.getH());
            sb = new VerticalSeekBar(context);
        }
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = sliderArgs.getjId();
        sb.setTag(tag);
//        sb.setContentDescription(tag.jId);
        sb.setMax(sliderArgs.getmMax());
        Theme theme = sliderArgs.getTheme();
        if (theme != null) {
            String backgoundImgPro = theme.getActiveValue().backgoundImg;
            String backgoundImgBg = theme.getInactiveValue().backgoundImg;
            Drawable drawablePro = Drawable.createFromPath(Constant.IMG_RES_DIR + "/" + backgoundImgPro);
            Drawable drawableBg = getScaleDrawable(backgoundImgBg, sliderArgs.getW(), sliderArgs.getH());
            if (sliderArgs.getW() < sliderArgs.getH()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap bitmap = ((BitmapDrawable) drawablePro).getBitmap();
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                drawablePro = new BitmapDrawable(context.getResources(), bitmap1);

                Bitmap bitmapbg = ((BitmapDrawable) drawableBg).getBitmap();
                Bitmap bitmapbg1 = Bitmap.createBitmap(bitmapbg, 0, 0, bitmapbg.getWidth(), bitmapbg.getHeight(), matrix, true);
                drawableBg = new BitmapDrawable(context.getResources(), bitmapbg1);
            }
            ClipDrawable clipDrawablePro = new ClipDrawable(drawablePro, Gravity.START, ClipDrawable.HORIZONTAL);
//            if(sliderArgs.getW()<sliderArgs.getH()){
//
//            }
//                clipDrawablePro = new ClipDrawable(drawablePro, Gravity.START, ClipDrawable.HORIZONTAL);
//            else clipDrawablePro = new ClipDrawable(drawablePro, Gravity.BOTTOM, ClipDrawable.VERTICAL);
            if (drawableBg != null) {
                LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawableBg, clipDrawablePro});
                layerDrawable.setId(0, android.R.id.background);
                layerDrawable.setId(1, android.R.id.progress);
                sb.setProgressDrawable(layerDrawable);
            }
        }
//        else {
//            Drawable drawable = sb.getProgressDrawable();
//            if(drawable instanceof LayerDrawable){
//                LayerDrawable layerDrawable = (LayerDrawable) drawable;
//                Drawable drawable0 = layerDrawable.getDrawable(0);
//                Bitmap bitmap = ((BitmapDrawable) drawable0).getBitmap();
//                Bitmap scale = Bitmap.createScaledBitmap(bitmap, (int) (sliderArgs.getW() * Properties.getInstant().getLayoutWithRatio()), (int) (sliderArgs.getH() * Properties.getInstant().getLayoutHightRatio()), false);
//                layerDrawable.setDrawableByLayerId(android.R.id.background,new BitmapDrawable(scale));
//                sb.setProgressDrawable(layerDrawable);
//            }
//        }
        SliderArgs.Indicator indicator = sliderArgs.getIndicator();
        SliderArgs.Indicator activeindicator = sliderArgs.getActiveindicator();
        StateListDrawable sld = new StateListDrawable();
        boolean hasThumb = false;
        if (activeindicator != null) {
            sld.addState(new int[]{android.R.attr.state_pressed}, getBDBypath(sliderArgs, activeindicator.imgPath));
            if (indicator == null) {
                sld.addState(new int[]{}, getBDBypath(sliderArgs, activeindicator.imgPath));
            }
            hasThumb = true;
        }
        if (indicator != null) {
            sld.addState(new int[]{}, getBDBypath(sliderArgs, indicator.imgPath));
            hasThumb = true;
        }
        if (hasThumb) {
            sb.setThumb(sld);
            LogUtils.e(TAG, "sld bound:" + sld.getBounds().width() + ",height:" + sld.getBounds().height());
            sb.setThumbOffset(0);
            if (sliderArgs.getW() > sliderArgs.getH()) {
                sb.setMinimumHeight((sld.getBounds().height()));
            } else {
                sb.setMinimumWidth((sld.getBounds().width()));
            }
        }
//        if (indicator != null) {
//            String drawPath = Constant.IMG_RES_DIR + "/" + indicator.imgPath;
//            Bitmap srcBmp = BitmapFactory.decodeFile(drawPath);
////			sb.setThumb(new BitmapDrawable(srcBmp));
//            if (srcBmp != null) {
//                Bitmap dstBmp = Bitmap.createScaledBitmap(srcBmp, (int) (srcBmp.getWidth() * Properties.getInstant().getLayoutWithRatio()), (int) (srcBmp.getHeight() * Properties.getInstant().getLayoutHightRatio()), false);
//                BitmapDrawable thumb = new BitmapDrawable(context.getResources(), dstBmp);
////                thumb.setBounds(0,0,(int)(srcBmp.getWidth()*Properties.getInstant().getLayoutWithRatio()), (int)(sliderArgs.getH()*Properties.getInstant().getLayoutWithRatio()));
//                sb.setThumb(thumb);
//                sb.setThumbOffset(-(int) (srcBmp.getWidth() * Properties.getInstant().getLayoutWithRatio() / 20));
//            }
//        }
        sb.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        return sb;
    }

    private Drawable getScaleDrawable(String imgPath, int width, int height) {
        String drawPath = Constant.IMG_RES_DIR + "/" + imgPath;
        if (new File(drawPath).exists()) {
            Bitmap srcBmp = BitmapFactory.decodeFile(drawPath);
            if (srcBmp != null) {
                Bitmap dstB = Bitmap.createScaledBitmap(srcBmp, (int) (width * Properties.getInstant().getLayoutWithRatio()), (int) (height * Properties.getInstant().getLayoutHightRatio()), false);
                return new BitmapDrawable(context.getResources(), dstB);
            }
        }
        return null;
    }

    private BitmapDrawable getBDBypath(SliderArgs sliderArgs, String imgPath) {
        String drawPath = Constant.IMG_RES_DIR + "/" + imgPath;
        Bitmap srcBmp = BitmapFactory.decodeFile(drawPath);
        if (srcBmp != null) {
            Bitmap dstBmp;
            Bitmap dstB = Bitmap.createScaledBitmap(srcBmp, (int) (srcBmp.getWidth() * Properties.getInstant().getLayoutWithRatio()), (int) (srcBmp.getHeight() * Properties.getInstant().getLayoutHightRatio()), false);
            if (sliderArgs.getW() < sliderArgs.getH()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                dstBmp = Bitmap.createBitmap(dstB, 0, 0, dstB.getWidth(), dstB.getHeight(), matrix, true);
            } else dstBmp = dstB;
//            Bitmap dstBmp = scaleBitmap(srcBmp,Properties.getInstant().getLayoutWithRatio());
            BitmapDrawable thumb = new BitmapDrawable(context.getResources(), dstBmp);
            LogUtils.e(TAG, "srcBmp width:" + srcBmp.getWidth() + ",height:" + srcBmp.getHeight());
            LogUtils.e(TAG, "dstBmp width:" + dstBmp.getWidth() + ",height:" + dstBmp.getHeight());
            return thumb;
        }
        return null;
    }

    private Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    //竖直进度条
    private VerticalSeekBar newVerticalProgressBar(GaugeArgs gaugeArgs) {
        VerticalSeekBar verticalSeekBar = new VerticalSeekBar(context);
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = gaugeArgs.getjId();
//        verticalSeekBar.setContentDescription(tag.jId);
        verticalSeekBar.setTag(tag);
        verticalSeekBar.setMax(gaugeArgs.getmMax());
        verticalSeekBar.setThumb(null);
        return verticalSeekBar;
    }

    //竖直拖动条
    private VerticalSeekBar newVerticalSeekBar(SliderArgs sliderArgs) {
        VerticalSeekBar verticalSeekBar = new VerticalSeekBar(context);
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = sliderArgs.getjId();
        verticalSeekBar.setTag(tag);
        verticalSeekBar.setMax(sliderArgs.getmMax());
        SliderArgs.Indicator indicator = sliderArgs.getIndicator();
        if (indicator != null) {
            String drawPath = Constant.IMG_RES_DIR + "/" + indicator.imgPath;
            Bitmap srcBmp = BitmapFactory.decodeFile(drawPath);
            Bitmap dstBmp = Bitmap.createScaledBitmap(srcBmp, sliderArgs.getH(), sliderArgs.getH(), false);
            verticalSeekBar.setThumb(new BitmapDrawable(dstBmp));
        } else {
            verticalSeekBar.setThumb(null);
        }
        //verticalSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        return verticalSeekBar;
    }


    private WebView newWebview(WebViewArgs webArgs) {
        WebView webView = new WebView(context);
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = webArgs.getjId();
        tag.sid = webArgs.getRefreshJid();
        webView.setTag(tag);
        webView.loadUrl(webArgs.getUrl());
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        //ws.setCacheMode(mode);
//        ws.setSupportZoom(true);
//        ws.setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();//忽略证书的错误继续Load页面内容，不会显示空白页面
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        Layouts.getInstance().getLiandongMap().put(webView, new String[]{webArgs.getBackJid(), webArgs.getForwardJid(), webArgs.getRefreshJid(), webArgs.getStopJid()});
        return webView;
    }

    //	private VideoView newVideoView(VideoArgs videoArgs){
//		VideoView vv = new VideoView(context);
//		
//		return vv;
//	}
    private MyVideoView newSurfaceView(VideoArgs videoArgs) {
        MyVideoView surfaceView = new MyVideoView(context);
        surfaceView.setUrl(videoArgs.getUrl());
        Layouts.getInstance().getLiandongMap().put(surfaceView, new String[]{videoArgs.getPlayJid(), videoArgs.getStopJid()});
        return surfaceView;
    }

    //    private SurfaceView newJkVideoView(VideoArgs videoArgs) {
//        SurfaceView surfaceView = new SurfaceView(context);
//        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
//
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
//
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1);
//
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
//
//        ijkMediaPlayer.setVolume(1.0f, 1.0f);
//
//        setEnableMediaCodec(ijkMediaPlayer,true);
//        ijkMediaPlayer.setDisplay(surfaceView.getHolder());
//        try {
//            ijkMediaPlayer.setDataSource(videoArgs.getUrl());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ijkMediaPlayer.prepareAsync();
//        ijkMediaPlayer.start();
//        return surfaceView;
//    }

    private View newPlVideoView(VideoArgs videoArgs) {
        PLVideoView plVideoView = new PLVideoView(context);
        plVideoView.setVideoPath(videoArgs.getUrl());
        plVideoView.setOnPreparedListener(new PLOnPreparedListener() {
            @Override
            public void onPrepared(int i) {

            }
        });
        plVideoView.start();
        return plVideoView;
    }

    private IjkVideoView newJkVideoView(VideoArgs videoArgs) {
        IjkVideoView videoView = new IjkVideoView(context);
        ViewArgs.Tag tag = new ViewArgs.Tag();
        tag.jId = videoArgs.getjId();
        tag.sid = videoArgs.getS();
        videoView.setTag(tag);
        videoView.setVideoPath(videoArgs.getUrl());
        Layouts.getInstance().getLiandongMap().put(videoView, new String[]{videoArgs.getPlayJid(), videoArgs.getStopJid()});
        return videoView;
    }


//    //设置是否开启硬解码
//    private void setEnableMediaCodec(IjkMediaPlayer ijkMediaPlayer, boolean isEnable) {
//        int value = isEnable ? 1 : 0;
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);//开启硬解码
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
//    }

    private VideoView newVideoView(VideoArgs videoArgs) {
//        IjkVideoView videoView = new IjkMediaPlayer();
        VideoView videoView = new VideoView(context) {
            @Override
            protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
                super.onVisibilityChanged(changedView, visibility);
                if (visibility == VISIBLE) {
                    resume();
                    start();
                } else {
                    pause();
                    stopPlayback();
                }
            }
        };
        Uri uri = Uri.parse(videoArgs.getUrl());
        videoView.setVideoURI(uri);
        Layouts.getInstance().getLiandongMap().put(videoView, new String[]{videoArgs.getPlayJid(), videoArgs.getStopJid()});
        return videoView;
    }


    private class MyViewOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
//            if (view.getTag() != null) {
//                ViewArgs.Tag argTag = (ViewArgs.Tag) view.getTag();
//                String jId = argTag.jId;
//                Log.i(TAG, "onClick-->jId=" + jId);
//                Toast.makeText(view.getContext(), "jId=" + jId, Toast.LENGTH_SHORT).show();
//            }
            if (ThirdActivity.myBtnOnClickListener != null)
                ThirdActivity.myBtnOnClickListener.onClick(view);
        }
    }

    private class MyViewOnTouchListener implements OnTouchListener {

        private float lastX;
        private float lastY;
        private RectF rectF;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            if (ThirdActivity.myBtnOnTouchListener != null)
                ThirdActivity.myBtnOnTouchListener.onTouch(view, event);
            int action = event.getAction();
            ViewArgs.Tag argTag = (ViewArgs.Tag) view.getTag();
            String sid = argTag.sid;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    rectF = new RectF(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    Log.i(TAG, "onTouch-->down, jId=" + sid);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if ("10000".equals(sid)) {
//                        if (cacheView == null) {
//                            cacheView = new ImageView(context);
//                            cacheView.setImageBitmap(view.getDrawingCache());
//                            if (view.getParent() instanceof MyAbsoluteLayout) {
//                                ((MyAbsoluteLayout) view.getParent()).addView(cacheView);
//                            }
//                            layoutParams = (AbsoluteLayout.LayoutParams) view.getLayoutParams();
//                        }
//                        int x = layoutParams.x + (int) (event.getX() - lastX);
//                        int y = layoutParams.y + (int) (event.getY() - lastY);
//                        cacheView.setLayoutParams(new AbsoluteLayout.LayoutParams(layoutParams.width, layoutParams.height, x, y));
                        float xoff = event.getRawX() - lastX;
                        float yoff = event.getRawY() - lastY;
                        view.layout((int) (view.getLeft() + xoff), (int) (view.getTop() + yoff), (int) (view.getRight() + xoff), (int) (view.getBottom() + yoff));
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(TAG, "onTouch-->up, jId=" + sid);
                    if ("10000".equals(sid)) {
                        int childCount = ((ViewGroup) view.getParent()).getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View child = ((ViewGroup) view.getParent()).getChildAt(i);
                            if (child.getTag() instanceof ViewArgs.Tag) {
                                try {
                                    if (Integer.parseInt(((ViewArgs.Tag) child.getTag()).sid) > 500) {
                                        if (new RectF(child.getLeft(), child.getTop(), child.getRight(), child.getBottom()).contains(event.getRawX(), event.getRawY())) {
                                            child.performClick();
                                            break;
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (rectF != null) {
                            view.layout((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                        }
                    }
                    break;
            }
            return false;
        }
    }


    private class MyImgViewOnTouchListener implements OnTouchListener {

        private float lastX;
        private float lastY;
        private RectF rectF;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getAction();
            ViewArgs.Tag argTag = (ViewArgs.Tag) view.getTag();
            String clickthrough = argTag.clickthrough;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    rectF = new RectF(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    Log.i(TAG, "onTouch-->down, clickthrough=" + clickthrough);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if ("1".equals(clickthrough)) {
//                        if (cacheView == null) {
//                            cacheView = new ImageView(context);
//                            cacheView.setImageBitmap(view.getDrawingCache());
//                            if (view.getParent() instanceof MyAbsoluteLayout) {
//                                ((MyAbsoluteLayout) view.getParent()).addView(cacheView);
//                            }
//                            layoutParams = (AbsoluteLayout.LayoutParams) view.getLayoutParams();
//                        }
//                        int x = layoutParams.x + (int) (event.getX() - lastX);
//                        int y = layoutParams.y + (int) (event.getY() - lastY);
//                        cacheView.setLayoutParams(new AbsoluteLayout.LayoutParams(layoutParams.width, layoutParams.height, x, y));
                        float xoff = event.getRawX() - lastX;
                        float yoff = event.getRawY() - lastY;
                        view.layout((int) (view.getLeft() + xoff), (int) (view.getTop() + yoff), (int) (view.getRight() + xoff), (int) (view.getBottom() + yoff));
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(TAG, "onTouch-->up, clickthrough=" + clickthrough);
                    if ("1".equals(clickthrough)) {
//                        if (cacheView != null) {
//                            ((MyAbsoluteLayout) view.getParent()).removeView(cacheView);
//                            cacheView = null;
//                        }
                        int childCount = ((ViewGroup) view.getParent()).getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View child = ((ViewGroup) view.getParent()).getChildAt(i);
                            if (child.getTag() instanceof ViewArgs.Tag) {
                                if ("0".equals(((ViewArgs.Tag) child.getTag()).clickthrough)) {
                                    if (new RectF(child.getLeft(), child.getTop(), child.getRight(), child.getBottom()).contains(event.getRawX(), event.getRawY())) {
                                        child.performClick();
                                        break;
                                    }
                                }
                            }
                        }
                        if (rectF != null) {
                            view.layout((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                        }
                    }
                    break;
            }
            return false;
        }
    }

    private class MySeekbarChangeListener implements OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (ThirdActivity.mySeekbarChangeListener != null)
                ThirdActivity.mySeekbarChangeListener.onProgressChanged(seekBar, progress, fromUser);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (ThirdActivity.mySeekbarChangeListener != null)
                ThirdActivity.mySeekbarChangeListener.onStartTrackingTouch(seekBar);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (ThirdActivity.mySeekbarChangeListener != null)
                ThirdActivity.mySeekbarChangeListener.onStopTrackingTouch(seekBar);
        }
    }

    private static class MyOnEditorActionListener implements OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            ((ViewGroup) v.getParent()).requestFocus();
            InputMethodManager manager = ((InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
            if (manager != null) manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            if (ThirdActivity.myOnEditorActionListener != null)
                return ThirdActivity.myOnEditorActionListener.onEditorAction(v, actionId, event);
            return false;
        }
    }
}
