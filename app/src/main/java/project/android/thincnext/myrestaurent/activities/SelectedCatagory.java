package project.android.thincnext.myrestaurent.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.database.UrlDatabaseHelper;
import project.android.thincnext.myrestaurent.model.ATaskCompleteListner;
import project.android.thincnext.myrestaurent.model.BackgroundServiceAsynkTask;
import project.android.thincnext.myrestaurent.model.MainItemListData;
import project.android.thincnext.myrestaurent.adapters.SelectedCatagoryAdapter;

public class SelectedCatagory extends AppCompatActivity implements ATaskCompleteListner<String>{

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private SelectedCatagoryAdapter adapter;

    private UrlDatabaseHelper urlDB;
    private ProgressBar loading;

    private String dishType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_catagory);
        toolbar= (Toolbar) findViewById(R.id.selected_catagory_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        int colorARGB = R.color.Black;
        Drawable navIcon = toolbar.getNavigationIcon();
        navIcon.setColorFilter(getResources().getColor(colorARGB), PorterDuff.Mode.SRC_IN);

        recyclerView= (RecyclerView) findViewById(R.id.selected_catagory_recyclerview);
        loading= (ProgressBar) findViewById(R.id.selected_catagory_loading);
        loading.setVisibility(View.GONE);
        urlDB=new UrlDatabaseHelper(SelectedCatagory.this);

        if( getIntent().getExtras() != null)
        {
            dishType = getIntent().getStringExtra("DISH_CATAGORY");
            onStartTask();
        }else {
            getToast("Nothing to show!");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                return;
            }
        });
    }

    private void onStartTask(){
        loading.setVisibility(View.VISIBLE);
        String url=urlDB.getSingleUrl(dishType);

        List<NameValuePair> parameters = null;
        parameters=new ArrayList<NameValuePair>(2);
        BackgroundServiceAsynkTask backgroundService=new BackgroundServiceAsynkTask(SelectedCatagory.this,this,parameters);
        backgroundService.execute(url);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        int colorARGB = R.color.Black;
        Drawable navIcon = toolbar.getNavigationIcon();
        navIcon.setColorFilter(getResources().getColor(colorARGB), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onTaskComplete(String result) {

        if(result!=null && !result.equals("") && !result.equals("null")) {

            List<MainItemListData> data = new ArrayList<>();

            try {
                JSONObject jsonObj = new JSONObject(result);

                JSONArray jArray = jsonObj.getJSONArray("Table");

                /*for (int i = jArray.length() - 1; i < jArray.length(); i--) {*/
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    MainItemListData mainListItemData = new MainItemListData();
                    mainListItemData.dishId = json_data.getString("Dish_ID");
                    mainListItemData.dishName = json_data.getString("Dish_Name");

                    mainListItemData.bloodgroups = json_data.getString("BloodGroup");
                    mainListItemData.dishImage = json_data.getString("Image_Url");
                    mainListItemData.dishDescp = json_data.getString("Description");
                    mainListItemData.dishIngredients = json_data.getString("Ingredients");

                    mainListItemData.dishPrice = json_data.getString("Price");
                    mainListItemData.dishRating = json_data.getString("Rating");
                    mainListItemData.vegNonVeg = json_data.getString("VegNonVeg");
                    mainListItemData.dishOriginalPrice = json_data.getString("OrginalPrice");

                    data.add(mainListItemData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int mod = position % 6;

                    if(position == 0 || position == 1)
                        return 2;
                    else if(position < 6)
                        return 1;
                    else if(mod == 0 || mod == 1)
                        return 2;
                    else
                        return 1;
                }
            });*/


            adapter=new SelectedCatagoryAdapter(SelectedCatagory.this,data);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }else {
            getToast("Something went wrong!");
        }
        loading.setVisibility(View.GONE);
    }

    private void getToast(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return super.onKeyDown(keyCode, event);
    }
}
