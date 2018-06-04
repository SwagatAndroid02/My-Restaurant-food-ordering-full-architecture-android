package project.android.thincnext.myrestaurent.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.android.thincnext.myrestaurent.model.CartDataItems;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by thincnext on 22-Jan-18.
 */

public class AddToCartDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 11;

    // Database Name
    private static final String DATABASE_NAME = "cart_database;";

    private static String DB_PATH = "/data/data/project.android.thincnext.sanggrail/databases/";


    // Contacts table name
    private static final String TABLE_NAME = "myCartDataTable";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DISH_NAME = "dish_name";
    private static final String KEY_DISH_VEGAN_TYPE="dish_vegan_type";
    private static final String KEY_DISH_ID = "dish_id";
    private static final String KEY_DISH_QUANTITY = "dish_quantity";
    private static final String KEY_DISH_PRICE="dish_price";
    private static final String KEY_DISH_TOTAL_PRICE="dish_total_price";
    private static final String KEY_DISH_IMAGE = "dish_image";

    private Context context;

    public AddToCartDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
        try{
            String myPath = DB_PATH + DATABASE_NAME; // also check the extension of you db file
            File dbfile = new File(myPath);

        }
        catch(SQLiteException e){
            System.out.println("Database doesn't exist");
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_DISH_NAME + " TEXT, " +
                KEY_DISH_ID + " TEXT, " +
                KEY_DISH_QUANTITY + " TEXT, " +
                KEY_DISH_PRICE + " TEXT, " +
                KEY_DISH_TOTAL_PRICE + " TEXT, " +
                KEY_DISH_VEGAN_TYPE + " TEXT, " +
                KEY_DISH_IMAGE + " TEXT);";
        db.execSQL(CREATE_CART_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            onCreate(db);
        }
    }


    public void addToCart(CartDataItems cartData) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DISH_NAME, cartData.getDishName());
        values.put(KEY_DISH_ID, cartData.getDishId());
        values.put(KEY_DISH_QUANTITY, cartData.getDishQuantity());
        values.put(KEY_DISH_PRICE,cartData.getDishPrice());
        values.put(KEY_DISH_TOTAL_PRICE,cartData.getDishTotalPrice());
        values.put(KEY_DISH_VEGAN_TYPE,cartData.getDishVeganType());
        values.put(KEY_DISH_IMAGE,cartData.getDishImage());

        //inserting the row in table
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    //get all cartitems
    public List<CartDataItems> getAllCartItems() {
        List<CartDataItems> cartitems = new ArrayList<CartDataItems>();
        //select all query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CartDataItems listCartData = new CartDataItems();
                listCartData.set_id(cursor.getString(0));
                listCartData.setDishName(cursor.getString(1));
                listCartData.setDishId(cursor.getString(2));
                listCartData.setDishQuantity(cursor.getString(3));
                listCartData.setDishPrice(cursor.getString(4));
                listCartData.setDishtotalPrice(cursor.getString(5));
                listCartData.setDishVeganType(cursor.getString(6));
                listCartData.setDishImage(cursor.getString(7));

                //adding item to list
                cartitems.add(listCartData);
            } while (cursor.moveToNext());
        }
        return cartitems;
    }

    public boolean checkProductExits(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + KEY_DISH_NAME + " FROM " + TABLE_NAME + " WHERE " + KEY_DISH_ID + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{id});
        if (cursor.getCount() > 0)
        {
            return true;
        }
        else
            return false;
    }

    public Cursor qureyData(String qurey) {
        SQLiteDatabase mydatabase;
        mydatabase = this.getReadableDatabase();
        return mydatabase.rawQuery(qurey, null);
    }


    public String getTotalPrice() {
        String totalPrice = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + KEY_DISH_TOTAL_PRICE+ ") as TotalPrice FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            double total = cursor.getInt(cursor.getColumnIndex("TotalPrice"));
            totalPrice=String.valueOf(total);
            return totalPrice;
        }
        return totalPrice;
    }

    public String getTotalNumberOfItemCount(){
        String totalCount = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + KEY_DISH_QUANTITY + ") as TotalItemQuantity FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            int total = cursor.getInt(cursor.getColumnIndex("TotalItemQuantity"));
            totalCount=String.valueOf(total);
            return totalCount;
        }
        return totalCount;
    }

    public int getAllCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public String getSingleItemQuantity(String dishId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String price = "";
        try {
            cursor = db.rawQuery("SELECT "+KEY_DISH_QUANTITY+" FROM "+TABLE_NAME+" WHERE "+KEY_DISH_ID+"=?", new String[] {dishId + ""});
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                price = cursor.getString(0);
            }
            return price;
        }finally {
            cursor.close();
        }
    }

    public void removeAllItems(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public void removeSinglecartitem(String dishId) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();

        //Execute sql query to remove from database
        //NOTE: When removing by String in SQL, value must be enclosed with ''
        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + KEY_DISH_ID + "= '" + dishId + "'");

        //Close the database
        database.close();
    }

    public void addOneExistingItem(String dishId, String value,String price){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_NAME + " SET "
                + KEY_DISH_TOTAL_PRICE + " = " + KEY_DISH_TOTAL_PRICE + " + "+price +", "
                + KEY_DISH_QUANTITY + " = " + KEY_DISH_QUANTITY + " + "+value+" WHERE "
                + KEY_DISH_ID + " = '" + dishId + "'");

        db.close();
    }

    public void minusOneExistingItem(String dishID, String price){
        SQLiteDatabase db = this.getWritableDatabase();

        String totalcount = null;

        db.execSQL("UPDATE " + TABLE_NAME + " SET "
                + KEY_DISH_TOTAL_PRICE + " = " + KEY_DISH_TOTAL_PRICE + " - "+price +", "
                + KEY_DISH_QUANTITY + " = " + KEY_DISH_QUANTITY + " - "+" 1 "+" WHERE "
                + KEY_DISH_ID + " = '" + dishID + "'");

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT "+KEY_DISH_QUANTITY+" FROM "+TABLE_NAME+" WHERE "+KEY_DISH_ID+" =?", new String[] {dishID + ""});

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                totalcount = cursor.getString(0);
            }
        }finally {
            cursor.close();
        }

        if(Integer.valueOf(totalcount)==0){
            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + KEY_DISH_ID + "= '" + dishID + "'");
        }

        db.close();
    }

    public JSONArray getJResults() {

        String myPath = DB_PATH + DATABASE_NAME;
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        String searchQuery = "SELECT  * FROM " + TABLE_NAME;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null );

        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ ) {
                if( cursor.getColumnName(i) != null ) {
                    try {
                        if( cursor.getString(i) != null ) {
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
             resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString() );
        return resultSet;
    }

}
