package com.example.sprintone;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FilterGalleryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd");
            Date now = calendar.getTime();
            String todayStr = new SimpleDateFormat("yyyy‐MM‐dd", Locale.getDefault()).format(now);
            Date today = format.parse((String) todayStr);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String tomorrowStr = new SimpleDateFormat("yyyy‐MM‐dd", Locale.getDefault()).format( calendar.getTime());
            Date tomorrow = format.parse((String) tomorrowStr);
            ((EditText) findViewById(R.id.filter_start_date)).setText(new SimpleDateFormat(
                    "yyyy‐MM‐dd HH:mm:ss", Locale.getDefault()).format(today));
            ((EditText) findViewById(R.id.filter_end_date)).setText(new SimpleDateFormat(
                    "yyyy‐MM‐dd HH:mm:ss", Locale.getDefault()).format(tomorrow));
        } catch (Exception ex) { }
    }
    public void onCancel(final View v) {
        finish();
    }
    public void onFilter(final View v) {
        Intent i = new Intent();
        EditText from = (EditText) findViewById(R.id.filter_start_date);
        EditText to = (EditText) findViewById(R.id.filter_end_date);
        //EditText tlLat = (EditText) findViewById(R.id.tlLat);
        //EditText tlLon = (EditText) findViewById(R.id.tlLon);
        //EditText brLat = (EditText) findViewById(R.id.brLat);
        //EditText brLon = (EditText) findViewById(R.id.brLon);
        EditText keywords = (EditText) findViewById(R.id.filter_keyword);

        i.putExtra("STARTTIMESTAMP", from.getText() != null ? from.getText().toString() : "");
        i.putExtra("ENDTIMESTAMP", to.getText() != null ? to.getText().toString() : "");
        //i.putExtra("TOPLEFTLATITUDE", tlLat.getText() != null ? to.getText().toString() : "");
        //i.putExtra("TOPLEFTLONGITUDE", tlLon.getText() != null ? to.getText().toString() : "");
        //i.putExtra("BTMRIGHTLATITUDE", brLat.getText() != null ? to.getText().toString() : "");
        //i.putExtra("BTMRIGHTLONGITUDE", brLon.getText() != null ? to.getText().toString() : "");
        i.putExtra("KEYWORDS", keywords.getText() != null ? keywords.getText().toString() : "");
        setResult(RESULT_OK, i);
        finish();
    }
}
