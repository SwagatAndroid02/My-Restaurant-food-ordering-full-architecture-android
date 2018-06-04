package project.android.thincnext.myrestaurent.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.adapters.AddToCartAdapter;
import project.android.thincnext.myrestaurent.database.AddToCartDBHelper;
import project.android.thincnext.myrestaurent.model.CartDataItems;
import project.android.thincnext.myrestaurent.model.CheckInternetConnection;

public class CartActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private AddToCartAdapter adapter;
    private AddToCartDBHelper db;

    private LinearLayout emptyCartImg;
    private Button checkout, shopnow;
    Cursor cursor;
    List<CartDataItems> data;

    private Toolbar toolbar;

    private CheckInternetConnection internetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        toolbar= (Toolbar) findViewById(R.id.cart_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My cart");

        internetConnection=new CheckInternetConnection(CartActivity.this);
        recyclerView= (RecyclerView) findViewById(R.id.addtocart_recyclerview);
        db=new AddToCartDBHelper(CartActivity.this);

        emptyCartImg= (LinearLayout) findViewById(R.id.cart_emptycart_layout);
        checkout= (Button) findViewById(R.id.cart_btn_checkout);
        shopnow= (Button) findViewById(R.id.cart_btn_shopnow);

        data = new ArrayList<>();

        if(db.getAllCount()>0) {
            emptyCartImg.setVerticalGravity(View.GONE);
            checkout.setVisibility(View.VISIBLE);
            getCartItems();
        }else {
            emptyCartImg.setVisibility(View.VISIBLE);
            checkout.setVisibility(View.GONE);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });

        checkout.setOnClickListener(this);
        shopnow.setOnClickListener(this);
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
        recreate();
    }

    @Override
    public void onClick(View v) {
        if(v==checkout){
            if(internetConnection.isConnected()) {
                if (db.getAllCount() > 0) {

                    //startActivity(new Intent(getActivity(), CheckOutActivity.class));
                    String totalprice = db.getTotalPrice();
                    //Toast.makeText(getActivity(),totalprice,Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(CartActivity.this, CheckOutActivity.class);
                    intent.putExtra("TOTAL_ADDED_PRICE", totalprice);
                    startActivity(intent);

                    //startActivity(new Intent(getActivity(), DeliveryDetailsActivity.class));

               /* String result= null;
                try {
                    result = db.getAllDataAndGenerateJSON();
                    Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/

                } else {
                    Toast.makeText(getApplicationContext(), "Nothing is there", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getApplicationContext(),"no internet connection!",Toast.LENGTH_LONG).show();
            }
        }
        if(v==shopnow){
            finish();
            overridePendingTransition(R.anim.stay,R.anim.layout_slide_down);
        }
    }

    private void getCartItems() {
        data.clear();
        try {
            cursor = db.qureyData("SELECT  * FROM myCartDataTable");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        CartDataItems item=new CartDataItems();
                        item.set_id(cursor.getString(0));
                        item.setDishName(cursor.getString(1));
                        item.setDishId(cursor.getString(2));
                        item.setDishQuantity(cursor.getString(3));
                        item.setDishPrice(cursor.getString(4));
                        item.setDishtotalPrice(cursor.getString(5));
                        item.setDishVeganType(cursor.getString(6));
                        item.setDishImage(cursor.getString(7));
                        data.add(item);

                    } while (cursor.moveToNext());
                }
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        initializeRecyclerview();
    }

    private void initializeRecyclerview() {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CartActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        adapter=new AddToCartAdapter(this,data,CartActivity.this);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.smoothScrollToPosition(0);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void checkEmptyCart(){
        //AddToCartDBHelper db=new AddToCartDBHelper(getActivity());
        if(db.getAllCount()==0){
            emptyCartImg.setVisibility(View.VISIBLE);
            checkout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}
