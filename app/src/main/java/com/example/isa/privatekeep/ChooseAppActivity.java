package com.example.isa.privatekeep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChooseAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_app);
    }

    public void scanAppOnclick(View view){

    }

    public void okOnclick(View view){
        finish();
    }

    public void cancelOnclick(View view){
        finish();
    }
}
