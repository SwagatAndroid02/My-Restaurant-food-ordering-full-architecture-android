package project.android.thincnext.myrestaurent.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by thincnext on 26-Jan-18.
 */

public class LoginSessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "MyLoginSessionPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "user_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";

    public LoginSessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, String email, String phone){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        // commit changes
        editor.commit();

    }

    /*public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){

           *//* // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);*//*

        }

    }
*/
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PHONE,pref.getString(KEY_PHONE,null));

        // return user
        return user;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        /*
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);*/
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }


}
