package project.android.thincnext.myrestaurent.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.activities.Home;
import project.android.thincnext.myrestaurent.adapters.SubscriptionAdapter;
import project.android.thincnext.myrestaurent.database.UrlDatabaseHelper;
import project.android.thincnext.myrestaurent.model.ATaskCompleteListner;
import project.android.thincnext.myrestaurent.model.BackgroundServiceAsynkTask;
import project.android.thincnext.myrestaurent.model.CenterZoomLayoutManager;
import project.android.thincnext.myrestaurent.model.LinePagerIndicatorDecoration;
import project.android.thincnext.myrestaurent.model.SubscriptionDataItems;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFragment extends Fragment implements ATaskCompleteListner<String>{

    private RecyclerView recyclerView;
    private SubscriptionAdapter adapter;

    private UrlDatabaseHelper db;
    View mView;

    private int overallXScrol = 0;

    public SubscriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_subscription, container, false);

        mView=view;

        recyclerView= (RecyclerView) view.findViewById(R.id.subscription_recyclerview);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        db=new UrlDatabaseHelper(getActivity());

        onStartTask();

        return view;
    }

    private void onStartTask(){
        String url=db.getSingleUrl("Subscription");

        List<NameValuePair> parameters = null;
        parameters=new ArrayList<NameValuePair>(2);
        BackgroundServiceAsynkTask asynkTask=new BackgroundServiceAsynkTask(getActivity(),this,parameters);
        asynkTask.execute(url);
    }

    @Override
    public void onTaskComplete(String result) {

        if(result!=null && !result.equals("") && !result.equals("null")) {

            List<SubscriptionDataItems> data = new ArrayList<>();

            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONArray jArray = jsonObj.getJSONArray("Table");

                /*for (int i = jArray.length() - 1; i < jArray.length(); i--) {*/
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    SubscriptionDataItems subsItems = new SubscriptionDataItems();
                    subsItems.subs_id = json_data.getString("Sub_ID");
                    subsItems.subs_title = json_data.getString("Dish_Name");
                    subsItems.subs_img = json_data.getString("Image");
                    subsItems.subs_original_price = json_data.getString("Orginalprice");
                    subsItems.subs_offer_price = json_data.getString("OfferPrice");
                    subsItems.subs_descp = json_data.getString("Description");
                    subsItems.subs_vegan_type = json_data.getString("category");
                    subsItems.subs_per_meal=json_data.getString("Per_Meal");
                    subsItems.subs_thumbnail=json_data.getString("subs_thumbnail");

                    data.add(subsItems);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new SubscriptionAdapter(getActivity(), data);
            CenterZoomLayoutManager linearLayoutManager= (CenterZoomLayoutManager) new CenterZoomLayoutManager(getActivity(), CenterZoomLayoutManager.HORIZONTAL, false);
            //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setOnFlingListener(null);
            recyclerView.setAdapter(adapter);
            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);

            recyclerView.addItemDecoration(new LinePagerIndicatorDecoration());

            ((Home)getActivity()).hideLoading();

        }
    }
}
