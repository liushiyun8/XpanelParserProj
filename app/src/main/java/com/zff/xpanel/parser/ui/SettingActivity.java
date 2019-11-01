package com.zff.xpanel.parser.ui;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.zff.xpanel.parser.R;
import com.zff.xpanel.parser.util.Properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingActivity extends BaseActivity {

    private Spinner mSpinner;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void iniView() {

        mSpinner = findViewById(R.id.spinner);
        findViewById(R.id.textView_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.textView_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void iniData() {
        Properties.LayoutMode mode = Properties.getInstant().getLayoutMode();
        if(mode != null){
            mSpinner.setPrompt(mode.value);
        }else {
            mSpinner.setPrompt("请选择");
        }
        final Properties.LayoutMode[] modes = Properties.LayoutMode.values();
        List<Map<String, String >> list = new ArrayList<>();
        for(int i=0; i<modes.length; i++){
            Map<String, String > map = new HashMap<>();
            map.put("modeV", modes[i].value);
            list.add(map);
        }
        SpinnerAdapter spinnerAdapter = new SimpleAdapter(this, list, android.R.layout.simple_spinner_item, new String[]{"modeV"}, new int[]{android.R.id.text1});
        mSpinner.setAdapter(spinnerAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Properties.getInstant().saveConfigLayoutMode(modes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void test(){
        SeekBar seekBar = null;
    }
}
