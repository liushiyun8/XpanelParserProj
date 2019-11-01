package com.zff.xpanel.parser.ui;

import com.zff.xpanel.parser.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SecondActivity extends Activity{

	private final String TAG = "SecondActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		LinearLayout container = (LinearLayout)findViewById(R.id.second_layout);
		//container.addView(child);
		addViewTest(container);
		
//		PageXmlParser xmlPsr = new PageXmlParser();
//		xmlPsr.parse("/mnt/sdcard/a_zff/demo.gui-密码输入", "demo.gui-demo.gui");
//		Map<String, Page> map = xmlPsr.getMapPage();
//		Page page = map.get("password");
//		
//		InflaterPage inflaterPage = new InflaterPage(this, page);
//		container.addView(inflaterPage.getAbsoluteLayout(page));
		
//		Page page = null;
//		Map<String, Page> map = new HashMap<String, Page>();
//		page = new Page();
//		map.put("aa", page);
//		ViewArgs va = new ViewArgs();
//		va.text = "va-";
//		page.add(va);
//		ViewArgs vv = new ViewArgs();
//		vv.text = "vv-";
//		page.add(vv);
//		
//		page = new Page();
//		map.put("bb", page);
//		ViewArgs vb = new ViewArgs();
//		va.text = "vb-";
//		page.add(vb);
//		ViewArgs vvb = new ViewArgs();
//		vv.text = "vvb-";
//		page.add(vvb);
//		
//		int size = map.size();
//		Log.i(TAG, "SIZE"+size);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	private void addViewTest(LinearLayout layout){
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(300, 50);
		TextView tv = new TextView(this);
		tv.setText("fajifjesljflsjf = "+layout.getChildCount());
		
		layout.addView(tv, 2, ll);
	}

}
