package project.android.thincnext.myrestaurent.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.adapters.AddressAdapter;
import project.android.thincnext.myrestaurent.database.UrlDatabaseHelper;
import project.android.thincnext.myrestaurent.model.ATaskCompleteListner;
import project.android.thincnext.myrestaurent.model.AddressItemData;
import project.android.thincnext.myrestaurent.model.AddressSession;
import project.android.thincnext.myrestaurent.model.BackgroundServiceAsynkTask;
import project.android.thincnext.myrestaurent.model.LoginSessionManager;

public class AddAddressActivity extends AppCompatActivity implements ATaskCompleteListner<String>, View.OnClickListener {

    private Toolbar toolbar;
    private AddressSession addressSession;
    private LoginSessionManager userSession;

    private AddressAdapter adapter;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    HashMap<String, String> userDetails;
    HashMap<String, String> lastAddress;

    private ImageView getLocation_btn;
    private ProgressBar loc_loading,toolbar_loading;

    private UrlDatabaseHelper urlDB;

    private EditText et_address;

    private RecyclerView recyclerView;
    private String radio_btn_string;

    private TextView no_previous_add;

    String mLat, mLong;
    private EditText et_pincode;

    private RelativeLayout editaddress_layout;

    private boolean validAddress=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        toolbar = (Toolbar) findViewById(R.id.address_activity_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Delivery Location");

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup_address_type);

        userSession = new LoginSessionManager(AddAddressActivity.this);
        urlDB = new UrlDatabaseHelper(AddAddressActivity.this);

        recyclerView = (RecyclerView) findViewById(R.id.addrss_activity_recyclerview);
        addressSession = new AddressSession(AddAddressActivity.this);
        et_address = (EditText) findViewById(R.id.address_activity_et_add);

        if (addressSession.isAddressPresent()) {
            getSavedAddress();
        }

        editaddress_layout= (RelativeLayout) findViewById(R.id.address_activity_edit_address_layout);

