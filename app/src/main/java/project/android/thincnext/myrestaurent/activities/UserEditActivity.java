package project.android.thincnext.myrestaurent.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.database.UrlDatabaseHelper;
import project.android.thincnext.myrestaurent.model.ATaskCompleteListner;
import project.android.thincnext.myrestaurent.model.AddressSession;
import project.android.thincnext.myrestaurent.model.BackgroundServiceAsynkTask;
import project.android.thincnext.myrestaurent.model.LoginCredentialdata;
import project.android.thincnext.myrestaurent.model.LoginSessionManager;

public class UserEditActivity extends AppCompatActivity implements View.OnClickListener, ATaskCompleteListner<String>{


    TextView tv_email, tv_phone, tv_username, tv_address;
    TextView change_address;
    LinearLayout layout_username,layout_password;
    EditText et_username, et_current_pass ,et_pass, et_confirm_pass;
    TextView btn_new_username, btn_new_password;

    boolean expand_username=false;

    HashMap<String, String> userDetails;
    private HashMap<String,String> addresDetails;
    private AddressSession addressSession;
    private LoginSessionManager loginSessionManager;

    private UrlDatabaseHelper db;

    private LinearLayout mainLayout;
    LoginSessionManager loginSession;

    private ProgressBar loading;

    //new things added
    private ImageView backArrow;
    private Button btn_all_edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        backArrow= (ImageView) findViewById(R.id.useredit_activity_btn_back_arrow);
        btn_all_edit= (Button) findViewById(R.id.useredit_activity_btn_all_edit);


        loginSession = new LoginSessionManager(UserEditActivity.this);
        db = new UrlDatabaseHelper(UserEditActivity.this);

        mainLayout= (LinearLayout) findViewById(R.id.useredit_activity_main_layout);

        loading= (ProgressBar) findViewById(R.id.useredit_activity_progress);

        tv_email= (TextView) findViewById(R.id.useredit_activity_tv_email);
        tv_username= (TextView) findViewById(R.id.useredit_activity_tv_username);
        tv_phone= (TextView) findViewById(R.id.useredit_activity_tv_phone1);
        tv_address= (TextView) findViewById(R.id.useredit_activity_tv_address);

        layout_password= (LinearLayout) findViewById(R.id.useredit_activity_password_extent_layout);
        layout_username= (LinearLayout) findViewById(R.id.useredit_activity_username_extent_layout);

        et_username= (EditText) findViewById(R.id.useredit_activity_et_username);
        et_current_pass= (EditText) findViewById(R.id.useredit_activity_et_current_pass);
        et_pass= (EditText) findViewById(R.id.useredit_activity_et_pass);
        et_confirm_pass= (EditText) findViewById(R.id.useredit_activity_et_confirm_pass);

        change_address= (TextView) findViewById(R.id.useredit_activity_change_address);

        btn_new_username= (TextView) findViewById(R.id.useredit_activity_btn_done_username);
        btn_new_password= (TextView) findViewById(R.id.useredit_activity_btn_done_password);

        onPageStart();

        backArrow.setOnClickListener(this);
        btn_all_edit.setOnClickListener(this);
        change_address.setOnClickListener(this);

