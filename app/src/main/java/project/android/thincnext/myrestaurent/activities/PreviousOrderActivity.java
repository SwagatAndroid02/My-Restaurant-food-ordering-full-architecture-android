package project.android.thincnext.myrestaurent.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.adapters.PreviousOrderAdapter;
import project.android.thincnext.myrestaurent.database.UrlDatabaseHelper;
import project.android.thincnext.myrestaurent.model.ATaskCompleteListner;
import project.android.thincnext.myrestaurent.model.BackgroundServiceAsynkTask;
import project.android.thincnext.myrestaurent.model.CheckInternetConnection;
import project.android.thincnext.myrestaurent.model.LoginSessionManager;
import project.android.thincnext.myrestaurent.model.MainItemListData;
import project.android.thincnext.myrestaurent.model.PreviousOrderData;

public class PreviousOrderActivity extends AppCompatActivity implements ATaskCompleteListner<String>{

    private LoginSessionManager loginSessionManager;
    boolean isUserLogedIn;
    private CheckInternetConnection checkInternetConnection;
    private UrlDatabaseHelper db;

    HashMap<String, String> userDetails;

    private RecyclerView recyclerView;
    private PreviousOrderAdapter adapter;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_order);
        toolbar= (Toolbar) findViewById(R.id.pre_ord_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My orders");

        checkInternetConnection=new CheckInternetConnection(PreviousOrderActivity.this);

        recyclerView= (RecyclerView) findViewById(R.id.previousorder_recyclerview);

        db=new UrlDatabaseHelper(PreviousOrderActivity.this);
        loginSessionManager = new LoginSessionManager(PreviousOrderActivity.this);
        isUserLogedIn = loginSessionManager.isLoggedIn();
        if (!isUserLogedIn) {
          startActivity(new Intent(PreviousOrderActivity.this,LoginActivity.class));
        }
        if(checkInternetConnection.isConnected()){
            onTaskStart();
        }else {
            Toast.makeText(getApplicationContext(),"no internet connection",Toast.LENGTH_SHORT).show();
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }

    private void onTaskStart(){
        String url=db.getSingleUrl("getjsonorderresult");

        userDetails = loginSessionManager.getUserDetails();
        String user_email = userDetails.get("email");
        List<NameValuePair> parameters = null;
        parameters = new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("useremail", user_email));

        BackgroundServiceAsynkTask backgroundService = new BackgroundServiceAsynkTask(PreviousOrderActivity.this, this, parameters);
        backgroundService.execute(url);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.stay, R.anim.layout_slide_down);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loginSessionManager = new LoginSessionManager(PreviousOrderActivity.this);
        isUserLogedIn = loginSessionManager.isLoggedIn();
        if (!isUserLogedIn) {
            finish();
            overridePendingTransition(R.anim.stay, R.anim.layout_slide_down);
        }

    }

    @Override
    public void onTaskComplete(String result) {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

        if(result!=null && !result.equals("") && !result.equals("null")) {

            List<PreviousOrderData> data = new ArrayList<>();

            try {
                JSONObject jsonObj = new JSONObject(result);

                JSONArray jArray = jsonObj.getJSONArray("Table");

                /*for (int i = jArray.length() - 1; i < jArray.length(); i--) {*/
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    PreviousOrderData myOrderData = new PreviousOrderData();
                    String name=json_data.getString("dish_image");
                    myOrderData.setDishId(json_data.getString("dish_id"));
                    myOrderData.setDishName(json_data.getString("dish_name"));
                    myOrderData.setDishQuantity(json_data.getString("dish_quantity"));
                    myOrderData.setDishPrice(json_data.getString("dish_price"));
                    myOrderData.setDishtotalPrice(json_data.getString("dish_total_price"));
                    myOrderData.setDishVeganType(json_data.getString("dish_vegan_type"));
                    myOrderData.setDishImage(json_data.getString("dish_image"));
                    //myOrderData.setOrderStatus(json_data.getString("Orderstatus"));
                    myOrderData.setOrderDate(json_data.getString("Orderdate"));
                    data.add(myOrderData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            adapter = new PreviousOrderAdapter(PreviousOrderActivity.this, data);
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(PreviousOrderActivity.this, R.anim.layout_animation_from_bottom);
            recyclerView.setLayoutAnimation(controller);
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(PreviousOrderActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.scheduleLayoutAnimation();
            recyclerView.setAdapter(adapter);
        }else {
            Toast.makeText(getApplicationContext(),"something went wrong!",Toast.LENGTH_SHORT).show();
        }
    }
}
