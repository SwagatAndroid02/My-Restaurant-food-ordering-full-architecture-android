package project.android.thincnext.myrestaurent.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import project.android.thincnext.myrestaurent.Fragment.LoginFragment;
import project.android.thincnext.myrestaurent.Fragment.RegisterFragment;
import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.model.LoginSessionManager;

public class LoginActivity extends AppCompatActivity {

    private String requestPage;
    private Fragment fragment;

    private Toolbar toolbar;

    private LoginSessionManager loginSessionManager;
    boolean isUserLogedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar= (Toolbar) findViewById(R.id.login_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("LOGIN");
        int colorARGB = R.color.Black;
        Drawable navIcon = toolbar.getNavigationIcon();
        navIcon.setColorFilter(getResources().getColor(colorARGB), PorterDuff.Mode.SRC_IN);

        loginSessionManager=new LoginSessionManager(LoginActivity.this);

        fragment = LoginFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.login_activity_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isUserLogedIn=loginSessionManager.isLoggedIn();
                if(isUserLogedIn){
                    startActivity(new Intent(LoginActivity.this,Home.class));
                    finish();
                    return;
                }else {
                    finish();
                    return;
                }
            }
        });
    }

    public void loginRequest(){
        requestPage="LOGIN";
        changeFragment();
        getSupportActionBar().setTitle("LOGIN");
    }
    public void registerRequest(){
        requestPage="REGISTER";
        changeFragment();
        getSupportActionBar().setTitle("REGISTER");
    }

    private void changeFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (requestPage.equals("LOGIN")){
            fragment = LoginFragment.newInstance();
            transaction.setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_from_left);

        }else if(requestPage.equals("REGISTER")){
            fragment = RegisterFragment.newInstance();
            transaction.setCustomAnimations(R.anim.fragment_enter_from_left, R.anim.fragment_exit_from_right);
        }

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.login_activity_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            finish();
        }
            return super.onKeyDown(keyCode, event);
    }

}
