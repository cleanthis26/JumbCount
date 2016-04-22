package com.ateolan.jumbcount;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int jumbCounter;

    // Components
    TextView counterTV;
    Button plusButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init
        jumbCounter = 0;
        counterTV = (TextView) findViewById(R.id.counterTV);
        plusButton = (Button) findViewById(R.id.plusButton);

        updateCounterTV();
    }

    private void updateCounterTV() {
        counterTV.setText("" + jumbCounter);
    }

    public void minusButton_onClick(View v) {
        if (jumbCounter <= 0) {
            jumbCounter = 0;
        } else {
            jumbCounter--;
        }
        updateCounterTV();
    }

    public void plusButton_onClick(View v) {
        jumbCounter++;
        updateCounterTV();
    }

}