        btn_new_password.setOnClickListener(this);
        btn_new_username.setOnClickListener(this);



    }

    private void onPageStart(){
        expand_username=false;
        et_current_pass.setText("");
        et_username.setText("");
        et_confirm_pass.setText("");
        et_pass.setText("");

        layout_username.setVisibility(View.GONE);
        layout_password.setVisibility(View.GONE);

        addressSession=new AddressSession(UserEditActivity.this);
        loginSessionManager = new LoginSessionManager(UserEditActivity.this);
        userDetails = new HashMap<>();
        userDetails = loginSessionManager.getUserDetails();
        String username = userDetails.get("user_name");
        String phone_nm = userDetails.get("phone");
        String email=userDetails.get("email");

        tv_phone.setText(phone_nm);
        tv_email.setText(email);
        tv_username.setText(username);

        addresDetails=new HashMap<String, String>();
        addresDetails=addressSession.getMyAddress();
        boolean isAddrss=addressSession.isAddressPresent();
        if (isAddrss) {
            String addrss = addresDetails.get("address");
            tv_address.setText(addrss);
        }else {
            tv_address.setText("no save address found");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onPageStart();
    }

    @Override
    public void onClick(View v) {
        ViewAnimationUtils animUtil=new ViewAnimationUtils();

        if(v==backArrow){
            finish();
            return;
        }
        if(v==btn_all_edit){
            if(!expand_username){
                animUtil.expand(layout_password);
                animUtil.expand(layout_username);
                expand_username=true;
            }else {
                animUtil.collapse(layout_password);
                animUtil.collapse(layout_username);
                expand_username=false;
            }
        }
        if(v==change_address){
            startActivity(new Intent(UserEditActivity.this,AddAddressActivity.class));
        }

        if(v==btn_new_username){
            hideSoftKey(v);
            String new_username=et_username.getText().toString().trim();
            if(!new_username.isEmpty()){
                changeNewUserName(new_username);
            }else {
                Toast.makeText(getApplicationContext(), "please enter new user name.", Toast.LENGTH_SHORT).show();
            }
        }
        if(v==btn_new_password){
            hideSoftKey(v);
            String sCurrentPass,sPass,sConfirmPass;
            sPass=et_pass.getText().toString().trim();
            sConfirmPass=et_confirm_pass.getText().toString().trim();
            sCurrentPass=et_current_pass.getText().toString();
            if(!sCurrentPass.isEmpty()) {
                if (!sPass.isEmpty()) {
                    if (!sConfirmPass.isEmpty()) {
                        if (sPass.equals(sConfirmPass)) {
                            if (sPass.length() >= 6) {


                                changePassword(sCurrentPass,sPass);


                            } else {
                                Toast.makeText(getApplicationContext(), "password must contain 6 or more characters.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "password not matching.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "please confirm password.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "please enter new password.", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getApplicationContext(), "please enter current password.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changePassword(String sCurrentPass ,String sPass) {
        loading.setVisibility(View.VISIBLE);
        String email=userDetails.get("email");

        List<NameValuePair> parameters = null;
        parameters = new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("old_pwd", sCurrentPass));
        parameters.add(new BasicNameValuePair("new_pwd", sPass));
        parameters.add(new BasicNameValuePair("conf_pwd", sPass));
        parameters.add(new BasicNameValuePair("email", email));

        String url = db.getSingleUrl("change_password");
        BackgroundServiceAsynkTask backgroundService = new BackgroundServiceAsynkTask(getApplicationContext(), this, parameters);
        backgroundService.execute(url);
    }


    private void changeNewUserName(String new_username) {
        loading.setVisibility(View.VISIBLE);
        String email=userDetails.get("email");

        List<NameValuePair> parameters = null;
        parameters = new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("Un", new_username));
        parameters.add(new BasicNameValuePair("email", email));

        String url = db.getSingleUrl("change_username");
        BackgroundServiceAsynkTask backgroundService = new BackgroundServiceAsynkTask(getApplicationContext(), this, parameters);
        backgroundService.execute(url);
    }

    @Override
    public void onTaskComplete(String responseString) {

        if (responseString != null && !responseString.equals("") && !responseString.equals("null")) {
            if (!responseString.equals("Updation_unsuccessfull")) {
                List<LoginCredentialdata> data = new ArrayList<>();

                try {
                    JSONObject jsonObj = new JSONObject(responseString);

                    JSONArray jArray = jsonObj.getJSONArray("Table");

                /*for (int i = jArray.length() - 1; i < jArray.length(); i--) {*/
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        LoginCredentialdata loginCredentialdata = new LoginCredentialdata();
                        loginCredentialdata.userName = json_data.getString("Username");
                        loginCredentialdata.email = json_data.getString("Email");
                        loginCredentialdata.phoneNum = json_data.getString("Phno");
                        data.add(loginCredentialdata);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LoginCredentialdata current = data.get(0);
                loginSession.createLoginSession(current.userName, current.email, current.phoneNum);
            } else {
                Snackbar.make(mainLayout,"Sorry! updation unsuccessfull. try again.",Snackbar.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"something went wrong!",Toast.LENGTH_SHORT).show();
        }
        loading.setVisibility(View.GONE);
        onPageStart();
    }


    //for hide and show animation
    private class ViewAnimationUtils {

        void expand(final View v) {
            v.measure(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
            final int targtetHeight = v.getMeasuredHeight();

            v.getLayoutParams().height = 0;
            v.setVisibility(View.VISIBLE);
            Animation a = new Animation()
            {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    v.getLayoutParams().height = interpolatedTime == 1
                            ? ActionBar.LayoutParams.WRAP_CONTENT
                            : (int)(targtetHeight * interpolatedTime);
                    v.requestLayout();


                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration((int)(targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
            v.startAnimation(a);
        }

        void collapse(final View v) {
            final int initialHeight = v.getMeasuredHeight();

            Animation a = new Animation()
            {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if(interpolatedTime == 1){
                        v.setVisibility(View.GONE);
                    }else{
                        v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                        v.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
            v.startAnimation(a);

        }
    }

    private void hideSoftKey(View view){
        InputMethodManager imm = (InputMethodManager)UserEditActivity.this.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
}