        et_pincode= (EditText) findViewById(R.id.address_activity_et_pin);
        toolbar_loading= (ProgressBar) findViewById(R.id.address_activity_toolbar_loading);
        toolbar_loading.setVisibility(View.GONE);
        loc_loading= (ProgressBar) findViewById(R.id.address_activity_loading);
        getLocation_btn = (ImageView) findViewById(R.id.address_activity_btn_locate);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("PRERMISSION_LOCATION_ASKED", false)) {
            permissionDialog();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("PRERMISSION_LOCATION_ASKED", true);
            editor.commit();
        }

        no_previous_add= (TextView) findViewById(R.id.address_activity_no_data_txt);

        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonID);
        final String selectedtext = (String) radioButton.getText();
        radio_btn_string = selectedtext;

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radiobutton_addtype_home:
                        int radioButtonID = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton = (RadioButton) group.findViewById(radioButtonID);
                        final String selectedtext = (String) radioButton.getText();
                        radio_btn_string = selectedtext;
                        break;
                    case R.id.radiobutton_addtype_work:
                        int radioButtonID1 = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton1 = (RadioButton) group.findViewById(radioButtonID1);
                        final String selectedtext1 = (String) radioButton1.getText();
                        radio_btn_string = selectedtext1;
                        break;
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ;
                return;
            }
        });

        onAddressTaskStart();

        getLocation_btn.setOnClickListener(this);
        editaddress_layout.setOnClickListener(this);
    }

    public void onAddressTaskStart() {
        recyclerView.setVisibility(View.VISIBLE);
        no_previous_add.setVisibility(View.GONE);
        String url = urlDB.getSingleUrl("getaddress");

        userDetails = userSession.getUserDetails();
        String email = userDetails.get("email");

        List<NameValuePair> parameters = null;
        parameters = new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("email", email));

        BackgroundServiceAsynkTask backgroundService = new BackgroundServiceAsynkTask(getApplicationContext(), this, parameters);
        backgroundService.execute(url);
    }

    @Override
    public void onTaskComplete(String responseString) {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

        userDetails = userSession.getUserDetails();
        String email = userDetails.get("email");

        if (responseString != null && !responseString.equals("") && !responseString.equals("null")) {

            if(responseString.equals("deleted")){
                onAddressTaskStart();
            }else {
                if (!isJSONValid(responseString)) {
                    Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    if(responseString.equals("no address found for "+email)){
                        recyclerView.setVisibility(View.GONE);
                        no_previous_add.setVisibility(View.VISIBLE);
                    }
                } else {

                    List<AddressItemData> data = new ArrayList<>();
                    try {
                        JSONObject jsonObj = new JSONObject(responseString);

                        JSONArray jArray = jsonObj.getJSONArray("Table");

                /*for (int i = jArray.length() - 1; i < jArray.length(); i--) {*/
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            AddressItemData addressItemData = new AddressItemData();
                            addressItemData.address = json_data.getString("address");
                            addressItemData.addressType = json_data.getString("address_type");
                            addressItemData.addressId = json_data.getString("AID");
                            addressItemData.addPinCode=json_data.getString("pin_no");
                            data.add(addressItemData);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter = new AddressAdapter(getApplicationContext(), data, AddAddressActivity.this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddAddressActivity.this);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.address_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_address:
                String add,pin;
                add=et_address.getText().toString();
                pin=et_pincode.getText().toString();
                if(!add.isEmpty()){
                    if(!pin.isEmpty()){
                        String[] androidStrings = getResources().getStringArray(R.array.pin_codes);
                        if (Arrays.asList(androidStrings).contains(pin)) {
                            final View action_save_btn = findViewById(R.id.action_save_address);
                            hideSoftKey(action_save_btn);
                            item.setVisible(false);
                            toolbar_loading.setVisibility(View.VISIBLE);
                            saveThisAddress();
                        }else {
                            Toast.makeText(getApplicationContext(),"pincode not found",Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(getApplicationContext(),"enter pincode",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"enter address",Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == getLocation_btn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    et_address.setText("");
                    et_pincode.setText("");
                    getLatLong();
                    loc_loading.setVisibility(View.VISIBLE);
                }else {
                    ActivityCompat.requestPermissions(AddAddressActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }
            }
        }
        if(v==editaddress_layout){
            et_address.requestFocus();
        }
    }

    private void getLatLong() {

        final LocationManager locationManager = (LocationManager) AddAddressActivity.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLat = Double.toString(location.getLatitude());
                mLong = Double.toString(location.getLongitude());

                getCurrentAddress(mLat,mLong);
                locationManager.removeUpdates(this);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    private void getCurrentAddress(String latitude,String longitude){

        double dLat=Double.valueOf(latitude);
        double dLong=Double.valueOf(longitude);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        String[] mTestArray;
        mTestArray = getResources().getStringArray(R.array.pin_codes);

        try {
            addresses = geocoder.getFromLocation(dLat, dLong, 1);
            String address = addresses.get(0).getAddressLine(0);
            String postalCode = addresses.get(0).getPostalCode();
            validAddress=stringContainsItemFromList(postalCode,mTestArray);
            if(validAddress){
                et_address.setText(address);
                et_pincode.setText(postalCode);
            }else {
                et_address.getText().clear();
               et_address.setHint("sorry! currently our services are not available in your area.");
                //Toast.makeText(getApplicationContext(),postalCode,Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        loc_loading.setVisibility(View.GONE);

    }


    private class SetUserAddress extends AsyncTask<String,Void,String> {

        private List<NameValuePair> parameters;

        SetUserAddress(List<NameValuePair> parameters) {
            this.parameters=parameters;
        }

        @Override
        protected String doInBackground(String... params) {
            String url=params[0];

            try {
                if(!parameters.isEmpty()){

                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    String response;
                    return response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

                }else {
                    URL Url = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) Url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();

                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                            //append means to add something at the end
                        }
                        bufferedReader.close();
                        return stringBuilder.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            toolbar_loading.setVisibility(View.GONE);
            super.onPostExecute(result);
            boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

            if(result!=null && !result.equals("") && !result.equals("null")) {
                String responseString = result.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                responseString = responseString.replace("<string xmlns=\"http://tempuri.org/\">", "");
                responseString = responseString.replace("</string>", "").trim();
                onNewAddressSaved(responseString);
            }
        }
    }

    private void onNewAddressSaved(String result) {
        toolbar_loading.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
        String newAdd=et_address.getText().toString();
        String pin=et_pincode.getText().toString();
        addressSession.setMyAddress(newAdd,radio_btn_string,pin);
        AddAddressActivity.this.finish();
        return;
    }

    private void saveThisAddress() {
        String newAdd,pin;
        newAdd=et_address.getText().toString();
        pin=et_pincode.getText().toString();
        userDetails = userSession.getUserDetails();
        String email = userDetails.get("email");
        if(!newAdd.isEmpty() && !pin.isEmpty()){
            String url=urlDB.getSingleUrl("setaddress");

            List<NameValuePair> parameters = null;
            parameters = new ArrayList<NameValuePair>(2);
            parameters.add(new BasicNameValuePair("email", email));
            parameters.add(new BasicNameValuePair("address", newAdd));
            parameters.add(new BasicNameValuePair("pin_no", pin));
            parameters.add(new BasicNameValuePair("address_type", radio_btn_string));
            SetUserAddress setUserAddress=new SetUserAddress(parameters);
            setUserAddress.execute(url);
            addressSession.setMyAddress(newAdd,radio_btn_string,pin);
        }else {
            Toast.makeText(getApplicationContext(),"Write delivery address!",Toast.LENGTH_SHORT).show();
        }
    }

    public void getSavedAddress(){
        lastAddress=addressSession.getMyAddress();
        String sv_add=lastAddress.get("address");
        String sv_add_type=lastAddress.get("add_type");

        et_address.setText(sv_add);

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        if(sv_add_type.equals("Home Address")) {
            radioGroup.check(R.id.radiobutton_addtype_home);
        } else {
            radioGroup.check(R.id.radiobutton_addtype_work);
        }
    }

    private void permissionDialog() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(AddAddressActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(AddAddressActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    public boolean isJSONValid(String result) {
        try {
            new JSONObject(result);
        } catch (JSONException ex) {
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(result);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private void hideSoftKey(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items)
    {
        for(int i =0; i < items.length; i++)
        {
            if(inputStr.contains(items[i]))
            {
                return true;
            }
        }
        return false;
    }
}
