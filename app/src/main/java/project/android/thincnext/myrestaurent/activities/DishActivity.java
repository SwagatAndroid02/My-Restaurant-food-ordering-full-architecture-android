package project.android.thincnext.myrestaurent.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.database.AddToCartDBHelper;
import project.android.thincnext.myrestaurent.model.CartDataItems;

public class DishActivity extends AppCompatActivity implements View.OnClickListener {

   // private CollapsingToolbarLayout collapsingToolbarLayout;
   // private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    //private LinearLayout ratingLayout;

    private ImageView image, iv_vegan_type;
    private TextView txt_title,txt_rating, txt_description, txt_price, txt_ingredient, txt_item_count, txt_item_original_price;

    private LinearLayout add_btn_layout, plusminus_btn_layout;
    private Button btn_add, btn_plus, btn_minus;
    private TextView countItems;

    private TextToSpeech tts;

    AddToCartDBHelper addToCartDBHelper;
    private boolean isDishAdded;

    private ImageView iv_opt_cart, iv_opt_dial, iv_opt_rating;

    private String name, price, descp, imgUrl, bloodGroup, dishid, veganType, ingrdnt, rating,originalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);
        toolbar = (Toolbar) findViewById(R.id.dish_activity_toolbar);
        setSupportActionBar(toolbar);
       /* appBarLayout = (AppBarLayout) findViewById(R.id.dish_activity_app_bar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.dish_activity_collapsingtoolbarlayout);*/
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        addToCartDBHelper = new AddToCartDBHelper(DishActivity.this);
        add_btn_layout = (LinearLayout) findViewById(R.id.dish_activity_addbtn_layout);
        plusminus_btn_layout = (LinearLayout) findViewById(R.id.dish_activity_plusminus_layout);
        iv_opt_cart = (ImageView) findViewById(R.id.dish_activity_opt_cart);
        iv_opt_dial = (ImageView) findViewById(R.id.dish_activity_opt_dial);
        iv_opt_rating = (ImageView) findViewById(R.id.dish_activity_opt_rating);

        btn_add = (Button) findViewById(R.id.dish_activity_btn_add);
        btn_minus = (Button) findViewById(R.id.dish_activity_btn__minus);
        btn_plus = (Button) findViewById(R.id.dish_activity_btn__plus);
        countItems = (TextView) findViewById(R.id.dish_activity_txt_count);


        //ratingLayout = (LinearLayout) findViewById(R.id.dish_activity_rating);
        txt_title= (TextView) findViewById(R.id.dish_activity_title);
        txt_description = (TextView) findViewById(R.id.dish_activity_descp);
        txt_price = (TextView) findViewById(R.id.dish_activity_price);
        txt_item_original_price = (TextView) findViewById(R.id.dish_activity_original_price);
        iv_vegan_type = (ImageView) findViewById(R.id.dish_activity_vegan_type);
        txt_ingredient = (TextView) findViewById(R.id.dish_activity_ingredient);


        if (getIntent().getExtras() != null) {
            dishid = getIntent().getExtras().getString("Dactivity_DISHID");
            name = getIntent().getExtras().getString("Dactivity_DISHNAME");
            price = getIntent().getExtras().getString("Dactivity_DISHPRICE");
            descp = getIntent().getExtras().getString("Dactivity_DISHDESCP");
            imgUrl = getIntent().getExtras().getString("Dactivity_DISHIMG");
            bloodGroup = getIntent().getExtras().getString("Dactivity_DISHBLOODGROUP");
            veganType = getIntent().getExtras().getString("Dactivity_DISHVEGANTYPE");
            ingrdnt = getIntent().getExtras().getString("Dactivity_DISHINGRDNT");
            rating = getIntent().getExtras().getString("Dactivity_DISHRATING");
            originalPrice = getIntent().getExtras().getString("Dactivity_DISHORIGINALPRICE");
            //The key argument here must match that used in the other activity
           /* collapsingToolbarLayout.setTitle(name);
        } else {
            collapsingToolbarLayout.setTitle(getString(R.string.app_name));
        }*/

            flipCartImage();

            isDishAdded = addToCartDBHelper.checkProductExits(dishid);
            if (isDishAdded) {
                Animation slideUpGone = AnimationUtils.loadAnimation(this, R.anim.slide_up_gone);
                Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
                add_btn_layout.startAnimation(slideUpGone);
                add_btn_layout.setVisibility(View.GONE);
                plusminus_btn_layout.setVisibility(View.VISIBLE);
                plusminus_btn_layout.startAnimation(slideUp);

                String quanty = addToCartDBHelper.getSingleItemQuantity(dishid);
                countItems.setText(quanty);

            } else {
                plusminus_btn_layout.setVisibility(View.GONE);
                add_btn_layout.setVisibility(View.VISIBLE);
            }

            String orprice = originalPrice;
            double opr = Double.valueOf(orprice);
            if (opr != 0.0 && !orprice.equals("0.000")) {
                txt_item_original_price.setVisibility(View.VISIBLE);
                Paint p = new Paint();
                p.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                //myHolder.original_price.setPaintFlags(p.getColor());
                txt_item_original_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                txt_item_original_price.setText(originalPrice);
            } else {
                txt_item_original_price.setVisibility(View.GONE);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        AppBarLayout.LayoutParams.MATCH_PARENT,
                        AppBarLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                txt_price.setLayoutParams(param);
            }

            image = (ImageView) findViewById(R.id.dish_activity_iv_dishimage);
            txt_rating = (TextView) findViewById(R.id.dish_activity_txt_rating);
            txt_description.setText(descp);

            txt_price.setText(price);
            txt_title.setText(name);

            txt_ingredient.setText(ingrdnt);

            if (veganType.equals("Veg")) {
                iv_vegan_type.setImageResource(R.drawable.veg_icon);
            } else {
                iv_vegan_type.setImageResource(R.drawable.nonveg_icon);
            }

            Picasso.with(DishActivity.this).load(imgUrl).into(image);
            txt_rating.setText(rating);


       // appBarLayout.addOnOffsetChangedListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


            btn_add.setOnClickListener(this);
            btn_minus.setOnClickListener(this);
            btn_plus.setOnClickListener(this);
            iv_opt_cart.setOnClickListener(this);
            iv_opt_dial.setOnClickListener(this);
            iv_opt_rating.setOnClickListener(this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    private void showRatingDialog() {
        Button b;
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_plus_layout, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(bottomSheetView);
        dialog.show();

        b= (Button) bottomSheetView.findViewById(R.id.dialog_plus_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    /*private void addPlusOne() {
        boolean b=addToCartDBHelper.checkProductExits(dishid);
        String qCount=cartItemCount.getText().toString();

        String grandPrice,productName,productImage;
        productName=item_name.getText().toString();
        grandPrice=item_grandPrice.getText().toString();
        productImage=image;

        if(!b) {
            //plusone.addOneItem(productName, grandPrice, productImage);
            itemAddToCart();
        }else {
            addToCartDBHelper.addOneExistingItem(name, "1", finalPrice);
            //((HomeActivity) getActivity()).getmainFragment();
        }
    }*/

    private void itemAddToCart(){
        String atcDishId,atcDishName,atcVeganType,atcDishQuantity,atcDishPrice,atcDishTotalPrice,atcDishImage;

        atcDishId=dishid;
        atcDishName=name;
        atcVeganType=veganType;
        atcDishQuantity="1";
        atcDishPrice=price;

        double fPrice=Double.valueOf(price)*Integer.valueOf(countItems.getText().toString());
        atcDishTotalPrice=String.valueOf(fPrice);

        atcDishImage=imgUrl;

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
        String sCount=countItems.getText().toString();
        int itemCount=Integer.valueOf(sCount)+1;
        countItems.setText(String.valueOf(itemCount));
        addToCartDBHelper.addOneExistingItem(dishid, "1", price);

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
        String sCount=countItems.getText().toString();
        int itemCount=Integer.valueOf(sCount)-1;

        countItems.setText(String.valueOf(itemCount));
        addToCartDBHelper.minusOneExistingItem(dishid,price);


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

    private void flipCartImage(){
        int inCartItemsCount=addToCartDBHelper.getAllCount();
        if(inCartItemsCount==0){
            iv_opt_cart.setImageResource(R.drawable.empty_cart_icon);
        }else {
            iv_opt_cart.setImageResource(R.drawable.filled_cart_icon);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == btn_add) {
            Animation slideUpGone = AnimationUtils.loadAnimation(this, R.anim.slide_up_gone);
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            add_btn_layout.startAnimation(slideUpGone);
            add_btn_layout.setVisibility(View.GONE);
            plusminus_btn_layout.setVisibility(View.VISIBLE);
            plusminus_btn_layout.startAnimation(slideUp);

            countItems.setText("1");
            String txtcount = countItems.getText().toString();
            String txtFinalCount;

            itemAddToCart();
            flipCartImage();

        }
        if (v == btn_plus) {
            addOnePlusItem();
        }
        if (v == btn_minus) {

            int countt = Integer.valueOf(countItems.getText().toString());

            if (countt == 1) {
                addToCartDBHelper.removeSinglecartitem(dishid);
                Animation slideUpGone = AnimationUtils.loadAnimation(this, R.anim.slide_up_gone);
                Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
                plusminus_btn_layout.startAnimation(slideUpGone);
                plusminus_btn_layout.setVisibility(View.GONE);
                add_btn_layout.setVisibility(View.VISIBLE);
                add_btn_layout.startAnimation(slideUp);

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
            flipCartImage();

        }
        if (v == iv_opt_cart) {

            startActivity(new Intent(DishActivity.this, CartActivity.class));
            final CoordinatorLayout ll = (CoordinatorLayout) findViewById(R.id.dish_activity_main);
            Animation a = AnimationUtils.loadAnimation(
                    DishActivity.this, android.R.anim.fade_out);
            a.setDuration(700);
            a.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }
            });
            ll.startAnimation(a);

            overridePendingTransition(R.anim.layout_slide_up, R.anim.stay);
        }

        if (v == iv_opt_dial) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + getString(R.string.myresto_phone_number)));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(callIntent);
        }

        if(v==iv_opt_rating){
            showRatingDialog();
        }
    }
}
