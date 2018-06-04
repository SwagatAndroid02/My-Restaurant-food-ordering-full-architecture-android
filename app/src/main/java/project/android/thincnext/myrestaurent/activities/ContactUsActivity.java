package project.android.thincnext.myrestaurent.activities;

import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import project.android.thincnext.myrestaurent.R;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView opt_fb,opt_tweeter,opt_insta;
    private ImageView map;

    private ImageView backarrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        backarrow= (ImageView) findViewById(R.id.contact_activity_btn_back_arrow);


        opt_insta= (ImageView) findViewById(R.id.contact_us_img_insta);
        opt_fb= (ImageView) findViewById(R.id.contact_us_img_fb);
        opt_tweeter= (ImageView) findViewById(R.id.contact_us_img_tweeter);
        map= (ImageView) findViewById(R.id.contact_us_map);

        opt_tweeter.setOnClickListener(this);
        opt_fb.setOnClickListener(this);
        opt_insta.setOnClickListener(this);
        map.setOnClickListener(this);
        backarrow.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v==backarrow){
            finish();
            return;
        }
        if(v==opt_fb){
            String fb_url="https://www.facebook.com/MyResto/";
            getScoicalMediaIntent(fb_url);
        }
        if(v==opt_tweeter){
            String tweeter_url="https://twitter.com/MyResto";
            getScoicalMediaIntent(tweeter_url);
        }
        if(v==opt_insta){
            String insta_url="https://www.instagram.com/MyResto/";
            getScoicalMediaIntent(insta_url);
        }
        if(v==map){
            String addres="http://maps.google.co.in/maps?q="+"MyResto";
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  Uri.parse(addres));
            startActivity(intent);
        }
    }

    private void getScoicalMediaIntent(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
