package project.android.thincnext.myrestaurent.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.database.AddToCartDBHelper;
import project.android.thincnext.myrestaurent.database.UrlDatabaseHelper;
import project.android.thincnext.myrestaurent.model.ATaskCompleteListner;
import project.android.thincnext.myrestaurent.model.BackgroundServiceAsynkTask;
import project.android.thincnext.myrestaurent.model.CheckInternetConnection;
import project.android.thincnext.myrestaurent.model.UrlLoaderItem;

public class SplashScreen extends AppCompatActivity implements ATaskCompleteListner<String>, View.OnClickListener{

    private LinearLayout nointernet;
    List<UrlLoaderItem> data;

    UrlDatabaseHelper db;
    AddToCartDBHelper addToCartDBHelper;

    public CheckInternetConnection internetConnection;
    private Button retry;

    String urlLoaderURL="http://mainurl.somee.com/allUrl.asmx/GetUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        internetConnection=new CheckInternetConnection(SplashScreen.this);
        nointernet= (LinearLayout) findViewById(R.id.splashScreen_layout_nointernet);
        nointernet.setVisibility(View.GONE);
        retry= (Button) findViewById(R.id.splashScreen_retry);

        addToCartDBHelper=new AddToCartDBHelper(this);
        addToCartDBHelper.removeAllItems();
        db=new UrlDatabaseHelper(this,SplashScreen.this);
        db.removeAllItems();

        if(internetConnection.isConnected()) {
            onStartTask();
        }else {
            nointernet.setVisibility(View.VISIBLE);
        }
        retry.setOnClickListener(this);
    }

    private void onStartTask(){

        nointernet.setVisibility(View.GONE);
        List<NameValuePair> parameters = null;
        parameters=new ArrayList<NameValuePair>(2);
        BackgroundServiceAsynkTask backgroundService=new BackgroundServiceAsynkTask(SplashScreen.this,this,parameters);
        backgroundService.execute(urlLoaderURL);
    }

    @Override
    public void onTaskComplete(String s) {

        if(s!=null && !s.equals("") && !s.equals("null")) {
            data = new ArrayList<>();

            try {
                JSONObject jsonObj = new JSONObject(s);

                JSONArray jArray = jsonObj.getJSONArray("Table");

                /*for (int i = jArray.length() - 1; i < jArray.length(); i--){*/
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                   /* UrlLoaderItem urlLoaderData = new UrlLoaderItem();
                    urlLoaderData.setUrlName(json_data.getString("Name"));
                    urlLoaderData.setUrlAddress(json_data.getString("Url"));
                    data.add(urlLoaderData);*/
                   db.loadUrl(json_data.getString("Name"),json_data.getString("Url"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<UrlLoaderItem> listdata = db.getAllURLs();

            for (UrlLoaderItem dt : listdata) {
                String log = "Id: " + dt.get_id()
                        + " ,tile: " + dt.getUrlName()
                        + " ,url: " + dt.getUrlAddress();
                // Writing Contacts to log
                Log.d("URLs: ", log);

            }


           /* adapter = new VerticalPagerAdapter(data, getActivity(),NewsFeed.this);
            verticalViewPager.setAdapter(adapter);
            //verticalViewPager.setPageTransformer(false, new DefaultTransformer());
            verticalViewPager.setCurrentItem(pos, true);*/
        }
    }

    public void goToHomePage(){
        startActivity(new Intent(SplashScreen.this,Home.class));
        finish();
        return;
    }

    @Override
    public void onClick(View v) {
        if(v==retry){
            recreate();
        }
    }
}
