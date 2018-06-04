package project.android.thincnext.myrestaurent.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.activities.AddAddressActivity;
import project.android.thincnext.myrestaurent.activities.LoginActivity;
import project.android.thincnext.myrestaurent.database.UrlDatabaseHelper;
import project.android.thincnext.myrestaurent.model.ATaskCompleteListner;
import project.android.thincnext.myrestaurent.model.BackgroundServiceAsynkTask;
import project.android.thincnext.myrestaurent.model.LoginCredentialdata;
import project.android.thincnext.myrestaurent.model.LoginSessionManager;
import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener, ATaskCompleteListner<String>{


    private TextView txt_login;
    private ImageView pass_guid;

    private EditText username,email,phn,password,confirm_pass;
    private Button register_btn;
    private ProgressBar progress;
    private UrlDatabaseHelper db;

    LoginSessionManager loginSession;
    private FrameLayout mainLayout;


    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(){
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_register, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mainLayout= (FrameLayout) view.findViewById(R.id.registerfragment_main);
        loginSession=new LoginSessionManager(getActivity());
        txt_login= (TextView) view.findViewById(R.id.registerfragment_txtlogin);
        pass_guid= (ImageView) view.findViewById(R.id.registerfragment_passguid);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        db=new UrlDatabaseHelper(getActivity());
        username= (EditText) view.findViewById(R.id.register_user_name);
        email= (EditText) view.findViewById(R.id.register_email);
        phn= (EditText) view.findViewById(R.id.register_phone_num);
        password= (EditText) view.findViewById(R.id.register_password);
        confirm_pass= (EditText) view.findViewById(R.id.register_confirm_password);
        register_btn= (Button) view.findViewById(R.id.register_btn_register);
        progress= (ProgressBar) view.findViewById(R.id.register_btn_progress);

        txt_login.setOnClickListener(this);
        pass_guid.setOnClickListener(this);
        register_btn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v==txt_login){
            LoginActivity parentFrag = (LoginActivity)getActivity();
            parentFrag.loginRequest();
        }
        if(v==pass_guid){
            showPasswordguidDialog();
        }
        if (v==register_btn){
            hideSoftKey(v);
            registerNewUser();
        }
    }


    private void showPasswordguidDialog() {


        Button b;
        View bottomSheetView = getActivity().getLayoutInflater().inflate(R.layout.login_info_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(bottomSheetView);
        dialog.show();

        b= (Button) bottomSheetView.findViewById(R.id.login_info_dialog_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void registerNewUser() {
        String sName, sEmail, sPhn, sPassword, sConfrmPass;
        sName=username.getText().toString();
        sEmail=email.getText().toString();
        sPhn=phn.getText().toString();
        sPassword=password.getText().toString();
        sConfrmPass=confirm_pass.getText().toString();

        if(!sName.isEmpty() && !sEmail.isEmpty() && !sPhn.isEmpty() && !sPassword.isEmpty() && !sConfrmPass.isEmpty()){

            if(sPassword.equals(sConfrmPass)){
                int password_length = sPassword.length();
                if(password_length>=6){
                    proceedUserRegister();
                }else {
                    // password lenth than 6
                }

            }else {
                //password dont match
            }

        }else {
            //all fields are mandatory
        }

    }

    private void proceedUserRegister() {

        register_btn.setText("");
        progress.setVisibility(View.VISIBLE);

        String url=db.getSingleUrl("Regester");
        //String url="http://ecomercedata.somee.com/weblogin.asmx?op=Reg";
        String sName, sEmail, sPhn, sPassword, sConfrmPass;
        sName=username.getText().toString();
        sEmail=email.getText().toString();
        sPhn=phn.getText().toString();
        sPassword=password.getText().toString();

        List<NameValuePair> parameters = null;
        parameters=new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("uname", sName));
        parameters.add(new BasicNameValuePair("email", sEmail));
        parameters.add(new BasicNameValuePair("phno", sPhn));
        parameters.add(new BasicNameValuePair("pwd", sPassword));
       /* UserRegistration usrRedg=new UserRegistration(parameters);
        usrRedg.execute(params);*/

        BackgroundServiceAsynkTask backgroundService=new BackgroundServiceAsynkTask(getActivity(),this,parameters);
        backgroundService.execute(url);
    }

    @Override
    public void onTaskComplete(String responseString) {
        register_btn.setText("REGISTER");
        progress.setVisibility(View.GONE);

        if (responseString != null && !responseString.equals("") && !responseString.equals("null")) {
            if (!responseString.equals("login error")) {
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
                startActivity(new Intent(getActivity(), AddAddressActivity.class));
                getActivity().finish();
            }else {
                Snackbar.make(mainLayout,"sorry! user already exist",Snackbar.LENGTH_LONG).show();
            }
        }

    }

    private void hideSoftKey(View view){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

}
