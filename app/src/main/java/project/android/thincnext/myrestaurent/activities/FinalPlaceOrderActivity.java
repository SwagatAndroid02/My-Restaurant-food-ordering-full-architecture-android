package project.android.thincnext.myrestaurent.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.database.UrlDatabaseHelper;
import project.android.thincnext.myrestaurent.model.ATaskCompleteListner;
import project.android.thincnext.myrestaurent.model.BackgroundServiceAsynkTask;

public class FinalPlaceOrderActivity extends AppCompatActivity implements ATaskCompleteListner<String>{


    private  String jsonobject,jsonarray;
    private UrlDatabaseHelper db;

    private Button back;

    private TextView txt_success;

    private ImageView gif_image;
    private TextView order_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_place_order);


        txt_success= (TextView) findViewById(R.id.finalOrdrPlc_txt_success);
        txt_success.setVisibility(View.GONE);
        back= (Button) findViewById(R.id.finalOrdrPlc_back_btn);
        order_status= (TextView) findViewById(R.id.finalOrdrPlc_order_status);
        order_status.setVisibility(View.GONE);

        gif_image= (ImageView) findViewById(R.id.finalOrdrPlc_image);

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gif_image);
        Glide.with(this).load(R.drawable.loading_gif).into(imageViewTarget);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonobject = extras.getString("MYJSONOBJECT");
            jsonarray = extras.getString("MYJSONARRAY");
            //Toast.makeText(getApplicationContext(),jsonobject,Toast.LENGTH_SHORT).show();
            Log.d("jsonobject",jsonobject);
            Log.d("jsonarray",jsonarray);
        }

        db=new UrlDatabaseHelper(FinalPlaceOrderActivity.this);
        onStartTask();
    }

    private void onStartTask(){
        List<NameValuePair> parameters = null;
        parameters = new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("orddet", jsonobject));
        parameters.add(new BasicNameValuePair("crt", jsonarray));

        String url=db.getSingleUrl("sendjsonorderdata");
        BackgroundServiceAsynkTask backgroundService = new BackgroundServiceAsynkTask(FinalPlaceOrderActivity.this, this, parameters);
        backgroundService.execute(url);
    }

    @Override
    public void onTaskComplete(String result) {
        if(result!=null && !result.equals("") && !result.equals("null")) {
            if (result.equals("ok")) {
                txt_success.setVisibility(View.VISIBLE);

                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gif_image);
                Glide.with(this).load(R.drawable.success_gif).into(imageViewTarget);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        return;
                    }
                });
                order_status.setVisibility(View.VISIBLE);
                order_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(FinalPlaceOrderActivity.this,PreviousOrderActivity.class));
                        finish();
                        return;
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                txt_success.setText("ORDER UNSUCCESSFUL");
                txt_success.setTextColor(getResources().getColor(R.color.colorAccent));
                txt_success.setVisibility(View.VISIBLE);
            }
        }else {
            Toast.makeText(getApplicationContext(),"something went wrong!",Toast.LENGTH_SHORT).show();
        }
    }
}
