package project.android.thincnext.myrestaurent.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.android.thincnext.myrestaurent.BuildConfig;
import project.android.thincnext.myrestaurent.Fragment.SubscriptionFragment;
import project.android.thincnext.myrestaurent.R;
import project.android.thincnext.myrestaurent.adapters.MainItemListAdapter;
import project.android.thincnext.myrestaurent.database.AddToCartDBHelper;
import project.android.thincnext.myrestaurent.database.UrlDatabaseHelper;
import project.android.thincnext.myrestaurent.model.ATaskCompleteListner;
import project.android.thincnext.myrestaurent.model.BackgroundServiceAsynkTask;
import project.android.thincnext.myrestaurent.model.CheckInternetConnection;
import project.android.thincnext.myrestaurent.model.LoginSessionManager;
import project.android.thincnext.myrestaurent.model.MainItemListData;
import project.android.thincnext.myrestaurent.model.UrlLoaderItem;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ATaskCompleteListner<String> {

    private Toolbar maintoolbar;
    private Fragment fragment;
    private RecyclerView recyclerView;
    private MainItemListAdapter adapter;
    private TextView txt_cat;
    private String currentCategory = null;
    String url;

    private RelativeLayout notifCount;
    private TextView cart_badge;

    private CheckInternetConnection checkInternetConnection;

    private RelativeLayout loading_layout, nointernet_layout;
    private Button internet_retry;
    /* private TabLayout tabLayout;
     private ViewPager viewPager;*/
   /* private TextView tv_beakfast, tv_dessert, tv_startr, tv_soup, tv_sandwich, tv_pizza, tv_burger,tv_pasta,tv_salad
            ,tv_brownrice, tv_combo;*/
    private NestedScrollView nestedScrollView;

    private NavigationView navigationView;

    private LinearLayout tiplayout;
    private Button tipButton;

    private LoginSessionManager loginSessionManager;
    boolean isUserLogedIn;
    private UrlDatabaseHelper db;
    private AddToCartDBHelper addToCartDBHelper;

    HashMap<String, String> userDetails;

    private TextView txt_swagatapps;

    private UrlLoaderItem urlLoader;

    String whileRestar = "notFromLoginPage";
    private static final int limit = 1;

    /* private toan.android.floatingactionmenu.FloatingActionButton fab_breakfast, fab_brownrice, fab_burger, fab_combos, fab_dessert,
             fab_pasta, fab_pizza, fab_salad, fab_sandwich, fab_soup, fab_starter;
     private toan.android.floatingactionmenu.FloatingActionsMenu floatingActionMenu1, floatingActionMenu2;*/
    int nY_Pos;

    private ImageView menu_opt_img;

    private TextView header_username, header_phone_number,header_signin;
    private ImageView header_user_setting,header_user_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        maintoolbar = (Toolbar) findViewById(R.id.home_toolbar);
        //tabLayout= (TabLayout) findViewById(home_catagory_tabs);
        setSupportActionBar(maintoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //maintoolbar.setLogo(R.drawable.sang_grail_title);

        recyclerView = (RecyclerView) findViewById(R.id.home_recyclerview);
        txt_cat = (TextView) findViewById(R.id.home_txt_selected_category);
        db = new UrlDatabaseHelper(Home.this);
        urlLoader = new UrlLoaderItem();

        checkInternetConnection = new CheckInternetConnection(Home.this);
        internet_retry = (Button) findViewById(R.id.home_internet_retry);
        nointernet_layout = (RelativeLayout) findViewById(R.id.home_internet_connection_layout);
        loading_layout = (RelativeLayout) findViewById(R.id.home_loading);
        loading_layout.setVisibility(View.VISIBLE);


        if (checkInternetConnection.isConnected()) {
            GetVersionCode getVersionCode = new GetVersionCode();
            getVersionCode.execute();

            nointernet_layout.setVisibility(View.GONE);

            SharedPreferences category_prefs = getSharedPreferences("MY_CURRENT_CATEGORY", MODE_PRIVATE);
            String restoredText = category_prefs.getString("text", null);
            if (restoredText != null) {
                currentCategory = category_prefs.getString("current_category", "");//"No name defined" is the default value.
            } else {
                currentCategory = "breakfast";
            }

            fragment = new SubscriptionFragment();
            getFragmentReady(R.id.home_main_frag_subscription, fragment);


            addToCartDBHelper = new AddToCartDBHelper(Home.this);

            tiplayout = (LinearLayout) findViewById(R.id.home_tip_layout);
            tipButton = (Button) findViewById(R.id.home_btn_tip_cancel);
            nestedScrollView = (NestedScrollView) findViewById(R.id.home_main_nestedscrollview);
       /* floatingActionMenu1 = (toan.android.floatingactionmenu.FloatingActionsMenu) findViewById(R.id.home_main_floatingactionmenu1);
        floatingActionMenu1.setIcon(getResources().getDrawable(R.drawable.menu_opt_icon));
        floatingActionMenu2 = (toan.android.floatingactionmenu.FloatingActionsMenu) findViewById(R.id.home_main_floatingactionmenu2);
        floatingActionMenu2.setIcon(getResources().getDrawable(R.drawable.menu_opt_icon));*/

            menu_opt_img = (ImageView) findViewById(R.id.home_main_menu_image);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (!prefs.getBoolean("PRERMISSION_ASKED", false)) {
                permissionDialog();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("PRERMISSION_ASKED", true);
                editor.commit();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, maintoolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View header = navigationView.getHeaderView(0);
            navigationView.setItemIconTintList(null);

            txt_swagatapps= (TextView) findViewById(R.id.home_swagat_txt);
            txt_swagatapps.setOnClickListener(this);

            header_username = (TextView) header.findViewById(R.id.nav_header_username);
            header_phone_number = (TextView) header.findViewById(R.id.nav_header_phone);
            header_signin= (TextView) header.findViewById(R.id.nav_header_signin);
            header_user_setting= (ImageView) header.findViewById(R.id.nav_header_user_setting);
            header_user_pic= (ImageView) header.findViewById(R.id.nav_header_user_pic);


            loginSessionManager = new LoginSessionManager(Home.this);
            isUserLogedIn = loginSessionManager.isLoggedIn();
            if (isUserLogedIn) {
                userDetails = new HashMap<>();
                userDetails = loginSessionManager.getUserDetails();
                String username = userDetails.get("user_name");
                String phone_nm = userDetails.get("phone");

                header_username.setText(username);
                header_phone_number.setText(phone_nm);
                header_signin.setVisibility(View.GONE);
                header_username.setVisibility(View.VISIBLE);
                header_phone_number.setVisibility(View.VISIBLE);
                Menu menu =navigationView.getMenu();
                MenuItem target = menu.findItem(R.id.nav_logout);
                target.setVisible(true);
            } else {
                /*header_username.setText("User Name");
                header_phone_number.setText("Phone Number");*/
                header_signin.setVisibility(View.VISIBLE);
                header_username.setVisibility(View.GONE);
                header_phone_number.setVisibility(View.GONE);
                Menu menu =navigationView.getMenu();
                MenuItem target = menu.findItem(R.id.nav_logout);
                target.setVisible(false);
            }

            header_user_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to user profile
                    if(isUserLogedIn) {
                        startActivity(new Intent(Home.this, UserEditActivity.class));
                    }else {
                        startActivity(new Intent(Home.this,LoginActivity.class));
                    }
                }
            });
            header_user_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isUserLogedIn) {
                        startActivity(new Intent(Home.this, UserEditActivity.class));
                    }else {
                        startActivity(new Intent(Home.this,LoginActivity.class));
                    }
                }
            });


            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*SharedPreferences.Editor editor = getSharedPreferences("RETURN_FROM_LOGIN_PAGE_PREF", MODE_PRIVATE).edit();
                    editor.putString("status", "FromLoginPage");
                    editor.commit();*/
                    if (!isUserLogedIn) {
                        startActivity(new Intent(Home.this, LoginActivity.class));
                    }
                }
            });


            tipButton.setOnClickListener(this);

            menu_opt_img.setOnClickListener(this);


            onTaskStart("breakfast");
        } else {
            nointernet_layout.setVisibility(View.VISIBLE);
        }

        internet_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    private void onTaskStart(String category) {
        if(checkInternetConnection.isConnected()) {
            SharedPreferences.Editor editor = getSharedPreferences("MY_CURRENT_CATEGORY", MODE_PRIVATE).edit();
            if (category.equals("breakfast")) {
                txt_cat.setText("Breakfast");
                url = db.getSingleUrl("BreakFast_List");
                editor.putString("current_category", "breakfast");
            }
            if (category.equals("starter")) {
                txt_cat.setText("Starter");
                url = db.getSingleUrl("Starter_List");
                editor.putString("current_category", "starter");
            }
            if (category.equals("soup")) {
                txt_cat.setText("Soup");
                url = db.getSingleUrl("Soup_List");
                editor.putString("current_category", "soup");
            }
            if (category.equals("sandwich")) {
                txt_cat.setText("Sandwich");
                url = db.getSingleUrl("Sandwich_List");
                editor.putString("current_category", "sandwich");
            }
            if (category.equals("pizza")) {
                txt_cat.setText("Pizza");
                url = db.getSingleUrl("Pizza_List");
                editor.putString("current_category", "pizza");
            }
            if (category.equals("burger")) {
                txt_cat.setText("Burger");
                url = db.getSingleUrl("Burger_List");
                editor.putString("current_category", "burger");
            }
            if (category.equals("salad")) {
                txt_cat.setText("Salad");
                url = db.getSingleUrl("Salad_List");
                editor.putString("current_category", "salad");
            }

            if (category.equals("pasta")) {
                txt_cat.setText("Pasta");
                url = db.getSingleUrl("Pasta_List");
                editor.putString("current_category", "pasta");
            }

            if (category.equals("dessert")) {
                txt_cat.setText("Dessert");
                url = db.getSingleUrl("Desserts_List");
                editor.putString("current_category", "dessert");
            }

            if (category.equals("combo")) {
                txt_cat.setText("Combo");
                url = db.getSingleUrl("Combo_List");
                editor.putString("current_category", "combo");
            }

            if (category.equals("brownrice")) {
                txt_cat.setText("Brown rice");
                url = db.getSingleUrl("BrownRice_List");
                editor.putString("current_category", "brownrice");
            }

            editor.apply();
            populateList(url);
            zoomOutText(txt_cat);
        }else {
            Toast.makeText(getApplicationContext(),"No internet connection!",Toast.LENGTH_LONG).show();
        }
    }


    private void viewDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.custom_dialog_menu_category_list, null);
        TextView dia_breafast, dia_brownrice, dia_burger, dia_combo, dia_dessert, dia_pasta, dia_pizza, dia_salad, dia_sandwich, dia_soup, dia_starter;
        dia_breafast = (TextView) alertLayout.findViewById(R.id.custom_dia_breackfast);
        dia_brownrice = (TextView) alertLayout.findViewById(R.id.custom_dia_brownrice);
        dia_burger = (TextView) alertLayout.findViewById(R.id.custom_dia_burger);
        dia_combo = (TextView) alertLayout.findViewById(R.id.custom_dia_combo);
        dia_dessert = (TextView) alertLayout.findViewById(R.id.custom_dia_dessert);
        dia_pasta = (TextView) alertLayout.findViewById(R.id.custom_dia_pasta);
        dia_salad = (TextView) alertLayout.findViewById(R.id.custom_dia_salad);
        dia_pizza = (TextView) alertLayout.findViewById(R.id.custom_dia_pizza);
        dia_sandwich = (TextView) alertLayout.findViewById(R.id.custom_dia_sandwich);
        dia_soup = (TextView) alertLayout.findViewById(R.id.custom_dia_soup);
        dia_starter = (TextView) alertLayout.findViewById(R.id.custom_dia_starter);

        String current_cat;
        SharedPreferences category_prefs = getSharedPreferences("MY_CURRENT_CATEGORY", MODE_PRIVATE);
        current_cat = category_prefs.getString("current_category", null);
        if (current_cat.equals("breakfast")) {
            dia_breafast.setTextColor(getResources().getColor(R.color.deeporange500));
        }
        if (current_cat.equals("starter")) {
            dia_starter.setTextColor(getResources().getColor(R.color.deeporange500));
        }
        if (current_cat.equals("soup")) {
            dia_soup.setTextColor(getResources().getColor(R.color.deeporange500));
        }
        if (current_cat.equals("sandwich")) {
            dia_sandwich.setTextColor(getResources().getColor(R.color.deeporange500));
        }
        if (current_cat.equals("pizza")) {
            dia_pizza.setTextColor(getResources().getColor(R.color.deeporange500));
        }
        if (current_cat.equals("burger")) {
            dia_burger.setTextColor(getResources().getColor(R.color.deeporange500));
        }
        if (current_cat.equals("salad")) {
            dia_salad.setTextColor(getResources().getColor(R.color.deeporange500));
        }
        if (current_cat.equals("pasta")) {
            dia_pasta.setTextColor(getResources().getColor(R.color.deeporange500));
        }
        if (current_cat.equals("dessert")) {
            dia_dessert.setTextColor(getResources().getColor(R.color.deeporange500));
        }
        if (current_cat.equals("combo")) {
            dia_combo.setTextColor(getResources().getColor(R.color.deeporange500));
        }
        if (current_cat.equals("brownrice")) {
            dia_brownrice.setTextColor(getResources().getColor(R.color.deeporange500));
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // alert.setTitle("Info");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);

        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        dia_breafast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snipetMenuCat("Breakfast");
                onTaskStart("breakfast");
                dialog.dismiss();
                nestedScrollView.scrollTo(0,0);

            }
        });

        dia_starter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snipetMenuCat("Starter");
                onTaskStart("starter");
                dialog.dismiss();
                nestedScrollView.scrollTo(0,0);
            }
        });

        dia_soup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snipetMenuCat("Soup");
                onTaskStart("soup");
                dialog.dismiss();
                nestedScrollView.scrollTo(0,0);
            }
        });

        dia_sandwich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snipetMenuCat("Sandwich");
                onTaskStart("sandwich");
                dialog.dismiss();
                nestedScrollView.scrollTo(0,0);
            }
        });

        dia_pizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snipetMenuCat("Pizza");
                onTaskStart("pizza");
                dialog.dismiss();
                nestedScrollView.scrollTo(0,0);
            }
        });

        dia_burger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snipetMenuCat("Burger");
                onTaskStart("burger");
                dialog.dismiss();
                nestedScrollView.scrollTo(0,0);
            }
        });

        dia_salad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snipetMenuCat("Salad");
                onTaskStart("salad");
                dialog.dismiss();
                nestedScrollView.scrollTo(0,0);
            }
        });

        dia_pasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snipetMenuCat("Pasta");
                onTaskStart("pasta");
                dialog.dismiss();
                nestedScrollView.scrollTo(0,0);
            }
        });

        dia_dessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snipetMenuCat("Dessert");
                onTaskStart("dessert");
                dialog.dismiss();
                nestedScrollView.scrollTo(0,0);
            }
        });

        dia_combo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snipetMenuCat("Combo");
                onTaskStart("combo");
                dialog.dismiss();
                nestedScrollView.scrollTo(0,0);
            }
        });
        dia_brownrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snipetMenuCat("Brown Rice");
                onTaskStart("brownrice");
                dialog.dismiss();
                nestedScrollView.scrollTo(0,0);
            }
        });


        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog.show();
        dialog.getWindow().setLayout(500, 800);
    }

    private void populateList(String url) {
        List<NameValuePair> parameters = null;
        parameters = new ArrayList<NameValuePair>(2);
        BackgroundServiceAsynkTask asynkTask = new BackgroundServiceAsynkTask(Home.this, this, parameters);
        asynkTask.execute(url);
    }



    @Override
    public void onClick(View v) {
        if (v == tipButton) {
            tiplayout.setVisibility(View.GONE);
        }

        if (v == menu_opt_img) {
            //Toast.makeText(getApplicationContext(),"Menu",Toast.LENGTH_LONG).show();
            viewDialog();
        }
        if (v==txt_swagatapps){
            String url = "http://www.domain.com/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        invalidateOptionsMenu();
        SharedPreferences category_prefs = getSharedPreferences("MY_CURRENT_CATEGORY", MODE_PRIVATE);
        String restoredText = category_prefs.getString("current_category", null);
        if (restoredText != null) {
            currentCategory = category_prefs.getString("current_category", "");//"No name defined" is the default value.
            onTaskStart(currentCategory);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        isUserLogedIn = loginSessionManager.isLoggedIn();
        if (isUserLogedIn) {
            userDetails = new HashMap<>();
            userDetails = loginSessionManager.getUserDetails();
            String username = userDetails.get("user_name");
            String phone_nm = userDetails.get("phone");

            header_username.setText(username);
            header_phone_number.setText(phone_nm);
            header_signin.setVisibility(View.GONE);
            header_username.setVisibility(View.VISIBLE);
            header_phone_number.setVisibility(View.VISIBLE);
            Menu menu =navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.nav_logout);
            target.setVisible(true);
        } else {
           /* header_username.setText("User Name");
            header_phone_number.setText("Phone Number");*/
            header_signin.setVisibility(View.VISIBLE);
            header_username.setVisibility(View.GONE);
            header_phone_number.setVisibility(View.GONE);
            Menu menu =navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.nav_logout);
            target.setVisible(false);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);

            this.finish();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        }
    }

    private MenuItem action_logout,action_cart;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        action_cart=menu.findItem(R.id.action_cart);
        MenuItemCompat.setActionView(action_cart, R.layout.cart_badge_layout);
        notifCount = (RelativeLayout)   MenuItemCompat.getActionView(action_cart);
        cart_badge = (TextView) notifCount.findViewById(R.id.actionbar_cart_badge_textview);

        String itemcount = addToCartDBHelper.getTotalNumberOfItemCount();
        if (itemcount.equals("0")) {
            cart_badge.setVisibility(View.GONE);
        } else {
            cart_badge.setVisibility(View.VISIBLE);
            cart_badge.setText(String.valueOf(itemcount));
        }

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            navigationView.getMenu().findItem(R.id.nav_cart).setChecked(false);
            startActivity(new Intent(Home.this, CartActivity.class));

            final LinearLayout ll = (LinearLayout) findViewById(R.id.home_main);
            Animation a = AnimationUtils.loadAnimation(
                    Home.this, android.R.anim.fade_out);
            a.setDuration(700);
            a.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }
            });
            ll.startAnimation(a);

            overridePendingTransition(R.anim.layout_slide_up, R.anim.stay);


        }
        if (id == R.id.nav_previous_order) {

            if (isUserLogedIn) {
                navigationView.getMenu().findItem(R.id.nav_previous_order).setChecked(false);
                startActivity(new Intent(Home.this, PreviousOrderActivity.class));

                final LinearLayout ll = (LinearLayout) findViewById(R.id.home_main);
                Animation a = AnimationUtils.loadAnimation(
                        Home.this, android.R.anim.fade_out);
                a.setDuration(700);
                a.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationEnd(Animation animation) {
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationStart(Animation animation) {
                    }
                });
                ll.startAnimation(a);

                overridePendingTransition(R.anim.layout_slide_up, R.anim.stay);
            } else {
                startActivity(new Intent(Home.this, LoginActivity.class));
            }
        }
        if (id == R.id.nav_contact_us) {
            startActivity(new Intent(Home.this, ContactUsActivity.class));
        }
        if(id==R.id.nav_logout){
            loginSessionManager.logoutUser();
            startActivity(new Intent(Home.this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    private void permissionDialog() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(Home.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(Home.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    2);
        }
    }

    private void getFragmentReady(int container, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(container, fragment).commit();
    }



    private void zoomOutText(View textview) {
        Animation zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        textview.startAnimation(zoomOutAnimation);
    }

    @Override
    public void onTaskComplete(String result) {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

        if (result != null && !result.equals("") && !result.equals("null")) {
            String responseString = result.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
            responseString = responseString.replace("<string xmlns=\"http://tempuri.org/\">", "");
            responseString = responseString.replace("</string>", "");
            List<MainItemListData> data = new ArrayList<>();

            try {
                JSONObject jsonObj = new JSONObject(responseString);

                JSONArray jArray = jsonObj.getJSONArray("Table");

                /*for (int i = jArray.length() - 1; i < jArray.length(); i--) {*/
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    MainItemListData mainListItemData = new MainItemListData();
                    mainListItemData.dishId = json_data.getString("Dish_ID");
                    mainListItemData.dishName = json_data.getString("Dish_Name");

                    mainListItemData.bloodgroups = json_data.getString("BloodGroup");
                    mainListItemData.dishImage = json_data.getString("Image_Url");
                    mainListItemData.dishDescp = json_data.getString("Description");
                    mainListItemData.dishIngredients = json_data.getString("Ingredients");

                    mainListItemData.dishPrice = json_data.getString("Price");
                    mainListItemData.dishRating = json_data.getString("Rating");
                    mainListItemData.vegNonVeg = json_data.getString("VegNonVeg");
                    mainListItemData.dishOriginalPrice = json_data.getString("Orginal_price");
                    data.add(mainListItemData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



            adapter = new MainItemListAdapter(Home.this, data, Home.this);
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(Home.this, R.anim.layout_animation_from_bottom);
            recyclerView.setLayoutAnimation(controller);
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Home.this);
            recyclerView.setLayoutManager(new LinearLayoutManager(Home.this, LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }

                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            //recyclerView.setLayoutManager(linearLayoutManager);
            //ViewCompat.setNestedScrollingEnabled(recyclerView, false);
            recyclerView.setHasFixedSize(true);
            //recyclerView.setNestedScrollingEnabled(false);
            recyclerView.scheduleLayoutAnimation();
            recyclerView.setAdapter(adapter);
        }
    }

    public void checkCartMenu() {
        Home.this.invalidateOptionsMenu();
    }

    public void hideLoading() {
        loading_layout.setVisibility(View.GONE);
    }

    public void onCartOptClick(View v){

        navigationView.getMenu().findItem(R.id.nav_cart).setChecked(false);
        startActivity(new Intent(Home.this, CartActivity.class));

        final LinearLayout ll = (LinearLayout) findViewById(R.id.home_main);
        Animation a = AnimationUtils.loadAnimation(
                Home.this, android.R.anim.fade_out);
        a.setDuration(700);
        a.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
        ll.startAnimation(a);

        overridePendingTransition(R.anim.layout_slide_up, R.anim.stay);
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + Home.this.getPackageName() + "&hl=it")
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            String currentVersion = BuildConfig.VERSION_NAME;
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    showUpadteDialog();
                }
            }
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);

        }
    }

    private void showUpadteDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("A new version of My Restaurant has came with better features, Update the app.");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String url = "https://play.google.com/store/appname";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
