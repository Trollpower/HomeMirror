package com.morristaedt.mirror;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.morristaedt.mirror.configuration.ConfigurationSettings;
import com.morristaedt.mirror.modules.CalendarModule;
import com.morristaedt.mirror.modules.DayModule;
import com.morristaedt.mirror.modules.SubstitutionModule;
import com.morristaedt.mirror.modules.XKCDModule;
import com.morristaedt.mirror.receiver.AlarmReceiver;
import com.morristaedt.mirror.requests.SubstitutionData;
import com.morristaedt.mirror.requests.SubstitutionRow;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class MirrorActivity extends ActionBarActivity {

    @NonNull
    private ConfigurationSettings mConfigSettings;

    private TextView mDayText;
    private ImageView mXKCDImage;
    private TextView mCalendarTitleText;
    private TextView mCalendarDetailsText;
    private TextView mSubstitutionText;
    private ListView mListView;
    private TextView mAnnouncementLabel;
    private ListView mListViewAnnouncements;

    private XKCDModule.XKCDListener mXKCDListener = new XKCDModule.XKCDListener() {
        @Override
        public void onNewXKCDToday(String url) {
            if (TextUtils.isEmpty(url)) {
                mXKCDImage.setVisibility(View.GONE);
            } else {
                Picasso.with(MirrorActivity.this).load(url).into(mXKCDImage);
                mXKCDImage.setVisibility(View.VISIBLE);
            }
        }
    };

    private CalendarModule.CalendarListener mCalendarListener = new CalendarModule.CalendarListener() {
        @Override
        public void onCalendarUpdate(String title, String details) {
            mCalendarTitleText.setVisibility(title != null ? View.VISIBLE : View.GONE);
            mCalendarTitleText.setText(title);
            mCalendarDetailsText.setVisibility(details != null ? View.VISIBLE : View.GONE);
            mCalendarDetailsText.setText(details);

            //Make marquee effect work for long text
            mCalendarTitleText.setSelected(true);
            mCalendarDetailsText.setSelected(true);
        }
    };

    private SubstitutionModule.SubstitutionListener mSubstitutionListener = new SubstitutionModule.SubstitutionListener() {
        @Override
        public void onNewSubstitution(SubstitutionData substitutionData) {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            String datum = df.format(substitutionData.getRequestedDate());
            if(!substitutionData.isSubstitutionFound()) {

                mSubstitutionText.setText("Keine Ausfälle oder Vertretungen für "+ datum +" gefunden");
                mListView.setVisibility(View.INVISIBLE);
                mAnnouncementLabel.setVisibility(View.INVISIBLE);
                mListViewAnnouncements.setVisibility(View.INVISIBLE);
            }
            else{
                MySimpleArrayAdapter substitutionAdapter = new MySimpleArrayAdapter(
                                MirrorApplication.getContext(),
                                substitutionData.getSubstitutionPlan());

                mAnnouncementLabel.setVisibility(View.VISIBLE);
                mListViewAnnouncements.setVisibility(View.VISIBLE);
                mSubstitutionText.setText("Vertretungsplan vom " + datum);
                mListView.setAdapter(substitutionAdapter);
                ArrayAdapter adapter = new ArrayAdapter<String>(MirrorApplication.getContext(), R.layout.announcement_listview, substitutionData.getAnnouncements());
                mListViewAnnouncements.setAdapter(adapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror);
        mConfigSettings = new ConfigurationSettings(this);
        AlarmReceiver.startMirrorUpdates(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mDayText = (TextView) findViewById(R.id.day_text);
        mXKCDImage = (ImageView) findViewById(R.id.xkcd_image);
        mCalendarTitleText = (TextView) findViewById(R.id.calendar_title);
        mCalendarDetailsText = (TextView) findViewById(R.id.calendar_details);

        if (mConfigSettings.invertXKCD()) {
            //Negative of XKCD image
            float[] colorMatrixNegative = {
                    -1.0f, 0, 0, 0, 255, //red
                    0, -1.0f, 0, 0, 255, //green
                    0, 0, -1.0f, 0, 255, //blue
                    0, 0, 0, 1.0f, 0 //alpha
            };
            ColorFilter colorFilterNegative = new ColorMatrixColorFilter(colorMatrixNegative);
            mXKCDImage.setColorFilter(colorFilterNegative); // not inverting for now
        }

        mSubstitutionText = (TextView) findViewById(R.id.substitutionText);
        mAnnouncementLabel = (TextView) findViewById(R.id.mitteilungen_header);
        mListView = (ListView) findViewById(R.id.listView);
        mListViewAnnouncements = (ListView) findViewById(R.id.listViewAnnouncements);

        TextView tvVersionName = (TextView)findViewById(R.id.versioninfo);
        tvVersionName.setText(BuildConfig.VERSION_NAME);

        setViewState();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setViewState();
    }

    private void setViewState() {
        mDayText.setText(DayModule.getDay());

        if (mConfigSettings.showXKCD()) {
            XKCDModule.getXKCDForToday(mXKCDListener);
        } else {
            mXKCDImage.setVisibility(View.GONE);
        }

        if (mConfigSettings.showNextCalendarEvent()) {
            CalendarModule.getCalendarEvents(this, mCalendarListener);
        } else {
            mCalendarTitleText.setVisibility(View.GONE);
            mCalendarDetailsText.setVisibility(View.GONE);
        }

        SubstitutionModule.getSubstitutions(mSubstitutionListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlarmReceiver.stopMirrorUpdates(this);
        Intent intent = new Intent(this, SetUpActivity.class);
        startActivity(intent);
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<SubstitutionRow> {
        private final Context context;
        private final ArrayList<SubstitutionRow> values;

        public MySimpleArrayAdapter(Context context, ArrayList<SubstitutionRow> values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_substitution, parent, false);
            TextView textViewLesson = (TextView) rowView.findViewById(R.id.lesson);
            textViewLesson.setText(values.get(position).getLesson() + ". St.");

            if(values.get(position).getRoom() != null && values.get(position).getRoom() != "") {
                TextView textViewRoom = (TextView) rowView.findViewById(R.id.room);
                textViewRoom.setText(values.get(position).getRoom());
                textViewRoom.setVisibility(View.VISIBLE);
            }
            else
            {
                TextView textViewRoom = (TextView) rowView.findViewById(R.id.room);
                textViewRoom.setVisibility(View.INVISIBLE);
            }

            if(values.get(position).getSubject() != null && values.get(position).getSubject() != "") {
                TextView textViewRoom = (TextView) rowView.findViewById(R.id.subject);
                textViewRoom.setText(values.get(position).getSubject());
                textViewRoom.setVisibility(View.VISIBLE);
            }
            else
            {
                TextView textViewRoom = (TextView) rowView.findViewById(R.id.subject);
                textViewRoom.setVisibility(View.INVISIBLE);
            }

            TextView textViewTeacher = (TextView) rowView.findViewById(R.id.teacher);
            textViewTeacher.setText(values.get(position).getTeacher());
            return rowView;
        }
    }


}
