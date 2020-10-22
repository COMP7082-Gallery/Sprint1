package com.example.sprintone.filter_gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sprintone.R;
import com.example.sprintone.numberfilter.InputFilterMinMax;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FilterGalleryActivity extends AppCompatActivity {

    private EditText startDate;
    private EditText endDate;
    private EditText tlLat;
    private EditText tlLon;
    private EditText brLat;
    private EditText brLon;
    private Date sDate;
    private Date eDate;
    private int dateLabel = 0;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    @BasicTrace("onCreate")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        startDate = findViewById(R.id.filter_start_date);
        endDate = findViewById(R.id.filter_end_date);
        tlLat = findViewById(R.id.tlLat);
        tlLon = findViewById(R.id.tlLon);
        brLat = findViewById(R.id.brLat);
        brLon = findViewById(R.id.brLon);

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(dateLabel);
        };

        startDate.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(FilterGalleryActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            dateLabel = 1;
        });

        endDate.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(FilterGalleryActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            dateLabel = 2;
        });

        int MAX_LAT = 90;
        int MIN_LAT = -90;
        tlLat.setFilters(new InputFilter[]{ new InputFilterMinMax(MIN_LAT, MAX_LAT)});
        brLat.setFilters(new InputFilter[]{ new InputFilterMinMax(MIN_LAT, MAX_LAT)});
        int MAX_LON = 180;
        int MIN_LON = -180;
        tlLon.setFilters(new InputFilter[]{ new InputFilterMinMax(MIN_LON, MAX_LON)});
        brLon.setFilters(new InputFilter[]{ new InputFilterMinMax(MIN_LON, MAX_LON)});
    }

    private void updateLabel(int label) {
        String myFormat = "MMM d, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (label == 1) {
            startDate.setText(sdf.format(myCalendar.getTime()));
            sDate = myCalendar.getTime();
        } else if (label == 2) {
            endDate.setText(sdf.format(myCalendar.getTime()));
            eDate = myCalendar.getTime();
            if ((startDate.getText() != null) && (sDate.getTime() > eDate.getTime())) {
                Toast.makeText(FilterGalleryActivity.this, "The end date should not be earlier than the start date",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onCancel(final View v) {
        finish();
    }

    public void onFilter(final View v) {
        Intent i = new Intent();
        EditText keywords = findViewById(R.id.filter_keyword);

        i.putExtra("STARTTIMESTAMP", startDate.getText() != null ? startDate.getText().toString() : "");
        i.putExtra("ENDTIMESTAMP", endDate.getText() != null ? endDate.getText().toString() : "");
        i.putExtra("TOPLEFTLATITUDE", tlLat.getText() != null ? tlLat.getText().toString() : "");
        i.putExtra("TOPLEFTLONGITUDE", tlLon.getText() != null ? tlLon.getText().toString() : "");
        i.putExtra("BTMRIGHTLATITUDE", brLat.getText() != null ? brLat.getText().toString() : "");
        i.putExtra("BTMRIGHTLONGITUDE", brLon.getText() != null ? brLon.getText().toString() : "");
        i.putExtra("KEYWORDS", keywords.getText() != null ? keywords.getText().toString() : "");
        setResult(RESULT_OK, i);
        finish();
    }
}
