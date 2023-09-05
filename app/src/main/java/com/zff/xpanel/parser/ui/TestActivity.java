package com.zff.xpanel.parser.ui;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.zff.xpanel.parser.R;

import org.videolan.vlc.widget.VerticalSeekBar;

public class TestActivity extends Activity {

    private static final String TAG = "Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_seekbar);
        VerticalSeekBar verBar = findViewById(R.id.verBar);
        verBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e(TAG,"fromUser:"+fromUser+",pro:"+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG,"onStopTrackingTouch:"+seekBar.getProgress());
            }
        });
    }
}
