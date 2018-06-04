package project.android.thincnext.myrestaurent.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import project.android.thincnext.myrestaurent.activities.SplashScreen;
import project.android.thincnext.myrestaurent.model.UrlLoaderItem;

/**
 * Created by thincnext on 18-Jan-18.
 */

public class UrlDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private SplashScreen activity;

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "url_loader_database;";

    public static  final String DB_PATH = "";
    // Contacts table name
    private static final String TABLE_NAME = "url_loader_table";

    private static final String KEY_ID = "id";
    private static final String KEY_URL_NAME = "url_name";
    private static final String KEY_URL_ADDRESS = "url_address";


    public UrlDatabaseHelper(Context context,SplashScreen activity) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.activity=activity;
    }

    public UrlDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_URL_NAME + " TEXT, " +
                KEY_URL_ADDRESS + " TEXT);";
        db.execSQL(CREATE_CART_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            onCreate(db);
        }
    }

    public void loadUrl(String urlName, String urlAddress) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_URL_NAME, urlName);
        values.put(KEY_URL_ADDRESS, urlAddress);
        //inserting the row in table
        db.insert(TABLE_NAME, null, values);
        db.close();
        activity.goToHomePage();
    }


    public List<UrlLoaderItem> getAllURLs() {
        List<UrlLoaderItem> urlLoaderItem = new ArrayList<UrlLoaderItem>();
        //select all query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                UrlLoaderItem urlLoader = new UrlLoaderItem();
                urlLoader.set_id(cursor.getString(0));
                urlLoader.setUrlName(cursor.getString(1));
                urlLoader.setUrlAddress(cursor.getString(2));


                //adding item to list
                urlLoaderItem.add(urlLoader);
            } while (cursor.moveToNext());
        }
        return urlLoaderItem;
    }



    public void removeAllItems(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }


    public String getSingleUrl(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String price = "";
        try {
            cursor = db.rawQuery("SELECT "+KEY_URL_ADDRESS+" FROM "+TABLE_NAME+" WHERE "+KEY_URL_NAME+"=?", new String[] {title + ""});
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                price = cursor.getString(0);
            }
            return price;
        }finally {
            cursor.close();
        }
    }

}
