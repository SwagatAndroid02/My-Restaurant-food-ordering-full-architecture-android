package project.android.thincnext.myrestaurent.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;

/**
 * Created by thincnext on 02-Mar-18.
 */

public class AddressSession {

    private SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private static final String IS_ADDRESS_EXIST = "IsAddExist";
    private static final String PREF_NAME = "MyAddressDetails";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_ADD_TYPE = "add_type";
    public static final String KEY_ADD_PIN="pin_code";

    public AddressSession(Context cntx) {
        prefs = cntx.getSharedPreferences(PREF_NAME, cntx.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
        editor = prefs.edit();
    }

    public void setMyAddress(String address, String add_type,String pincode) {
        editor.putBoolean(IS_ADDRESS_EXIST, true);

        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_ADD_TYPE, add_type);
        editor.putString(KEY_ADD_PIN, pincode);
        editor.commit();
    }

    public HashMap<String, String> getMyAddress() {
        HashMap<String, String> myAddress = new HashMap<String, String>();
        myAddress.put(KEY_ADDRESS, prefs.getString(KEY_ADDRESS, null));
        myAddress.put(KEY_ADD_TYPE, prefs.getString(KEY_ADD_TYPE, null));
        myAddress.put(KEY_ADD_PIN, prefs.getString(KEY_ADD_PIN, null));

        return myAddress;
    }

    public void removeAddress(){
        editor.clear();
        editor.commit();
    }

    public boolean isAddressPresent(){
        return prefs.getBoolean(IS_ADDRESS_EXIST, false);
    }

}
