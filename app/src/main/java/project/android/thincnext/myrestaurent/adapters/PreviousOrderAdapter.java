package project.android.thincnext.myrestaurent.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.model.PreviousOrderData;

/**
 * Created by thincnext on 01-Mar-18.
 */

public class PreviousOrderAdapter  extends RecyclerView.Adapter<PreviousOrderAdapter.MyViewHolder>{

    List<PreviousOrderData> data= Collections.emptyList();
    Context context;

    public PreviousOrderAdapter(Context context,List<PreviousOrderData> data){
        this.context=context;
        this.data=data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_item_previous_order,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MyViewHolder myHolder= (MyViewHolder) holder;
        final PreviousOrderData current=data.get(position);

        Picasso.with(context).load(current.getDishImage()).into(myHolder.iv_dishImage);

        loadStatusImage(R.drawable.status_done,myHolder.iv_ord_palce);
        myHolder.txt_ord_palce.setTextColor(ContextCompat.getColor(context, R.color.green700));
        myHolder.dishName.setText(current.getDishName());
        myHolder.dishId.setText(current.getDishId());
        myHolder.dishQuantity.setText(current.getDishQuantity());
        myHolder.dishTotalPrice.setText(current.getDishtotalPrice());
        myHolder.dishPrice.setText(current.getDishPrice());
        myHolder.dishImageUrl.setText(current.getDishImage());
        myHolder.orderDate.setText(current.getOrderDate());

    }

    private void loadStatusImage(int drawable,ImageView imageview){
        Picasso.with(context).load(drawable).into(imageview);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

     class MyViewHolder extends RecyclerView.ViewHolder{

         ImageView iv_dishImage;
         TextView  dishId, dishName,dishPrice, dishTotalPrice, dishQuantity,dishImageUrl,orderDate;
         TextView txt_ord_palce,txt_ord_confirmed,txt_ord_out,txt_ord_delivered;
         ImageView iv_ord_palce,iv_ord_confirmed,iv_ord_out,iv_ord_delivered;

        MyViewHolder(View itemView) {
            super(itemView);
            iv_dishImage= (ImageView) itemView.findViewById(R.id.previous_order_img);

            dishId= (TextView) itemView.findViewById(R.id.previous_order_product_id);
            dishName= (TextView) itemView.findViewById(R.id.previous_order_product_name);
            dishPrice= (TextView) itemView.findViewById(R.id.previous_order_product_price);
            dishTotalPrice= (TextView) itemView.findViewById(R.id.previous_order_product_total_price);
            dishQuantity= (TextView) itemView.findViewById(R.id.previous_order_product_quantity);
            dishImageUrl= (TextView) itemView.findViewById(R.id.previous_order_product_imageUrl);
            orderDate= (TextView) itemView.findViewById(R.id.previous_order_oderdate);

            txt_ord_palce= (TextView) itemView.findViewById(R.id.previous_order_status_txt_ord_placed);
            txt_ord_confirmed= (TextView) itemView.findViewById(R.id.previous_order_status_txt_ord_confirmed);
            txt_ord_out= (TextView) itemView.findViewById(R.id.previous_order_status_txt_ord_out);
            txt_ord_delivered= (TextView) itemView.findViewById(R.id.previous_order_status_txt_ord_delivered);

            iv_ord_palce= (ImageView) itemView.findViewById(R.id.previous_order_status_iv_one);
            iv_ord_confirmed=(ImageView) itemView.findViewById(R.id.previous_order_status_iv_two);
            iv_ord_out=(ImageView) itemView.findViewById(R.id.previous_order_status_iv_three);
            iv_ord_delivered=(ImageView) itemView.findViewById(R.id.previous_order_status_iv_four);
        }
    }
}
