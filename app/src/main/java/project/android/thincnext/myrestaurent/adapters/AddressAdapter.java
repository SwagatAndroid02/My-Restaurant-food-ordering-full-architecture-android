package project.android.thincnext.myrestaurent.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.activities.AddAddressActivity;
import project.android.thincnext.myrestaurent.database.UrlDatabaseHelper;
import project.android.thincnext.myrestaurent.model.ATaskCompleteListner;
import project.android.thincnext.myrestaurent.model.AddressItemData;
import project.android.thincnext.myrestaurent.model.AddressSession;
import project.android.thincnext.myrestaurent.model.BackgroundServiceAsynkTask;

/**
 * Created by thincnext on 03-Mar-18.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> implements ATaskCompleteListner<String> {

    private Context context;
    List<AddressItemData> data = Collections.emptyList();
    //final static int ITEMS_PER_PAGE = 2;
    AddAddressActivity activity;

    private UrlDatabaseHelper urlDB;

    AddressSession addressSession;

    public AddressAdapter(Context context, List<AddressItemData> data, AddAddressActivity activity) {
        this.context = context;
        this.data = data;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_item_address, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        addressSession = new AddressSession(context);
        urlDB = new UrlDatabaseHelper(context);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MyViewHolder myHolder = (MyViewHolder) holder;
        final AddressItemData current = data.get(position);

        String adtype = current.addressType;
        String ad = current.address;
        if (adtype.equalsIgnoreCase("Home")) {
            myHolder.add_type_image.setImageResource(R.drawable.home_add_icon);
            myHolder.add_type_txt.setText("Home Address");
        } else {
            myHolder.add_type_image.setImageResource(R.drawable.work_add_icon);
            myHolder.add_type_txt.setText("Work Address");
        }

        myHolder.txt_address.setText(ad);
        myHolder.add_id.setText(current.addressId);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clear() {
        final int size = data.size();
        data.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public void onTaskComplete(String result) {
        activity.onAddressTaskStart();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView add_type_image, add_delete;
        TextView add_type_txt, txt_address, add_id,txt_address_pin;

        MyViewHolder(View itemView) {
            super(itemView);

            add_type_image = (ImageView) itemView.findViewById(R.id.single_itm_add_image);
            add_type_txt = (TextView) itemView.findViewById(R.id.single_itm_add_type);
            txt_address = (TextView) itemView.findViewById(R.id.single_itm_txt_address);
            txt_address_pin= (TextView) itemView.findViewById(R.id.single_itm_txt_pincode);
            add_id = (TextView) itemView.findViewById(R.id.single_itm_add_id);
            add_delete = (ImageView) itemView.findViewById(R.id.single_itm_delete);


            add_type_image.setOnClickListener(this);
            add_type_txt.setOnClickListener(this);
            txt_address.setOnClickListener(this);
            add_delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == add_type_image || v == add_type_txt || v == txt_address) {
                String addrss = txt_address.getText().toString();
                String pincode = txt_address_pin.getText().toString();
                String addrsstype = add_type_txt.getText().toString();
                addressSession.setMyAddress(addrss, addrsstype,pincode);

                activity.getSavedAddress();
                activity.finish();
                return;
            }
            if (v == add_delete) {

                String addId = add_id.getText().toString();
                deleteAddress(addId);
            }
        }
    }

    private void deleteAddress(String id) {
        String url = urlDB.getSingleUrl("deleteaddress");

        List<NameValuePair> parameters = null;
        parameters = new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("AID", id));

        BackgroundServiceAsynkTask backgroundService = new BackgroundServiceAsynkTask(context, activity, parameters);
        backgroundService.execute(url);
    }
}
