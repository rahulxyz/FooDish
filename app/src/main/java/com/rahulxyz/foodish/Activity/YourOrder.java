package com.rahulxyz.foodish.Activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rahulxyz.foodish.AppWidget;
import com.rahulxyz.foodish.R;
import com.rahulxyz.foodish.Utils.FireUtils;
import com.rahulxyz.foodish.Utils.FoodItem;
import com.rahulxyz.foodish.Utils.ListAdapter;
import com.rahulxyz.foodish.Utils.Menu;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YourOrder extends AppCompatActivity {


    public static final String LIST_POSITION = "list_position";
    static String NAME = "Name";
    static String PHONE = "PhoneNumber";
    static String ADDRESS = "Location";
    static String BILL = "Bill";
    public LinearLayoutManager mListLayout;
    @BindView(R.id.recycler_list)
    RecyclerView recycler_list;
    ListAdapter listAdapter;
    @BindView(R.id.totalAmount)
    TextView totalAmount;
    @BindView(R.id.toolbar_yourOrder)
    Toolbar toolbar;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    private Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_order);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        mDatabase = FireUtils.getDatabase();
        mReference = mDatabase.getReference(getString(R.string.FirebaseOrdersRef));

        mListLayout = new LinearLayoutManager(this);
        recycler_list.setLayoutManager(mListLayout);
        listAdapter = new ListAdapter(this, Menu.currentOrder);
        recycler_list.setAdapter(listAdapter);

        totalAmount.setText(getFinalBill());
    }


    public String getFinalBill() {
        Integer price, quantity, sum = 0;
        for (FoodItem foodItem : Menu.currentOrder) {
            price = foodItem.getPrice();
            quantity = foodItem.getQuantity();
            sum += (price * quantity);
        }
        String s = getString(R.string.indianCurrency) + " " + sum.toString();
        return s;
    }


    public HashMap<String, String> getData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name, phoneNum, location;
        name = preferences.getString(getString(R.string.userName), null);
        phoneNum = preferences.getString(getString(R.string.phoneNumber), null);
        location = preferences.getString(getString(R.string.location), null);

        HashMap<String, String> map = null;
        TextView bill = (TextView) findViewById(R.id.totalAmount);

        if (name == null || phoneNum == null || location == null || name.isEmpty() || phoneNum.isEmpty() | location.isEmpty()) {
            Toast.makeText(this, R.string.fillDetailsMessage, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
        } else {
            map = new HashMap<>();
            map.put(NAME, name);
            map.put(ADDRESS, location);
            map.put(PHONE, phoneNum);
            for (FoodItem item : Menu.currentOrder) {
                map.put(item.getName(), item.getQuantity().toString());
            }
            map.put(BILL, bill.getText().toString());
        }
        return map;
    }

    public void placeOrder(View v) {
        //get userInfo
        HashMap<String, String> map = getData();
        if (map != null) {
            //constuct all data
            String phoneNumKey = map.get(PHONE);
            map.remove(PHONE);
            mReference.child(phoneNumKey).push().setValue(map);

            ///once order is placed call MainActivity
            MainActivity.isMultiSelect = false;
            //------clear Cuurent order and store it as last order
            Menu.lastOrder = Menu.currentOrder;
            Menu.currentOrder = null;
            updateWidget();
            Toast.makeText(this, R.string.orderPlacedMessage, Toast.LENGTH_SHORT).show();
            MainActivity.DATA_FETCH_TOGGLE = false;
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        AppWidget widget = new AppWidget();
        ComponentName componentName = new ComponentName(this, AppWidget.class);
        int[] widgetId = AppWidgetManager.getInstance(this).getAppWidgetIds(componentName);
        widget.onUpdate(this, appWidgetManager, widgetId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = mListLayout.onSaveInstanceState();
        outState.putParcelable(LIST_POSITION, mListState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        mListState = inState.getParcelable(LIST_POSITION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null)
            mListLayout.onRestoreInstanceState(mListState);
    }
}
