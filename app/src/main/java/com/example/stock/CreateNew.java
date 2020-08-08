package com.example.stock;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static com.example.stock.MainActivity.items;



public class CreateNew extends AppCompatActivity implements Dialog.DialogListener {
    DatabaseHelper mDatabaseHelper;
    EditText etName, etPrice, etStock, etCategory, etDescription;
    Button btnViewAll;
    TextView tvPromoStatus;
    CheckBox enablePromo;
    String newCategory;
    String promoDescription;
    private boolean enablePromoForAll = false;
    private String promoCount = "-1";
    private String promoPrice = "-1";
    private String promoDiscount = "-1";

    private static final String TAG = "CreateNew";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new);

        mDatabaseHelper = new DatabaseHelper(this);

        Spinner spinner = findViewById(R.id.spinnerCategory);
        Button goMain = findViewById(R.id.btnCancel);
        Button btnSave = findViewById(R.id.btnCreate);
        btnViewAll = findViewById(R.id.btnViewAll);
        tvPromoStatus = findViewById(R.id.tvPromoStatus);

        //EditText
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);
        etCategory = findViewById(R.id.etCategory);
        etDescription = findViewById(R.id.etDescription);

        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        etPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        etStock.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        etCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        etDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //Auto-setup promo
        enablePromo = findViewById(R.id.isPromo);
        

        enablePromo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (enablePromo.isChecked() && tvPromoStatus.getText().toString() != ""){
                    return;
                }
                else if (enablePromo.isChecked()){
                    showMessage3();
                }
                else{
                    cancelEverything("-1","-1","-1");
                    tvPromoStatus.setText("");
                }

            }
        });

        viewAll();


        //initialize spinner
        final ArrayList<String> categoryArray = new ArrayList<>();
        categoryArray.add("");

        Cursor categoryData = mDatabaseHelper.getDistinctItemCategory();
        while(categoryData.moveToNext()){
            categoryArray.add(categoryData.getString(0));
        }
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,categoryArray);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etCategory.setText(categoryArray.get(position));
                Cursor data = mDatabaseHelper.getPromoForAllStatus(categoryArray.get(position));

                loop:while(data.moveToNext()){
                    if(data.getString(1).contains("for all")){
                        Log.d(TAG, "onItemSelected: " + data.getString(0));

                        tvPromoStatus.setText(data.getString(1));
                        promoCount = data.getString(2);
                        promoPrice = data.getString(3);
                        promoDiscount = data.getString(4);
                        enablePromoForAll = true;
                        enablePromo.setChecked(true);

                        break loop;
                    }
                    else{
                        Log.d(TAG, "else: " + data.getString(0));
                        cancelEverything("-1","-1","-1");
                        enablePromoForAll = false;
                        enablePromo.setChecked(false);
                    }
                }
                data.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        goMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.close();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finishAffinity();

            }
        });

        Log.d(TAG, "createnew page created");
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checkDuplicate = false;

                newCategory = etCategory.getText().toString();
                String newName = etName.getText().toString();
                String newPrice = etPrice.getText().toString();
                String newStock = etStock.getText().toString();
                String newDescription = etDescription.getText().toString();
                promoDescription = tvPromoStatus.getText().toString();
                boolean isPromoStatus = enablePromo.isChecked();
                Log.d(TAG, "btn save clicked");

                //check name and category if an item has the same name and category within the item list.
                for(Item i: items){
                    if (newName.equalsIgnoreCase(i.getName()) && newCategory.equalsIgnoreCase(i.getCategory())){
                        checkDuplicate = true;
                    }
                }
                for(String s: categoryArray){
                    if(s.trim().equalsIgnoreCase(newCategory)){
                        newCategory = s;
                    }
                }
                Log.d(TAG, "checkduplicate complete");

                //check if an duplicated is true and no blank. If the data is inserted, show a message dialog if the user wants to add more
                if (checkDuplicate){
                    Toast.makeText(CreateNew.this, "Item exists", Toast.LENGTH_SHORT).show();
                }
                else if(!newName.trim().isEmpty() && !newStock.trim().isEmpty() && !newPrice.trim().isEmpty()){
                    if(enablePromo.isChecked() && promoCount == "-1" && promoPrice == "-1" && promoDiscount == "-1"){
                        isPromoStatus = false;
                    }

                    boolean isInserted = mDatabaseHelper.insertData(newCategory, newName, newPrice, newStock, isPromoStatus, newDescription, promoDescription,
                            promoCount, promoPrice, promoDiscount);
                    if(enablePromo.isChecked() && enablePromoForAll){
                        Cursor data = mDatabaseHelper.getItemIDByCategory(newCategory);
                        while (data.moveToNext()) {
                            mDatabaseHelper.updatePromo(data.getInt(0), true, promoDescription, promoCount, promoPrice, promoDiscount);
                            Log.d(TAG, "applyDiscountPromo: " + data.getInt(0) +" " + promoCount + " " + promoPrice + "  " + promoDiscount);
                        }
                        mDatabaseHelper.setPromoForAll(newCategory, true);

                    }
                    if(isInserted = true){
                        items.add(new Item(newCategory, newName, Integer.parseInt(newPrice), Integer.parseInt(newStock)));
                        showMessage2();
                    }
                    else{
                        Toast.makeText(CreateNew.this, "Data NOT Inserted", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(CreateNew.this, "No blank is allowed", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public void viewAll(){
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor data = mDatabaseHelper.getAllData();
                if(data.getCount() == 0){
                    Log.d(TAG, "onClick: ");
                    showMessage("Error", "No data found");
                    return;
                }

                StringBuffer buffer = new StringBuffer();

                while (data.moveToNext()){
                    buffer.append("Category: "+ data.getString(4) + "\n");
                    buffer.append("Name: "+ data.getString(1) + "\n");
                    buffer.append("Price: "+ data.getString(2) + "\n");
                    buffer.append("Stock: "+ data.getString(3) + "\n\n");
                }

                showMessage("Current Stock", buffer.toString());

            }
        });
    }
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void showMessage2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Saved");
        builder.setCancelable(false);
        builder.setMessage("Do you want to add more?");
        builder.setNegativeButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabaseHelper.close();
                startActivity(getIntent());
                finish();
            }
        });
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabaseHelper.close();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    public void showMessage3(){
        Dialog dialog = new Dialog();
        dialog.show(getSupportFragmentManager(),"dialog");
    }

    @Override
    public void applyPricePromo(String pCount, String pPrice, String pDiscount, boolean promoForAll) {
        if (promoForAll){
            enablePromoForAll = true;
            tvPromoStatus.setText(String.format("Buy %s make item $%s\nPromotion for all category enabled",pCount,pPrice));
        }
        else{
            tvPromoStatus.setText(String.format("Buy %s make item $%s",pCount,pPrice));
        }
        promoCount = pCount;
        promoPrice = pPrice;
        promoDiscount = pDiscount;
    }

    @Override
    public void applyDiscountPromo(String pCount, String pPrice, String pDiscount, boolean promoForAll) {
            if (promoForAll) {
                enablePromoForAll = true;
                tvPromoStatus.setText(String.format("Buy %s discount $%s for each item\nPromotion for all category enabled", pCount, pDiscount));
            }
            else {
                tvPromoStatus.setText(String.format("Buy %s discount $%s for each item", pCount, pDiscount));
            }
            promoCount = pCount;
            promoPrice = pPrice;
            promoDiscount = pDiscount;

    }

    @Override
    public void cancelEverything(String pCount, String pPrice, String pDiscount) {
        promoCount = pCount;
        promoPrice = pPrice;
        promoDiscount = pDiscount;
        enablePromo.setChecked(false);
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

