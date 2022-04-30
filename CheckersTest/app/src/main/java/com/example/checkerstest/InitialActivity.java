package com.example.checkerstest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class InitialActivity extends AppCompatActivity {

    public static String GAMETYPE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        ToggleButton selection = findViewById(R.id.selection);
        Button playGame=findViewById(R.id.playGame);

        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.i("distance",""+"hola");
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                //Log.i("distance",""+"hola1");
                if (selection.getText().toString().compareTo("Manual")==0){
                    intent.putExtra(GAMETYPE,false);
                }else{
                    intent.putExtra(GAMETYPE,true);
                }
                //Log.i("distance",""+"hola2");
                startActivity(intent);
            }
        });
    }
}