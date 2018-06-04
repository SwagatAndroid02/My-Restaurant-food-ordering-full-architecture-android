package project.android.thincnext.myrestaurent.adapters;

import android.content.Context;
import android.content.Intent;
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
import project.android.thincnext.myrestaurent.activities.SubscriptionActivity;
import project.android.thincnext.myrestaurent.model.SubscriptionDataItems;

/**
 * Created by thincnext on 16-Feb-18.
 */

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder>{

    private Context context;
    List<SubscriptionDataItems> data= Collections.emptyList();

    public SubscriptionAdapter(Context context,List<SubscriptionDataItems> data){
        this.context=context;
        this.data=data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_item_subscription,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MyViewHolder myViewHolder=holder;
        SubscriptionDataItems currentItem=data.get(position);

        holder.id.setText(currentItem.subs_id);
        holder.title.setText(currentItem.subs_title);
        holder.descp.setText(currentItem.subs_descp);
        holder.original_price.setText(currentItem.subs_original_price);
        holder.offer_price.setText(currentItem.subs_offer_price);
        holder.img_url.setText(currentItem.subs_img);
        holder.per_meal_price.setText(currentItem.subs_per_meal);
        holder.vegan_type.setText(currentItem.subs_vegan_type);
        if (currentItem.subs_vegan_type.equals("veg")) {
            holder.vegan_type_img.setImageResource(R.drawable.veg_icon);
        } else {
            holder.vegan_type_img.setImageResource(R.drawable.nonveg_icon);
        }
        holder.thumbnail.setText(currentItem.subs_thumbnail);
        if(currentItem.subs_thumbnail!=null && !currentItem.subs_thumbnail.equals("null") && !currentItem.subs_thumbnail.equals("")){
            Picasso.with(context).load(currentItem.subs_thumbnail).placeholder(R.drawable.subs_placeholder_img).into(holder.img);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img,vegan_type_img;
        TextView id,title,descp,original_price,offer_price,img_url,per_meal_price,vegan_type,thumbnail;

        MyViewHolder(View itemView) {
            super(itemView);
            img= (ImageView) itemView.findViewById(R.id.single_item_subs_img);
            vegan_type_img= (ImageView) itemView.findViewById(R.id.single_item_subs_vegan_type_img);

            id= (TextView) itemView.findViewById(R.id.single_item_subs_id);
            title= (TextView) itemView.findViewById(R.id.single_item_subs_title);
            descp= (TextView) itemView.findViewById(R.id.single_item_subs_descp);
            original_price= (TextView) itemView.findViewById(R.id.single_item_subs_original_price);
            offer_price= (TextView) itemView.findViewById(R.id.single_item_subs_offer_price);
            img_url= (TextView) itemView.findViewById(R.id.single_item_subs_img_url);
            per_meal_price= (TextView) itemView.findViewById(R.id.single_item_subs_per_meal);
            vegan_type= (TextView) itemView.findViewById(R.id.single_item_subs_vegan_type);
            thumbnail= (TextView) itemView.findViewById(R.id.single_item_subs_thumb_nail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String subs_id,subs_title,subs_desc,subs_original_price,subs_offer_price,subs_img_url,subs_per_meal_price,subs_vegan_type,subs_thmbnail;
                    subs_id=id.getText().toString();
                    subs_title=title.getText().toString();
                    subs_desc=descp.getText().toString();
                    subs_original_price=original_price.getText().toString();
                    subs_offer_price=offer_price.getText().toString();
                    subs_img_url=img_url.getText().toString();
                    subs_per_meal_price=per_meal_price.getText().toString();
                    subs_vegan_type=vegan_type.getText().toString();
                    subs_thmbnail=thumbnail.getText().toString();

                    Intent intent = new Intent(context, SubscriptionActivity.class);
                    intent.putExtra("SUBS_ID", subs_id);
                    intent.putExtra("SUBS_TITLE", subs_title);
                    intent.putExtra("SUBS_DESC", subs_desc);
                    intent.putExtra("SUBS_ORIGINAL_PRICE", subs_original_price);
                    intent.putExtra("SUBS_OFFER_PRICE", subs_offer_price);
                    intent.putExtra("SUBS_IMAGE_URL", subs_img_url);
                    intent.putExtra("SUBS_PER_MEAL_PRICE", subs_per_meal_price);
                    intent.putExtra("SUBS_VEGAN_TYPE", subs_vegan_type);
                    intent.putExtra("SUBS_THUMBNAIL", subs_thmbnail);
                    context.startActivity(intent);

                }
            });
        }
    }
}
