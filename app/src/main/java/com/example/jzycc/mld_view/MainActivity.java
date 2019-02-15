package com.example.jzycc.mld_view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.jzycc.mld_view.View.DragProgressView;
import com.example.jzycc.mld_view.View.FlowLabelLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DragProgressView dragProgressView;
    private FlowLabelLayout flowLabelLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dragProgressView = findViewById(R.id.dpv_progess);

        flowLabelLayout = findViewById(R.id.fbl_tag);

        dragProgressView.addProgressListener(new DragProgressView.OnProgressListener() {
            @Override
            public void onCurrentProgress(float value) {

            }

            @Override
            public void onError() {

            }
        });

        List<FlowLabelLayout.Label> list = new ArrayList<>();
        FlowLabelLayout.Label label = new FlowLabelLayout.Label();
        label.setText("!23");
        FlowLabelLayout.Label label1 = new FlowLabelLayout.Label();
        label1.setText("!234");
        list.add(label);
        list.add(label1);

        List<String> labels = new ArrayList<>();
        labels.add("12111111123214");
        labels.add("12111111123214knklsf444444444");
        labels.add("12111111123gsdg2214");
        labels.add("12111111123214");
        labels.add("12111111123214knklsf4");
        labels.add("12111111123gsdg221");
        flowLabelLayout.setFlowLabelLayout(labels,label,null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("jzy111", "onResume: "+ flowLabelLayout.getMeasuredHeight());

    }
}
