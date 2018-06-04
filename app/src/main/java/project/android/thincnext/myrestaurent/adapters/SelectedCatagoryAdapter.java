package project.android.thincnext.myrestaurent.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.activities.DishActivity;
import project.android.thincnext.myrestaurent.database.AddToCartDBHelper;
import project.android.thincnext.myrestaurent.model.CartDataItems;
import project.android.thincnext.myrestaurent.model.MainItemListData;

/**
 * Created by thincnext on 19-Jan-18.
 */

public class SelectedCatagoryAdapter extends RecyclerView.Adapter<SelectedCatagoryAdapter.MyViewHolder>{

    private Context context;
    List<MainItemListData> data= Collections.emptyList();
    AddToCartDBHelper db;

    MyViewHolder myHolder;

    public SelectedCatagoryAdapter(Context context,List<MainItemListData> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_item_selected_catagory,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        db=new AddToCartDBHelper(context);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

         myHolder=holder;
        MainItemListData current=data.get(position);

        myHolder.dishId.setText(current.dishId);

        Picasso.with(context).load(current.dishImage).into(myHolder.iv_dishImg);

        myHolder.dishName.setText(current.dishName);
        myHolder.dishPrice.setText(current.dishPrice);
        myHolder.dishDescp.setText(current.dishDescp);
        myHolder.dishImgUrl.setText(current.dishImage);
        myHolder.dishId.setText(current.dishId);
        myHolder.dishVegNveg.setText(current.vegNonVeg);
        myHolder.dishIngrdnt.setText(current.dishIngredients);
        myHolder.dishRating.setText(current.dishRating);

        myHolder.dishOriginalPrice.setText(current.dishOriginalPrice);

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

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dishName, dishPrice, dishDescp, dishImgUrl, bloodgroups, dishId, dishVegNveg, dishIngrdnt, dishRating,dishOriginalPrice;
        ImageView iv_dishImg, iv_vegNveg;

       /* LinearLayout layoutAdd, layoutPlusMinus;
        Button plus, minus;
        TextView itemCount;
        AddToCartDBHelper db;*/
        MyViewHolder(View itemView) {
            super(itemView);

            db=new AddToCartDBHelper(context);
            dishId = (TextView) itemView.findViewById(R.id.selected_catagory_dishId);
            dishName = (TextView) itemView.findViewById(R.id.selected_catagory_dishTitle);
            dishPrice = (TextView) itemView.findViewById(R.id.selected_catagory_dishPrice);
            dishDescp = (TextView) itemView.findViewById(R.id.selected_catagory_dishDesc);
            dishImgUrl = (TextView) itemView.findViewById(R.id.selected_catagory_dishImgUrl);
            bloodgroups = (TextView) itemView.findViewById(R.id.selected_catagory_dishBloodGroup);
            dishVegNveg = (TextView) itemView.findViewById(R.id.selected_catagory_vegeNveg);
            dishIngrdnt = (TextView) itemView.findViewById(R.id.selected_catagory_dishIngredient);
            dishRating = (TextView) itemView.findViewById(R.id.selected_catagory_dishRating);
            dishOriginalPrice= (TextView) itemView.findViewById(R.id.selected_catagory_dishOriginalPrice);

            iv_dishImg = (ImageView) itemView.findViewById(R.id.iv_selected_catagory_mainImg);
            iv_vegNveg = (ImageView) itemView.findViewById(R.id.iv_selected_catagory_vegenveg);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name, price, descp, imgUrl, bloodGroup, dishid, veganType, ingrdnt, rating,originalprice;

                    name = dishName.getText().toString();
                    price = dishPrice.getText().toString();
                    descp = dishDescp.getText().toString();
                    imgUrl = dishImgUrl.getText().toString();
                    bloodGroup = bloodgroups.getText().toString();
                    dishid = dishId.getText().toString();
                    veganType = dishVegNveg.getText().toString();
                    ingrdnt = dishIngrdnt.getText().toString();
                    rating = dishRating.getText().toString();
                    originalprice=dishOriginalPrice.getText().toString();

                    Intent intent = new Intent(context, DishActivity.class);
                    intent.putExtra("Dactivity_DISHID", dishid);
                    intent.putExtra("Dactivity_DISHNAME", name);
                    intent.putExtra("Dactivity_DISHPRICE", price);
                    intent.putExtra("Dactivity_DISHDESCP", descp);
                    intent.putExtra("Dactivity_DISHIMG", imgUrl);
                    intent.putExtra("Dactivity_DISHBLOODGROUP", bloodGroup);
                    intent.putExtra("Dactivity_DISHVEGANTYPE", veganType);
                    intent.putExtra("Dactivity_DISHINGRDNT", ingrdnt);
                    intent.putExtra("Dactivity_DISHRATING", rating);
                    intent.putExtra("Dactivity_DISHORIGINALPRICE",originalprice);
                    context.startActivity(intent);
                }
            });

        }

    }
}
