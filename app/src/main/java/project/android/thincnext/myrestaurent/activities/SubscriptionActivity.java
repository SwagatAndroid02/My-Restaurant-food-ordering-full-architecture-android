package project.android.thincnext.myrestaurent.activities;

import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.database.AddToCartDBHelper;
import project.android.thincnext.myrestaurent.model.CartDataItems;

public class SubscriptionActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView subs_image;
    private String subs_id,subs_title,subs_desc,subs_original_price,subs_offer_price,subs_img_url,subs_per_meal_price,subs_vegan_type,subs_thumbnail;
    private TextView title,veganType,finalPrice,original_price,descp,price_permeal,subId;
    private RelativeLayout original_price_layout;
    private Button add;
    private LinearLayout plusminus_layout;
    private Button plus,minus;
    private TextView item_count;

    AddToCartDBHelper addToCartDBHelper;
    private boolean isDishAdded;

    private Toolbar toolbar;

    DecimalFormat decimal_format = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        toolbar= (Toolbar) findViewById(R.id.subs_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Subscription");

        addToCartDBHelper = new AddToCartDBHelper(SubscriptionActivity.this);

        if (getIntent().getExtras() != null) {
            subs_id = getIntent().getExtras().getString("SUBS_ID");
            subs_title = getIntent().getExtras().getString("SUBS_TITLE");
            subs_desc = getIntent().getExtras().getString("SUBS_DESC");
            subs_original_price = getIntent().getExtras().getString("SUBS_ORIGINAL_PRICE");
            subs_offer_price = getIntent().getExtras().getString("SUBS_OFFER_PRICE");
            subs_img_url = getIntent().getExtras().getString("SUBS_IMAGE_URL");
            subs_per_meal_price = getIntent().getExtras().getString("SUBS_PER_MEAL_PRICE");
            subs_vegan_type = getIntent().getExtras().getString("SUBS_VEGAN_TYPE");
            subs_thumbnail=getIntent().getExtras().getString("SUBS_THUMBNAIL");
        }

        original_price_layout= (RelativeLayout) findViewById(R.id.sub_activity_original_price_layout);
        title= (TextView) findViewById(R.id.subs_activity_title);
        veganType= (TextView) findViewById(R.id.subs_activity_vegantype);
        subs_image= (ImageView) findViewById(R.id.subs_activity_img);
        Picasso.with(getApplicationContext()).load(subs_img_url).resize(558,789).placeholder(R.drawable.placeholder).into(subs_image);

        finalPrice= (TextView) findViewById(R.id.subs_activity_final_price);
        add= (Button) findViewById(R.id.subs_activity_add_btn);
        descp= (TextView) findViewById(R.id.subs_activity_desc);
        price_permeal= (TextView) findViewById(R.id.subs_activity_price_per_meal);
        subId= (TextView) findViewById(R.id.subs_activity_subId);
        original_price= (TextView) findViewById(R.id.subs_activity_original_price);

        plusminus_layout= (LinearLayout) findViewById(R.id.subs_activity_plus_minus_layout);
        plus= (Button) findViewById(R.id.subs_activity_btn__plus);
        minus= (Button) findViewById(R.id.subs_activity_btn__minus);
        item_count= (TextView) findViewById(R.id.subs_activity_txt_count);

        subId.setText(subs_id);
        original_price.setText(subs_original_price);
        if(subs_offer_price.equals(subs_original_price)){
            original_price_layout.setVisibility(View.GONE);
        }else {
            original_price_layout.setVisibility(View.VISIBLE);
        }
        title.setText(subs_title);
        if (subs_vegan_type.equals("veg")) {
            veganType.setText("Veg");
        } else {
            veganType.setText("Non Veg");
        }

        finalPrice.setText(subs_offer_price);
        descp.setText(subs_desc);

        //0.00 format
        double number = Double.parseDouble(subs_per_meal_price);
        String formated_permeal_price = decimal_format.format(number);
        price_permeal.setText(formated_permeal_price);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });

        isDishAdded = addToCartDBHelper.checkProductExits(subs_id);
        if(isDishAdded){
            add.setVisibility(View.GONE);
            plusminus_layout.setVisibility(View.VISIBLE);
            String quanty = addToCartDBHelper.getSingleItemQuantity(subs_id);
            item_count.setText(quanty);
        }else {
            plusminus_layout.setVisibility(View.GONE);
            add.setVisibility(View.VISIBLE);
        }

        add.setOnClickListener(this);
        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==add){
            add.setVisibility(View.GONE);
            plusminus_layout.setVisibility(View.VISIBLE);
            item_count.setText("1");
            itemAddToCart();
        }
        if(v==plus){
            addOnePlusItem();
        }
        if(v==minus){
            int countt = Integer.valueOf(item_count.getText().toString());

            if (countt == 1) {
                addToCartDBHelper.removeSinglecartitem(subs_id);
                plusminus_layout.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);

                List<CartDataItems> data = addToCartDBHelper.getAllCartItems();

                for (CartDataItems dt : data) {
                    String log = "Id: " + dt.get_id()
                            + " ,NAME: " + dt.getDishName()
                            + " ,DISH_ID: " + dt.getDishId()
                            + " ,COUNT: " + dt.getDishQuantity()
                            + " ,TOTAL_PRICE: " + dt.getDishTotalPrice()
                            + " ,PRICE: " + dt.getDishPrice()
                            + " ,VEGAN_TYPE: " + dt.getDishVeganType()
                            + " IMAGE: " + dt.getDishImage();
                    // Writing Contacts to log
                    Log.d("CART_ITEMS: ", log);
                }

            } else {
                removeOneMinusItem();
            }
        }
    }

    private void itemAddToCart(){
        String atcDishId,atcDishName,atcVeganType,atcDishQuantity,atcDishPrice,atcDishTotalPrice,atcDishImage;

        atcDishId=subs_id;
        atcDishName="(Subscription package) "+subs_title;
        atcVeganType=subs_vegan_type;
        atcDishQuantity="1";
        atcDishPrice=subs_offer_price;

        double fPrice=Double.valueOf(subs_offer_price)*Integer.valueOf(item_count.getText().toString());
        atcDishTotalPrice=String.valueOf(fPrice);

        atcDishImage=subs_img_url;

        addToCartDBHelper.addToCart(new CartDataItems(atcDishName,atcVeganType,atcDishId,atcDishQuantity,atcDishTotalPrice,atcDishPrice,atcDishImage));

        List<CartDataItems> data = addToCartDBHelper.getAllCartItems();

        for (CartDataItems dt : data) {
            String log = "Id: " + dt.get_id()
                    + " ,NAME: " + dt.getDishName()
                    + " ,DISH_ID: " + dt.getDishId()
                    + " ,COUNT: " + dt.getDishQuantity()
                    + " ,TOTAL_PRICE: " + dt.getDishTotalPrice()
                    + " ,PRICE: " + dt.getDishPrice()
                    + " ,VEGAN_TYPE: " + dt.getDishVeganType()
                    + " IMAGE: " + dt.getDishImage();
            // Writing Contacts to log
            Log.d("CART_ITEMS: ", log);
        }
    }


    private void addOnePlusItem(){
        String sCount=item_count.getText().toString();
        int itemCount=Integer.valueOf(sCount)+1;
        item_count.setText(String.valueOf(itemCount));
        addToCartDBHelper.addOneExistingItem(subs_id, "1", subs_offer_price);

        List<CartDataItems> data = addToCartDBHelper.getAllCartItems();

        for (CartDataItems dt : data) {
            String log = "Id: " + dt.get_id()
                    + " ,NAME: " + dt.getDishName()
                    + " ,DISH_ID: " + dt.getDishId()
                    + " ,COUNT: " + dt.getDishQuantity()
                    + " ,TOTAL_PRICE: " + dt.getDishTotalPrice()
                    + " ,PRICE: " + dt.getDishPrice()
                    + " ,VEGAN_TYPE: " + dt.getDishVeganType()
                    + " IMAGE: " + dt.getDishImage();
            // Writing Contacts to log
            Log.d("CART_ITEMS: ", log);
        }
    }

    private void removeOneMinusItem(){
        String sCount=item_count.getText().toString();
        int itemCount=Integer.valueOf(sCount)-1;

        item_count.setText(String.valueOf(itemCount));
        addToCartDBHelper.minusOneExistingItem(subs_id,subs_offer_price);


        List<CartDataItems> data = addToCartDBHelper.getAllCartItems();

        for (CartDataItems dt : data) {
            String log = "Id: " + dt.get_id()
                    + " ,NAME: " + dt.getDishName()
                    + " ,DISH_ID: " + dt.getDishId()
                    + " ,COUNT: " + dt.getDishQuantity()
                    + " ,TOTAL_PRICE: " + dt.getDishTotalPrice()
                    + " ,PRICE: " + dt.getDishPrice()
                    + " ,VEGAN_TYPE: " + dt.getDishVeganType()
                    + " IMAGE: " + dt.getDishImage();
            // Writing Contacts to log
            Log.d("CART_ITEMS: ", log);
        }
    }
}
