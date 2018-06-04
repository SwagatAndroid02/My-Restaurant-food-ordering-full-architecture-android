package project.android.thincnext.myrestaurent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.activities.CartActivity;
import project.android.thincnext.myrestaurent.database.AddToCartDBHelper;
import project.android.thincnext.myrestaurent.model.CartDataItems;

/**
 * Created by thincnext on 26-Jan-18.
 */

public class AddToCartAdapter extends RecyclerView.Adapter<AddToCartAdapter.MyViewHolder>{

    private Context context;
    List<CartDataItems> data= Collections.emptyList();
    AddToCartDBHelper db;
    CartActivity cartActivity;

    public AddToCartAdapter(Context context,List<CartDataItems> data, CartActivity cartActivity){
        this.context=context;
        this.data=data;
        this.cartActivity=cartActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_item_carts,parent,false);
        MyViewHolder holder=new MyViewHolder(view,this);
        db=new AddToCartDBHelper(context);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final MyViewHolder myHolder= (MyViewHolder) holder;
        final CartDataItems current=data.get(position);

        myHolder._id.setText(current.get_id());

        Picasso.with(context).load(current.getDishImage()).into(myHolder.iv_dishImage);
        myHolder.dishName.setText(current.getDishName());
        myHolder.dishId.setText(current.getDishId());
        myHolder.dishQuantity.setText(current.getDishQuantity());
        myHolder.dishTotalPrice.setText(current.getDishTotalPrice());
        myHolder.dishPrice.setText(current.getDishPrice());
        myHolder.dishVeganType.setText(current.getDishVeganType());
        myHolder.dishImage.setText(current.getDishImage());
        myHolder.count_item.setText(current.getDishQuantity());
        myHolder._id.setText(current.get_id());

        myHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double intTotalPrice=Double.valueOf(myHolder.dishTotalPrice.getText().toString());
                double intPrice=Double.valueOf(myHolder.dishPrice.getText().toString());
                double finalPrice=intTotalPrice-intPrice;
                if(finalPrice!=0.0) {
                    int countItem = Integer.valueOf(myHolder.dishQuantity.getText().toString());
                    int finalCount = countItem - 1;
                    db.minusOneExistingItem(current.getDishId(), current.getDishPrice());
                    myHolder.count_item.setText(String.valueOf(finalCount));
                    myHolder.dishQuantity.setText(String.valueOf(finalCount));
                    myHolder.dishTotalPrice.setText(String.valueOf(finalPrice));
                }else {
                    db.removeSinglecartitem(current.getDishId());
                    removeItem(position);
                }
            }
        });

        myHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double intTotalPrice=Double.valueOf(myHolder.dishTotalPrice.getText().toString());
                double intPrice=Double.valueOf(myHolder.dishPrice.getText().toString());
                double finalPrice=intTotalPrice+intPrice;
                int countItem = Integer.valueOf(myHolder.dishQuantity.getText().toString());
                int finalCount = countItem + 1;
                db.addOneExistingItem(current.getDishId(),"1",current.getDishPrice());
                myHolder.count_item.setText(String.valueOf(finalCount));
                myHolder.dishQuantity.setText(String.valueOf(finalCount));
                myHolder.dishTotalPrice.setText(String.valueOf(finalPrice));
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    private void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        cartActivity.checkEmptyCart();

    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        AddToCartAdapter adapter;

        ImageView iv_dishImage;
        TextView _id, dishId, dishName,dishPrice, dishTotalPrice, dishQuantity, dishVeganType,dishImage;

        Button plus,minus;
        TextView count_item;

        public MyViewHolder(View itemView, final AddToCartAdapter adapter) {
            super(itemView);
            this.adapter=adapter;
            iv_dishImage= (ImageView) itemView.findViewById(R.id.single_item_addtocart_img);
            _id= (TextView) itemView.findViewById(R.id.single_item_addtocart_id);
            dishId= (TextView) itemView.findViewById(R.id.single_item_addtocart_product_id);
            dishName= (TextView) itemView.findViewById(R.id.single_item_addtocart_product_name);
            dishPrice= (TextView) itemView.findViewById(R.id.single_item_addtocart_product_price);
            dishTotalPrice= (TextView) itemView.findViewById(R.id.single_item_addtocart_product_total_price);
            dishQuantity= (TextView) itemView.findViewById(R.id.single_item_addtocart_product_quantity);
            dishVeganType= (TextView) itemView.findViewById(R.id.single_item_addtocart_vegan_type);
            dishImage= (TextView) itemView.findViewById(R.id.single_item_addtocart_product_imageUrl);

            plus= (Button) itemView.findViewById(R.id.single_item_addtocart_plus_btn);
            minus= (Button) itemView.findViewById(R.id.single_item_addtocart_minus_btn);
            count_item= (TextView) itemView.findViewById(R.id.single_item_addtocart_item_count);



        }

    }
}
