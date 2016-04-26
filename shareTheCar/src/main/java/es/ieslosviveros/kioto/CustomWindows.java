package es.ieslosviveros.kioto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import es.ieslosviveros.www.myapplication.R;

/**
 * Created by papa on 24/04/2016.
 */
public class CustomWindows extends Activity {
    protected ImageButton icon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.activity_main);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);


        icon  = (ImageButton) findViewById(R.id.icon);
        icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent goHome = new Intent(Intent.ACTION_MAIN);
                goHome.setClass(CustomWindows.this, MainActivity.class);
                goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goHome);
                finish();
            }
        });
    }

}

