package com.example.jzycc.mld_view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.jzycc.mld_view.View.DragProgressView;

public class MainActivity extends AppCompatActivity {
    private DragProgressView dragProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dragProgressView = findViewById(R.id.dpv_progess);

        dragProgressView.addProgressListener(new DragProgressView.OnProgressListener() {
            @Override
            public void onCurrentProgress(float value) {
                Log.i("jzy111", "onCurrentProgress: " + value);
            }
        });
    }
}
