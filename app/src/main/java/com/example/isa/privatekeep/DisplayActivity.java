package com.example.isa.privatekeep;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class DisplayActivity extends AppCompatActivity {

    String linkid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = this.getIntent();
        linkid = intent.getStringExtra("FileId");
        Toast.makeText(this, linkid,Toast.LENGTH_SHORT).show();
    }
}
