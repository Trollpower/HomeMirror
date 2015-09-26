package com.morristaedt.mirror;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.morristaedt.mirror.configuration.ConfigurationSettings;

public class SetUpActivity extends Activity {

    @NonNull
    private ConfigurationSettings mConfigSettings;

    private CheckBox mMoodDetectionCheckbox;
    private CheckBox mShowNextCaledarEventCheckbox;
    private CheckBox mShowNewsHeadlineCheckbox;
    private CheckBox mXKCDCheckbox;
    private CheckBox mXKCDInvertCheckbox;
    private EditText mLatitude;
    private EditText mLongitude;
    private EditText mStockTickerSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        mConfigSettings = new ConfigurationSettings(this);

        mMoodDetectionCheckbox = (CheckBox) findViewById(R.id.mood_detection_checkbox);
        mMoodDetectionCheckbox.setChecked(mConfigSettings.showMoodDetection());

        mShowNextCaledarEventCheckbox = (CheckBox) findViewById(R.id.calendar_checkbox);
        mShowNextCaledarEventCheckbox.setChecked(mConfigSettings.showNextCalendarEvent());

        mShowNewsHeadlineCheckbox = (CheckBox) findViewById(R.id.headline_checkbox);
        mShowNewsHeadlineCheckbox.setChecked(mConfigSettings.showNewsHeadline());

        mXKCDCheckbox = (CheckBox) findViewById(R.id.xkcd_checkbox);
        mXKCDCheckbox.setChecked(mConfigSettings.showXKCD());

        mXKCDInvertCheckbox = (CheckBox) findViewById(R.id.xkcd_invert_checkbox);
        mXKCDInvertCheckbox.setChecked(mConfigSettings.invertXKCD());

        mLatitude = (EditText) findViewById(R.id.latitude);
        mLongitude = (EditText) findViewById(R.id.longitude);

        mLatitude.setText(String.valueOf(mConfigSettings.getLatitude()));
        mLongitude.setText(String.valueOf(mConfigSettings.getLongitude()));

        mStockTickerSymbol = (EditText) findViewById(R.id.stock_name);
        mStockTickerSymbol.setText(mConfigSettings.getStockTickerSymbol());

        findViewById(R.id.launch_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFields();
                Intent intent = new Intent(SetUpActivity.this, MirrorActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveFields() {
        mConfigSettings.setShowMoodDetection(mMoodDetectionCheckbox.isChecked());
        mConfigSettings.setShowNextCalendarEvent(mShowNextCaledarEventCheckbox.isChecked());
        mConfigSettings.setShowNewsHeadline(mShowNewsHeadlineCheckbox.isChecked());
        mConfigSettings.setXKCDPreference(mXKCDCheckbox.isChecked(), mXKCDInvertCheckbox.isChecked());
        mConfigSettings.setLatLon(mLatitude.getText().toString(), mLongitude.getText().toString());
        mConfigSettings.setStockTickerSymbol(mStockTickerSymbol.getText().toString());
    }
}