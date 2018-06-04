package project.android.thincnext.myrestaurent.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.database.AddToCartDBHelper;
import project.android.thincnext.myrestaurent.model.AddressSession;
import project.android.thincnext.myrestaurent.model.CheckInternetConnection;
import project.android.thincnext.myrestaurent.model.LoginSessionManager;

public class CheckOutActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView txt_totalPrice,txt_sgst,txt_cgst, txt_finalPayable, pinCodeMsg;

    private RadioButton radioButton;
    private RadioGroup radioGroup;
    private Button proceed;

    AddToCartDBHelper db;

    private LinearLayout parentLayout;

    private LoginSessionManager loginSessionManager;

    private CheckInternetConnection internetConnection;

    private Spinner pincode_spinner;
    private TextView pincode_spinner_text;

    private Toolbar toolbar;
    String totalPrice;
    private EditText txt_mobile, txt_deliveryadd, txt_landmark;
    String paymentOption;

    private ImageView addrssEditImg;
    private TextView address_txt;
    private HashMap<String,String> addresDetails;
    private AddressSession addressSession;

    String[] pinCodeList;

    DecimalFormat decimal_format = new DecimalFormat("0.00");

    HashMap<String, String> userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        toolbar = (Toolbar) findViewById(R.id.chckout_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Checkout");

        int colorARGB = R.color.Black;
        Drawable navIcon = toolbar.getNavigationIcon();
        navIcon.setColorFilter(getResources().getColor(colorARGB), PorterDuff.Mode.SRC_IN);

        internetConnection = new CheckInternetConnection(CheckOutActivity.this);
        loginSessionManager = new LoginSessionManager(CheckOutActivity.this);

        addressSession=new AddressSession(CheckOutActivity.this);
        if(loginSessionManager.isLoggedIn()) {
            if (!addressSession.isAddressPresent()) {
                startActivity(new Intent(CheckOutActivity.this, AddAddressActivity.class));
            }
        }

      /*  boolean b = loginSessionManager.isLoggedIn();
        if(!b){
            startActivity(new Intent(CheckOutActivity.this, LoginActivity.class));
        }
*/
        if (getIntent().getExtras() != null) {
            totalPrice = getIntent().getStringExtra("TOTAL_ADDED_PRICE");
        }

        pinCodeList = getResources().getStringArray(R.array.pin_codes);

        db = new AddToCartDBHelper(CheckOutActivity.this);

        address_txt= (TextView) findViewById(R.id.chckout_txt_deliveryadd);
        addrssEditImg= (ImageView) findViewById(R.id.chckout_btn_add_deliveryadd);

        pincode_spinner = (Spinner) findViewById(R.id.chckout_pincode_spinner);
        pincode_spinner_text = (TextView) findViewById(R.id.chckout_pincode_spinner_text_name);
        txt_totalPrice = (TextView) findViewById(R.id.chckout_total_price);

        txt_sgst = (TextView) findViewById(R.id.chckout_sgst_price);
        txt_cgst = (TextView) findViewById(R.id.chckout_cgst_price);

        getCurrentSavedAddrss();


        //txt_taxPrice = (TextView) findViewById(R.id.chckout_tax_price);
        txt_finalPayable = (TextView) findViewById(R.id.chckout_payable);
        parentLayout = (LinearLayout) findViewById(R.id.chckout_parent_layout);
        pinCodeMsg = (TextView) findViewById(R.id.chckout_txt_pincode_msg);

        txt_mobile = (EditText) findViewById(R.id.chckout_et_mobile);
        //txt_deliveryadd = (EditText) findViewById(R.id.chckout_et_deliveryadd);
        txt_landmark = (EditText) findViewById(R.id.chckout_et_landmark);
        //txt_pincode= (EditText) findViewById(R.id.chckout_et_pin);

        userDetails = loginSessionManager.getUserDetails();
        String phone_nm = userDetails.get("phone");
        txt_mobile.setText(phone_nm);

        radioGroup = (RadioGroup) findViewById(R.id.chckout_radiogroup);

        double tp = getTaxPrice(totalPrice);

        pincode_spinner.setPrompt("select from list");


        //Toast.makeText(getApplicationContext(),String.valueOf(tp),Toast.LENGTH_LONG).show();

        proceed = (Button) findViewById(R.id.chckout_proceed_btn);

        String s = String.valueOf(tp).substring(0, 4);

        //0.00 formating
        double number = Double.parseDouble(totalPrice);
        String formated_totalPrice = decimal_format.format(number);


        txt_totalPrice.setText(formated_totalPrice);
        //txt_taxPrice.setText(s);
        txt_sgst.setText(s);
        txt_cgst.setText(s);

        double ttp = Double.valueOf(totalPrice);

        double ft = Double.valueOf(s) + ttp;

        String pay = String.valueOf(ft);
        double d_pay = Double.parseDouble(pay);

        String fPay = decimal_format.format(d_pay);

        txt_finalPayable.setText(fPay);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });


        pincode_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinner_text = pincode_spinner.getSelectedItem().toString();
                switch (spinner_text) {
                    case "560041":
                        pincode_spinner_text.setText("Jayanagar");
                        break;
                    case "560011":
                        pincode_spinner_text.setText("Jayanagar 3rd Block");
                        break;
                    case "560070":
                        pincode_spinner_text.setText("Jayanagar West / BSK 3rd Stage");
                        break;
                    case "560069":
                        pincode_spinner_text.setText("Jayanagar East");
                        break;
                    case "560078":
                        pincode_spinner_text.setText("JP Nagar 3rd Phase");
                        break;
                    case "560076":
                        pincode_spinner_text.setText("JP Nagar 8th Phase / BTM 2nd stage");
                        break;
                    case "560085":
                        pincode_spinner_text.setText("BSK 3rd Stage");
                        break;
                    case "560050":
                        pincode_spinner_text.setText("BSK");
                        break;
                    case "560068":
                        pincode_spinner_text.setText("BTM layout");
                        break;
                    default:
                        pincode_spinner_text.setText("select pin code");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        proceed.setOnClickListener(this);
        addrssEditImg.setOnClickListener(this);
    }


    public static boolean containsAny(String str, String[] words) {
        boolean bResult = false; // will be set, if any of the words are found
        //String[] words = {"word1", "word2", "word3", "word4", "word5"};

        List<String> list = Arrays.asList(words);
        for (String word : list) {
            boolean bFound = str.contains(word);
            if (bFound) {
                bResult = bFound;
                break;
            }
        }
        return bResult;
    }


    public double getTaxPrice(String price) {

        double d = Double.valueOf(price);
        double k = (d * (2.5f / 100.0f));
        return k;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        boolean b = loginSessionManager.isLoggedIn();
        boolean isAddrss=addressSession.isAddressPresent();
        if (b) {
            userDetails = loginSessionManager.getUserDetails();
            String phone_nm = userDetails.get("phone");
            txt_mobile.setText(phone_nm);
        }
        if(isAddrss){
            getCurrentSavedAddrss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == proceed) {
            if (internetConnection.isConnected()) {
                boolean b = loginSessionManager.isLoggedIn();
                if (b) {
                    proceedProcess();

                }else {
                    startActivity(new Intent(CheckOutActivity.this,LoginActivity.class));

                }
            }else {
                Toast.makeText(getApplicationContext(),"no internet connection!",Toast.LENGTH_SHORT).show();
            }
        }
        if(v==addrssEditImg){
            if(loginSessionManager.isLoggedIn()) {
                startActivity(new Intent(CheckOutActivity.this, AddAddressActivity.class));
            }else {
                startActivity(new Intent(CheckOutActivity.this,LoginActivity.class));

            }
        }
    }


    /*private void getOrderId() {
        GetOrder getOrder = new GetOrder();
        getOrder.execute();
    }*/

    private void proceedProcess() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        //String rId = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();

        if (selectedId != -1) {
            radioButton = (RadioButton) findViewById(selectedId);

            paymentOption = radioButton.getText().toString();
            //Toast.makeText(getApplicationContext(),paymentOption,Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Select any payment method!", Toast.LENGTH_LONG).show();
        }

        String mobilenum, deliveryadd, landmark, pincode;

        mobilenum = txt_mobile.getText().toString();
        deliveryadd = address_txt.getText().toString();
        landmark = txt_landmark.getText().toString();
        if(landmark.equals("")){
            landmark="none";
        }
        pincode = pincode_spinner.getSelectedItem().toString();

        if (!mobilenum.isEmpty() && !deliveryadd.isEmpty() && !landmark.isEmpty() && !pincode.isEmpty()) {

            try {
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = new JSONObject();

                JSONObject arrayParent=new JSONObject();
                JSONArray jArrayObj=new JSONArray();
               /* Gson gson = new GsonBuilder().create();
                JsonArray myCustomArray = gson.toJsonTree(db.getJResults()).getAsJsonArray();*/

                JSONArray jss = db.getJResults();

                userDetails = new HashMap<>();
                userDetails = loginSessionManager.getUserDetails();
                String username = userDetails.get("user_name");
                String phone_nm = userDetails.get("phone");
                String email = userDetails.get("email");

                jsonObject.put("username", username);
                jsonObject.put("email", email);
                jsonObject.put("user_phone_num", phone_nm);
                jsonObject.put("deliveryaddress", deliveryadd);
                jsonObject.put("landmark", landmark);
                jsonObject.put("pin", pincode);
                jsonObject.put("Mobile", mobilenum);
                jsonObject.put("payment_type", paymentOption);

                jArrayObj.put(jsonObject);

                //parent.put("orders", jsonObject);
                parent.put("orders", jArrayObj);
                arrayParent.put("cartitem", jss);
                Log.d("output", parent.toString());

                Intent theIntent = new Intent(CheckOutActivity.this, FinalPlaceOrderActivity.class);
                theIntent.putExtra("MYJSONOBJECT", parent.toString());
                theIntent.putExtra("MYJSONARRAY", arrayParent.toString());

                db.removeAllItems();
                startActivity(theIntent);
                this.finish();
                return;

            } catch (JSONException e) {
                e.printStackTrace();

                Log.e("CARTJSONERROR", "exception : ", e);
            }

        } else {

            showSnakebar("All fields are mandatory!");
        }
    }

    private void showSnakebar(String msg) {
        Snackbar snackbar = Snackbar
                .make(parentLayout, msg, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    public void getCurrentSavedAddrss(){
        addresDetails=new HashMap<String, String>();
        addresDetails=addressSession.getMyAddress();
        String addrss = addresDetails.get("address");
        String pincode=addresDetails.get("pin_code");
        address_txt.setText(addrss);

        String compareValue = pincode;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pin_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pincode_spinner.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            pincode_spinner.setSelection(spinnerPosition);
        }
    }


}



