package com.example.sprintone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;


public class FilterGalleryActivity extends AppCompatActivity {

    private HashMap<String,String> filterMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
    }

    public void onCancel(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onFilter(View v) {
        EditText startDate = (EditText) findViewById(R.id.filter_start_date);
        EditText endDate = (EditText) findViewById(R.id.filter_end_date);
        EditText tlLat = (EditText) findViewById(R.id.tlLat);
        EditText tlLon = (EditText) findViewById(R.id.tlLon);
        EditText brLat = (EditText) findViewById(R.id.brLat);
        EditText brLon = (EditText) findViewById(R.id.brLon);
        EditText keyword = (EditText) findViewById(R.id.filter_keyword);

        filterMap = new HashMap<>();
        filterMap.put("startDate", startDate.getText().toString());
        filterMap.put("endDate", endDate.getText().toString());
        filterMap.put("tlLat", tlLat.getText().toString());
        filterMap.put("tlLon", tlLon.getText().toString());
        filterMap.put("brLat", brLat.getText().toString());
        filterMap.put("brLon", brLon.getText().toString());
        filterMap.put("keyword", keyword.getText().toString());

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Filter_Input", filterMap);
        Log.d("Map", "Filter Strings:" + filterMap);
        startActivity(intent);
    }
}
