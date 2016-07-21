package com.newteo.wuziqi;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public CustomView customView;
    public Button btn_restart;
    public TextView result_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customView = (CustomView) findViewById(R.id.broad_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("人生第一款安卓游戏");
        btn_restart = (Button) findViewById(R.id.btn_restart);
        result_text = (TextView) findViewById(R.id.result_text);
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customView.reStart();
                result_text.setText(customView.result);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
}
