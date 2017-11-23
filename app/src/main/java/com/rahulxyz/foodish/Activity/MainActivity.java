package com.rahulxyz.foodish.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rahulxyz.foodish.R;
import com.rahulxyz.foodish.Utils.FireUtils;
import com.rahulxyz.foodish.Utils.FoodItem;
import com.rahulxyz.foodish.Utils.GridAdapter;
import com.rahulxyz.foodish.Utils.Menu;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GridAdapter.GridItemClickListener {

    public static final String GRID_POSITION = "grid_position";
    public static boolean isMultiSelect = false;
    public static String DATA_FETCHED = "DataAlreadyFetched";
    public static boolean DATA_FETCH_TOGGLE = false;
    static String MENU = "Menu";
    static String TAG = "FooDish";
    public GridLayoutManager mGridLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    GridAdapter gridAdapter;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @BindView(R.id.errorMessage)
    TextView errorMessage;
    private FirebaseDatabase mDatabase;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Parcelable mGridState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        if (isNetworkAvailable())
            showGrid();
        else
            showError();


        mDatabase = FireUtils.getDatabase();
        mDatabaseRef = mDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mGridLayout = new GridLayoutManager(this,
                getResources().getInteger(R.integer.minGridColumn));
        recyclerView.setLayoutManager(mGridLayout);

        if (savedInstanceState != null) {
            DATA_FETCH_TOGGLE = savedInstanceState.getBoolean(DATA_FETCHED);
        }

        if (!DATA_FETCH_TOGGLE)
            fetchData();
        else {
            gridAdapter = new GridAdapter(MainActivity.this, Menu.currentMenu, mStorageRef, MainActivity.this);
            recyclerView.setAdapter(gridAdapter);
        }
    }

    public void showGrid() {
        recyclerView.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }

    public void showError() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void fetchData() {
        if (!DATA_FETCH_TOGGLE) {
            DATA_FETCH_TOGGLE = true;
        }

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Menu.currentMenu = new ArrayList<>();
                new ParseMenu().execute(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    @Override
    public void onCardClick(View root, int index) {
        String quantity = Menu.currentMenu.get(index).getQuantity().toString();
        if (!isMultiSelect) {
            int val = Integer.parseInt(quantity);
            val++;
            Menu.currentMenu.get(index).setQuantity(val);
        } else {
            //start changing layout and add to order list
            if (quantity.equals("0"))
                Snackbar.make(coordinatorLayout, R.string.zeroQuantity, Snackbar.LENGTH_SHORT).show();
            else if (!Menu.currentMenu.get(index).getScrimToggle()) {
                addOrderItem(root, index);
                Menu.currentMenu.get(index).setScrimToggle(true);
            } else {
                Menu.currentMenu.get(index).setScrimToggle(false);

                if (Menu.currentOrder.isEmpty()) {
                    isMultiSelect = false;
                }
            }
        }

        gridAdapter.notifyItemChanged(index);
    }

    @Override
    public void onLongCardClick(View root, int index) {
        View scrim = root.findViewById(R.id.scrim);
        TextView quantity = root.findViewById(R.id.quantity);
        if (quantity.getText().toString().equals(getString(R.string.initialQuantity)))
            Snackbar.make(coordinatorLayout, R.string.zeroQuantity, Snackbar.LENGTH_SHORT).show();
        else if (!isMultiSelect) {
            isMultiSelect = true;
            Menu.currentMenu.get(index).setScrimToggle(true);
            Menu.currentOrder = new ArrayList<>();
            addOrderItem(root, index);
            scrim.setVisibility(View.VISIBLE);
        }

    }

    public void addOrderItem(View root, int index) {
        TextView quantity = root.findViewById(R.id.quantity);
        FoodItem newItem = Menu.currentMenu.get(index);
        newItem.setQuantity(Integer.parseInt(quantity.getText().toString()));
        Menu.currentOrder.add(newItem);
    }

    public void fabClick(View v) {
        if (Menu.currentOrder == null || Menu.currentOrder.isEmpty()) {
            Snackbar.make(coordinatorLayout, R.string.atleastOneItem, Snackbar.LENGTH_SHORT).show();
        } else {
            //start intent
            Intent intent = new Intent(this, YourOrder.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DATA_FETCHED, DATA_FETCH_TOGGLE);
        mGridState = mGridLayout.onSaveInstanceState();
        outState.putParcelable(GRID_POSITION, mGridState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        mGridState = inState.getParcelable(GRID_POSITION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGridState != null)
            mGridLayout.onRestoreInstanceState(mGridState);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                //make all Quantitiy 0
                refreshData();
                return true;
            case R.id.action_setting:
                Intent intent = new Intent(this, Setting.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshData() {
        for (FoodItem foodItem : Menu.currentMenu) {
            foodItem.setQuantity(0);
            foodItem.setScrimToggle(false);
        }

        gridAdapter.notifyDataSetChanged();
        isMultiSelect = false;
    }

    class ParseMenu extends AsyncTask<DataSnapshot, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //start progressbar
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(DataSnapshot... param) {
            DataSnapshot dataSnapshot = param[0];
            for (DataSnapshot eachItem : dataSnapshot.child(MENU).getChildren()) {
                FoodItem value = eachItem.getValue(FoodItem.class);
                value.setQuantity(Integer.parseInt(getString(R.string.initialQuantity)));
                Menu.currentMenu.add(value);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            gridAdapter = new GridAdapter(MainActivity.this, Menu.currentMenu, mStorageRef, MainActivity.this);
            recyclerView.setAdapter(gridAdapter);
        }
    }

}
