package io.github.agaghd.flowlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlowLayout flowLayout = (FlowLayout) findViewById(R.id.flow_layout);
        String text = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            TextView textView = new TextView(this);
            textView.setText(i + text.substring(0, random.nextInt(text.length())));
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            textView.setBackgroundResource(R.drawable.tag_bg);
            textView.setPadding(8, 8, 8, 8);
            flowLayout.addView(textView);
        }
    }
}
