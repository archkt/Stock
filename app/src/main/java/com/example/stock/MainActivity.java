package com.example.stock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity{

    DatabaseHelper myDB;
    private static final String TAG = "MainActivity";
    private ListView lvStock;
    public static ArrayList<Item> filtered_items;
    public static ArrayList<Item> items;
    public static int todayTotal;
    ActionBarDrawerToggle toggle;
    static String startingDate;

    private void setAdapterWithListener(ListView lv, final ArrayList<Item> list){
        ItemListAdapter adapter = new ItemListAdapter(getApplicationContext(), R.layout.adapter_view_layout, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), EditItem.class);
                intent.putExtra("CurrentCategory", list.get(position).getCategory());
                intent.putExtra("CurrentName", list.get(position).getName());
                intent.putExtra("CurrentPrice", list.get(position).getPrice());
                intent.putExtra("CurrentStock", list.get(position).getStock());
                intent.putExtra("CurrentID", list.get(position).getID());
                intent.putExtra("CurrentPromo", list.get(position).isPromo());
                intent.putExtra("CurrentDescription", list.get(position).getDescription());
                intent.putExtra("CurrentPromoDescription", list.get(position).getPromoDescription());
                intent.putExtra("CurrentPromoDetail", list.get(position).getPromoDetail());
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting");

        Button btnTotal = findViewById(R.id.btnTotal1);
        Button btnModify = findViewById(R.id.btnModify);
        ImageButton btnRefresh = findViewById(R.id.refresh);
        lvStock =  findViewById(R.id.listView);
        EditText etSearch = findViewById(R.id.search_filter);
        NavigationView navView = findViewById(R.id.navView);
        final Switch switch_show = findViewById(R.id.swNoShowZeroStock);

        items = new ArrayList<>();
        filtered_items = new ArrayList<>();
        myDB = new DatabaseHelper(this);
        Cursor data = myDB.getAllData();


        //Initialize date and Button btnTotal
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Cursor allDateData = myDB.getAllDateData();


        //if there is no data --> create new report row
        if(allDateData.getCount() == 0){
            Log.d(TAG, "no report data was found. So created new one with: ");
            myDB.insertReport(df.format(date));
            startingDate = df.format(date);
        }
        Cursor getUnsettledDateData = myDB.getUnsettledDateData();
        if(getUnsettledDateData.getCount() != 0){
            while(getUnsettledDateData.moveToNext()){
                startingDate = getUnsettledDateData.getString(0);
                todayTotal = getUnsettledDateData.getInt(2);
            }
        }
        else{
            startingDate = df.format(date);
            myDB.insertReport(startingDate);
        }

            //todayTotal = myDB.getUnsettledDateData().getInt(2);


        Log.d(TAG, "daily total on btnTotal text: " + todayTotal);
        btnTotal.setText(String.format("Today's total $%s", todayTotal));

        //Initialize from the database and set adapter
        if (data.getCount() == 0){
            Toast.makeText(getApplicationContext(), "The database is empty", Toast.LENGTH_SHORT).show();
        }
        else{
            while(data.moveToNext()){
                Item item = new Item(data.getString(4),data.getString(1),data.getInt(2),data.getInt(3));
                boolean promo;
                if(data.getString(5).equals("Y") || data.getString(5).equals("Yall")){
                    promo = true;
                }
                else{
                    promo = false;
                }

                int promoStock = Integer.parseInt(data.getString(8));
                int promoPrice = Integer.parseInt(data.getString(9));
                int promoDiscount = Integer.parseInt(data.getString(10));
                int [] promoDetail = {promoStock,promoPrice,promoDiscount};

                item.setPromoDetail(promoDetail);
                item.setName(data.getString(1));
                item.setPrice(data.getInt(2));
                item.setStock(data.getInt(3));
                item.setID(data.getInt(0));
                item.setPromo(promo);
                item.setPromoDescription(data.getString(7));
                item.setDescription(data.getString(6));

                items.add(item);
                if(item.getStock() != 0){
                    filtered_items.add(item);}
            }
        }

         for(Item i: items){
            Log.d(TAG, "item "+ i + " is Promo? : " + i.isPromo() + "   getPromoDescription : " + i.getPromoDescription()
                    + "    getPromoDetail :" + i.getPromoDetail()[0] + " " + i.getPromoDetail()[1] + " " +i.getPromoDetail()[2]
                    + "");
        }
        setAdapterWithListener(lvStock,items);

        final ArrayList <Item> sorted_items = new ArrayList<>(items);
        final ArrayList <Item> sorted_filtered_items = new ArrayList<>(filtered_items);



        //Set spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinnerSort);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //set switch
                switch_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            Log.d(TAG, "onCheckedChanged: changed");

                            setAdapterWithListener(lvStock,sorted_filtered_items);
                        }
                        if(!isChecked){
                            setAdapterWithListener(lvStock,sorted_items);
                        }
                    }
                });
                if(position == 0){
                    if(switch_show.isChecked())
                        Collections.sort(sorted_filtered_items, new NameComparator());
                        setAdapterWithListener(lvStock, filtered_items);
                    if(!switch_show.isChecked())
                        Collections.sort(sorted_items, new NameComparator());
                        setAdapterWithListener(lvStock, items);
                }
                if(position == 1){
                    if(switch_show.isChecked()){
                        Collections.sort(sorted_filtered_items, new PriceComparator());
                        setAdapterWithListener(lvStock, sorted_filtered_items);}
                    if(!switch_show.isChecked()){
                        Collections.sort(sorted_items, new PriceComparator());
                        setAdapterWithListener(lvStock, sorted_items); }
                        }
                if(position == 2){
                    if(switch_show.isChecked()){
                        Collections.sort(sorted_filtered_items, new StockComparator());
                        setAdapterWithListener(lvStock, sorted_filtered_items); }
                    if(!switch_show.isChecked()){
                        Collections.sort(sorted_items, new StockComparator());
                        setAdapterWithListener(lvStock, sorted_items); }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettleMessage();

            }
        });


        //Button Modify
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnModify clicked");

                Intent toModify = new Intent(MainActivity.this, ModifyItem.class);
                startActivity(toModify);
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                startActivity(refresh);
                finishAffinity();
                myDB.close();
            }
        });

        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //EditText etSearch: Search filter and set the new adapter with temp ArrayList
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Item> temp = new ArrayList<>();
                if(switch_show.isChecked()){
                    for (Item i : filtered_items){
                        if(i.getName().toLowerCase().contains(s) || i.getCategory().toLowerCase().contains(s)){
                            temp.add(i);
                        }
                    }
                    Log.d(TAG, "temp: " + temp);
                }
                else{
                    for (Item i : items){
                        if(i.getName().toLowerCase().contains(s) || i.getCategory().toLowerCase().contains(s)){
                            temp.add(i);
                        }
                    }
                    Log.d(TAG, "temp: " + temp);
                }
                setAdapterWithListener(lvStock,temp);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    public void showSettleMessage(){
        Log.d(TAG, "btnTotal clicked");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        builder.setCancelable(true);
        builder.setTitle("Warning!");
        builder.setMessage("Do you want to reset?");
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Date currentDate = Calendar.getInstance().getTime();
                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String endingDate = format.format(currentDate);

                myDB.settleTotal(startingDate, endingDate, todayTotal);
                myDB.insertReport(endingDate);
                Intent resetMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(resetMain);
                finishAffinity();
            }
        });
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();

            }
        });
        builder.show();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}