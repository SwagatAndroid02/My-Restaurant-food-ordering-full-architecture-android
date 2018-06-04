package project.android.thincnext.myrestaurent.Fragment;


import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import project.android.thincnext.myrestaurent.R;
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
public class LoginFragment extends Fragment implements View.OnClickListener, ATaskCompleteListner<String> {


    private TextView txtRegister, forgot_password;

    private EditText userName, password;
    private Button btn_login;
    private ProgressBar loading;

    LoginSessionManager loginSession;
    private UrlDatabaseHelper db;

    private RelativeLayout mainLayout;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        loginSession = new LoginSessionManager(getActivity());
        mainLayout = (RelativeLayout) view.findViewById(R.id.loginfragment_main);
        db = new UrlDatabaseHelper(getActivity());

        txtRegister = (TextView) view.findViewById(R.id.loginfragment_txtregister);
        forgot_password = (TextView) view.findViewById(R.id.loginfragment_forget_password);

        userName = (EditText) view.findViewById(R.id.loginfragment_et_email);
        password = (EditText) view.findViewById(R.id.loginfragment_et_password);
        btn_login = (Button) view.findViewById(R.id.loginfragment_btn_login);
        loading = (ProgressBar) view.findViewById(R.id.loginfragment_progress);
        loading.setVisibility(View.GONE);


        txtRegister.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        forgot_password.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == txtRegister) {
            LoginActivity parentFrag = (LoginActivity) getActivity();
            parentFrag.registerRequest();
        }
        if (v == btn_login) {
            hideSoftKey(v);
            String usernm, pass;
            usernm = userName.getText().toString();
            pass = password.getText().toString();
            if (!usernm.isEmpty() && !pass.isEmpty()) {
                loginUser(usernm, pass);
            } else {
                Toast.makeText(getActivity(), "All fields are mandatory!", Toast.LENGTH_LONG).show();
            }
        }
        if (v == forgot_password) {
            showForgotPasswordDialog();
        }
    }

    private void loginUser(String usernm, String pass) {

        btn_login.setText("");
        loading.setVisibility(View.VISIBLE);

        List<NameValuePair> parameters = null;
        parameters = new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("u", usernm));
        parameters.add(new BasicNameValuePair("p", pass));

        String url = db.getSingleUrl("Login");
        BackgroundServiceAsynkTask backgroundService = new BackgroundServiceAsynkTask(getActivity(), this, parameters);
        backgroundService.execute(url);
    }

    @Override
    public void onTaskComplete(String responseString) {
        btn_login.setText("LOGIN");
        loading.setVisibility(View.GONE);

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
                getActivity().finish();
            } else {
                Snackbar.make(mainLayout, "user name and password incorrect!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void showForgotPasswordDialog() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.forgot_password_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.et_forgot_pass_dealog);

        final TextView ok= (TextView) promptsView.findViewById(R.id.tv_forgot_pass_ok);
        final TextView cancel= (TextView) promptsView.findViewById(R.id.tv_forgot_pass_cancel);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail=userInput.getText().toString();
                if(!userEmail.isEmpty()) {
                    requestNewPassword(userEmail);
                    alertDialog.dismiss();
                }else {
                    Toast.makeText(getActivity(),"Enter valid email id.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // create alert dialog
        // show it
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

    }

    private void  requestNewPassword(String email){
        List<NameValuePair> parameters = null;
        parameters = new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("user_email_id", email));

        String url = db.getSingleUrl("new_password");
        /*BackgroundServiceAsynkTask backgroundService = new BackgroundServiceAsynkTask(getActivity(), this, parameters);
        backgroundService.execute(url);*/
        RequestNewPassword requestNewPassword=new RequestNewPassword(url,parameters);
        requestNewPassword.execute();
    }

    private void hideSoftKey(View view){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    //for new password
    private class RequestNewPassword extends AsyncTask<String, Void, String>{

        String url;
        private List<NameValuePair> parameters;

        RequestNewPassword(String url, List<NameValuePair> parameters){
            this.url=url;
            this.parameters=parameters;
        }
        @Override
        protected String doInBackground(String... params) {

            try {
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    String response;
                    return response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Snackbar.make(mainLayout, s, Snackbar.LENGTH_LONG).show();
        }
    }

}
