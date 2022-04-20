package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    MultitouchView myTouchView;
    ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myTouchView = new MultitouchView();
        //setContentView(myTouchView);

        setContentView(R.layout.activity_main);
        myTouchView=findViewById(R.id.mutitouchview);
        //Log.i("distance",""+this);
        myTouchView.initView(this);
    }
}