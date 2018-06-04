package project.android.thincnext.myrestaurent.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.activities.DishActivity;
import project.android.thincnext.myrestaurent.activities.Home;
import project.android.thincnext.myrestaurent.database.AddToCartDBHelper;
import project.android.thincnext.myrestaurent.model.CartDataItems;
import project.android.thincnext.myrestaurent.model.MainItemListData;

/**
 * Created by thincnext on 18-Jan-18.
 */

public class MainItemListAdapter extends RecyclerView.Adapter<MainItemListAdapter.MyViewHolder>{

    private Context context;
    List<MainItemListData> data= Collections.emptyList();
    //final static int ITEMS_PER_PAGE = 2;
    AddToCartDBHelper db;
    Home homeActivity;

    public MainItemListAdapter(Context context,List<MainItemListData> data,Home homeActivity) {
        this.context = context;
        this.data = data;
        this.homeActivity=homeActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //int itemWidth = parent.getWidth() / ITEMS_PER_PAGE;
        View view= LayoutInflater.from(context).inflate(R.layout.single_item_main_list,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        db=new AddToCartDBHelper(context);
       /* ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = itemWidth;
        view.setLayoutParams(layoutParams);
        return new MyViewHolder(view);*/

       return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MyViewHolder myHolder= (MyViewHolder) holder;
        final MainItemListData current=data.get(position);

        myHolder.dishId.setText(current.dishId);

        Picasso.with(context).load(current.dishImage).resize(350,250).centerCrop().placeholder(R.drawable.placeholder).into(myHolder.iv_dishImg);

        myHolder.dishName.setText(current.dishName);
        myHolder.dishPrice.setText(current.dishPrice);
        myHolder.dishDescp.setText(current.dishDescp);

        myHolder.dishImgUrl.setText(current.dishImage);
        Log.d("main_dish_image",current.dishImage);

        myHolder.dishId.setText(current.dishId);
        myHolder.dishVegNveg.setText(current.vegNonVeg);
        myHolder.dishIngrdnt.setText(current.dishIngredients);
        myHolder.dishRating.setText(current.dishRating);


        double opr;
        String orprice=current.dishOriginalPrice;
        if(orprice!=null && !orprice.equals("null")) {
            opr = Double.valueOf(orprice);
        }else {
            opr=0.0;
        }
        if(opr!=0.0){

            myHolder.original_price_layout.setVisibility(View.VISIBLE);
            /*myHolder.original_price.setText(current.dishOriginalPrice);
            myHolder.original_price.setPaintFlags(myHolder.original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);*/

            /*Paint p = new Paint();
            p.setColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));

            //myHolder.original_price.setPaintFlags(p.getColor());
            myHolder.original_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);*/
            myHolder.original_price.setText(current.dishOriginalPrice);
            //myHolder.original_price.setPaintFlags(p.getColor());
        }else {
            myHolder.original_price_layout.setVisibility(View.GONE);
            myHolder.original_price.setText("0.000");
        }

        boolean isItemAvailable;
        isItemAvailable=db.checkProductExits(current.dishId);
        if(isItemAvailable){
            myHolder.addBtn.setVisibility(View.GONE);
            myHolder.plusminusLayout.setVisibility(View.VISIBLE);
        }

        if (isItemAvailable) {
            myHolder.addBtn.setVisibility(View.GONE);
            myHolder.plusminusLayout.setVisibility(View.VISIBLE);
            String quanty = db.getSingleItemQuantity(current.dishId);
            myHolder.countItems.setText(quanty);
        } else {
            myHolder.plusminusLayout.setVisibility(View.GONE);
            myHolder.addBtn.setVisibility(View.VISIBLE);
        }

        if(current.bloodgroups!=null && !current.bloodgroups.equals("null") && !current.bloodgroups.equals("NULL")) {
            myHolder.bloodgroups.setText(current.bloodgroups);
        }else {
            myHolder.bloodgroups.setText("");
        }

        String vegenveg=current.vegNonVeg;

        if(vegenveg.equalsIgnoreCase("Veg")){
          myHolder.iv_vegNveg.setImageResource(R.drawable.veg_icon);
        }else {
            myHolder.iv_vegNveg.setImageResource(R.drawable.nonveg_icon);
        }

        myHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHolder.addBtn.setVisibility(View.GONE);
                myHolder.plusminusLayout.setVisibility(View.VISIBLE);
                myHolder.countItems.setText("1");
                double fPrice=Double.valueOf(current.dishPrice)*Integer.valueOf(myHolder.countItems.getText().toString());
                String dish_total_price=String.valueOf(fPrice);

                itemAddToCart(current.dishName,current.vegNonVeg,current.dishId,"1",dish_total_price,current.dishPrice,current.dishImage);
            }
        });

        myHolder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sCount=myHolder.countItems.getText().toString();
                int itemCount=Integer.valueOf(sCount)+1;
                myHolder.countItems.setText(String.valueOf(itemCount));
                db.addOneExistingItem(current.dishId, "1", current.dishPrice);
                addOnePlusItem();

            }
        });

        myHolder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int countt = Integer.valueOf(myHolder.countItems.getText().toString());

                if (countt == 1) {
                    db.removeSinglecartitem(current.dishId);
                    myHolder.plusminusLayout.setVisibility(View.GONE);
                    myHolder.addBtn.setVisibility(View.VISIBLE);
                    homeActivity.checkCartMenu();
                }else {
                    String sCount = myHolder.countItems.getText().toString();
                    int itemCount = Integer.valueOf(sCount) - 1;
                    myHolder.countItems.setText(String.valueOf(itemCount));
                    db.minusOneExistingItem(current.dishId, current.dishPrice);
                    removeOneMinusItem();
                    homeActivity.checkCartMenu();
                }
            }
        });

    }

    private void itemAddToCart(String dish_name,String dish_vega_type,String dish_id,String dish_quantity,String dish_total_price,String dish_price,String dish_image) {
        String atcDishId,atcDishName,atcVeganType,atcDishQuantity,atcDishPrice,atcDishTotalPrice,atcDishImage;

        atcDishId=dish_id;
        atcDishName=dish_name;
        atcVeganType=dish_vega_type;
        atcDishQuantity=dish_quantity;
        atcDishPrice=dish_price;
        atcDishTotalPrice=dish_total_price;
        atcDishImage=dish_image;

        db.addToCart(new CartDataItems(atcDishName,atcVeganType,atcDishId,atcDishQuantity,atcDishTotalPrice,atcDishPrice,atcDishImage));

        List<CartDataItems> data = db.getAllCartItems();

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

        homeActivity.checkCartMenu();
    }

    private void addOnePlusItem(){

        List<CartDataItems> data = db.getAllCartItems();

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
        homeActivity.checkCartMenu();
    }

    private void removeOneMinusItem(){

        List<CartDataItems> data = db.getAllCartItems();

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
        homeActivity.checkCartMenu();
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CardView main_layout;
        TextView dishName,dishPrice,dishDescp,dishImgUrl,bloodgroups,dishId,dishVegNveg,dishIngrdnt,dishRating,original_price;
        ImageView iv_dishImg,iv_vegNveg;
        RelativeLayout original_price_layout;

        Button addBtn,minusBtn,plusBtn;
        TextView countItems;
        LinearLayout plusminusLayout;

        MyViewHolder(final View itemView) {
            super(itemView);

            main_layout= (CardView) itemView.findViewById(R.id.my_list_background);
            dishId= (TextView) itemView.findViewById(R.id.main_list_dish_id);
            dishName= (TextView) itemView.findViewById(R.id.main_list_dishName);
            dishPrice= (TextView) itemView.findViewById(R.id.main_list_price);
            dishDescp= (TextView) itemView.findViewById(R.id.main_list_description);
            dishImgUrl= (TextView) itemView.findViewById(R.id.main_list_image_url);
            bloodgroups= (TextView) itemView.findViewById(R.id.main_list_bloodgroup);
            dishVegNveg= (TextView) itemView.findViewById(R.id.main_list_vegnonveg);
            dishIngrdnt= (TextView) itemView.findViewById(R.id.main_list_ingredients);
            dishRating= (TextView) itemView.findViewById(R.id.main_list_rating);
            original_price= (TextView) itemView.findViewById(R.id.main_list_original_price);
            original_price_layout= (RelativeLayout) itemView.findViewById(R.id.main_list_original_price_layout);

            addBtn= (Button) itemView.findViewById(R.id.main_list_btn_add);
            plusminusLayout= (LinearLayout) itemView.findViewById(R.id.main_list_plusminus_layout);
            minusBtn= (Button) itemView.findViewById(R.id.main_list_btn__minus);
            plusBtn= (Button) itemView.findViewById(R.id.main_list_btn__plus);
            countItems= (TextView) itemView.findViewById(R.id.main_list_txt_count);


            iv_dishImg= (ImageView) itemView.findViewById(R.id.dish_activity_iv_dishimage);
            iv_vegNveg= (ImageView) itemView.findViewById(R.id.main_list_vegnonveg_icon);

        }
    }
}
