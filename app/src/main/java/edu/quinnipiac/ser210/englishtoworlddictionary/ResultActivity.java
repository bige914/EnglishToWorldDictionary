package edu.quinnipiac.ser210.englishtoworlddictionary;
/**
 * ResultActivity class, output area for converted word from English
 * to Chosen language
 *
 * @authors Ellsworth Evarts IV, Ania Lightly
 * @date 2/29/2020
 */
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        String translation = (String) getIntent().getExtras().get("translation");

        TextView textView = (TextView) findViewById(R.id.result);

        textView.setText(translation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
