package com.example.finnkino;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Activity2 extends AppCompatActivity {
    private ListView lv;
    private TextView tw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        lv = (ListView) findViewById(R.id.listView);
        tw = (TextView) findViewById(R.id.textView);
        Movies mList = Movies.getInstance();
        tw.setText(mList.getHeader());
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mList.getList());
        lv.setAdapter(adapter);
    }
}