package com.example.sprintone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FilterGalleryActivity extends AppCompatActivity {

    private EditText startDate;
    private EditText endDate;
    private int dateLabel = 0;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        startDate = findViewById(R.id.filter_start_date);
        endDate = findViewById(R.id.filter_end_date);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(dateLabel);
            }
        };

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(FilterGalleryActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                dateLabel = 1;
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(FilterGalleryActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                dateLabel = 2;
            }
        });
    }

    private void updateLabel(int label) {
        String myFormat = "MMM d, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (label == 1){
            startDate.setText(sdf.format(myCalendar.getTime()));
        } else if (label == 2){
            endDate.setText(sdf.format(myCalendar.getTime()));
        }
    }

    public void onCancel(final View v) {
        finish();
    }

    public void onFilter(final View v) {
        Intent i = new Intent();
        EditText from = findViewById(R.id.filter_start_date);
        EditText to = findViewById(R.id.filter_end_date);
        EditText tlLat = findViewById(R.id.tlLat);
        EditText tlLon = findViewById(R.id.tlLon);
        EditText brLat = findViewById(R.id.brLat);
        EditText brLon = findViewById(R.id.brLon);
        EditText keywords = findViewById(R.id.filter_keyword);

        i.putExtra("STARTTIMESTAMP", from.getText() != null ? from.getText().toString() : "");
        i.putExtra("ENDTIMESTAMP", to.getText() != null ? to.getText().toString() : "");
        i.putExtra("TOPLEFTLATITUDE", tlLat.getText() != null ? tlLat.getText().toString() : "");
        i.putExtra("TOPLEFTLONGITUDE", tlLon.getText() != null ? tlLon.getText().toString() : "");
        i.putExtra("BTMRIGHTLATITUDE", brLat.getText() != null ? brLat.getText().toString() : "");
        i.putExtra("BTMRIGHTLONGITUDE", brLon.getText() != null ? brLon.getText().toString() : "");
        i.putExtra("KEYWORDS", keywords.getText() != null ? keywords.getText().toString() : "");
        setResult(RESULT_OK, i);
        finish();
    }
}
