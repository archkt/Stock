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


public class EditItem extends AppCompatActivity implements Dialog.DialogListener{
    DatabaseHelper mDatabaseHelper;
    EditText etEditName, etEditPrice, etEditStock, etEditCategory, etEditDescription;
    TextView tvPromo;
    CheckBox isPromo;
    private static final String TAG = "EditItem";
    private String promoCount,promoPrice,promoDiscount,promoDescription;
    private boolean enablePromoForAll = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item);


        //Buttons, spinner and checkbox
        Button btnViewAll = findViewById(R.id.btnEditViewAll);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnCancel = findViewById(R.id.btnEditCancel);
        Spinner spinner = findViewById(R.id.spinnerEditCategory);
        isPromo = findViewById(R.id.isPromo3);

        //Values from previous intent(MainActivity)
        final String initialCategory = getIntent().getStringExtra("CurrentCategory");
        String initialName = getIntent().getStringExtra("CurrentName");
        int initialPrice = getIntent().getIntExtra("CurrentPrice", 0);
        int initialStock = getIntent().getIntExtra("CurrentStock", 0);
        boolean initialPromo = getIntent().getBooleanExtra("CurrentPromo",false);
        final int currentID = getIntent().getIntExtra("CurrentID", 0);
        String initialDescription = getIntent().getStringExtra("CurrentDescription");
        String initialPromoDescription = getIntent().getStringExtra("CurrentPromoDescription");
        final int [] initialPromoDetail = getIntent().getIntArrayExtra("CurrentPromoDetail");


        promoCount = Integer.toString(initialPromoDetail[0]);
        promoPrice = Integer.toString(initialPromoDetail[1]);
        promoDiscount = Integer.toString(initialPromoDetail[2]);

        //EditText and TextView
        etEditCategory = findViewById(R.id.etEditCategory);
        etEditName = findViewById(R.id.etEditName);
        etEditPrice = findViewById(R.id.etEditPrice);
        etEditStock = findViewById(R.id.etEditStock);
        etEditDescription = findViewById(R.id.etDescription3);
        tvPromo = findViewById(R.id.tvPromo);

        etEditCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        etEditName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        etEditPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        etEditStock.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        etEditDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //Set text in EditText and setCheck in checkbox
        etEditCategory.setText(initialCategory);
        etEditName.setText(initialName);
        etEditPrice.setText(Integer.toString(initialPrice));
        etEditStock.setText(Integer.toString(initialStock));
        etEditDescription.setText(initialDescription);
        tvPromo.setText(initialPromoDescription);
        isPromo.setChecked(initialPromo);
        isPromo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isPromo.isChecked() && tvPromo.getText().toString() != ""){
                    return;
                }
                else if (isPromo.isChecked()){
                    showMessage3();
                }
                else{
                    cancelEverything("-1", "-1","-1");
                    tvPromo.setText("");
                }
            }
        });

        //call database
        mDatabaseHelper = new DatabaseHelper(this);

        //create category ArrayList to attach on adapter
        final ArrayList<String> categoryArray = new ArrayList<>();
        categoryArray.add("Current Category");
        Cursor categoryData = mDatabaseHelper.getDistinctItemCategory();
        while(categoryData.moveToNext()){
            categoryArray.add(categoryData.getString(0));
        }

        //create adapter
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,categoryArray);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor data = mDatabaseHelper.getPromoForAllStatus(categoryArray.get(position));
                if (position == 0){
                    etEditCategory.setText(initialCategory);
                }
                else{
                    etEditCategory.setText(categoryArray.get(position));
                    while(data.moveToNext()){
                        if(data.getString(0).equalsIgnoreCase("Yall")){

                            tvPromo.setText(data.getString(1));
                            promoCount = data.getString(2);
                            promoPrice = data.getString(3);
                            promoDiscount = data.getString(4);
                            enablePromoForAll = true;
                            isPromo.setChecked(true);
                            break;
                        }
                    }
                }
                data.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //check if not duplicate nor empty, update the item with given information
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkDuplicate = false;
                String newCategory = etEditCategory.getText().toString();
                String newName = etEditName.getText().toString();
                String newPrice = etEditPrice.getText().toString();
                String newStock = etEditStock.getText().toString();
                String newDescription = etEditDescription.getText().toString();
                promoDescription = tvPromo.getText().toString();
                boolean isPromoStatus = isPromo.isChecked();
                Log.d(TAG, "isPromoStatus initialize " + isPromoStatus);

                for(Item i: items){
                    if (newName.equalsIgnoreCase(i.getName()) && newCategory.equalsIgnoreCase(i.getCategory()) && i.getID() != currentID){
                        checkDuplicate = true;
                    }
                }

                if(checkDuplicate){
                    Toast.makeText(getApplicationContext(), "Item exists", Toast.LENGTH_SHORT).show();
                }

                else if(!newName.trim().isEmpty() && !newStock.trim().isEmpty() && !newPrice.trim().isEmpty()){

                    if(isPromo.isChecked() && promoCount.equals("-1") && promoPrice.equals("-1") && promoDiscount.equals("-1")){
                        isPromoStatus = false;
                    }


                    if(isPromoStatus && enablePromoForAll){
                        Cursor data = mDatabaseHelper.getItemIDByCategory(newCategory);
                        while (data.moveToNext()) {
                            mDatabaseHelper.updatePromo(data.getInt(0), true, promoDescription, promoCount, promoPrice, promoDiscount);
                            Log.d(TAG, "applyDiscountPromo: " + data.getInt(0) +" " + promoCount + " " + promoPrice + "  " + promoDiscount);
                        }
                        mDatabaseHelper.setPromoForAll(newCategory, true);
                    }
                    mDatabaseHelper.updateCategory(currentID, newCategory);
                    mDatabaseHelper.updateName(currentID, newName);
                    mDatabaseHelper.updatePrice(currentID, Integer.parseInt(newPrice));
                    mDatabaseHelper.updateStock(currentID, Integer.parseInt(newStock));
                    mDatabaseHelper.updatePromo(currentID, isPromoStatus, promoDescription, promoCount, promoPrice, promoDiscount);
                    mDatabaseHelper.updateDescription(currentID, newDescription);


                    Toast.makeText(getApplicationContext(), "Item updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    mDatabaseHelper.close();
                    startActivity(intent);
                    finishAffinity();
                }


            }
        });

        //show all current stock
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

                for (Item i : items){
                    buffer.append("Category: "+ i.getCategory() + "\n");
                    buffer.append("Name: "+ i.getName() + "\n");
                    buffer.append("Price: "+ i.getPrice() + "\n");
                    buffer.append("Stock: "+ i.getStock() + "\n\n");
                }


                showMessage("Current Stock", buffer.toString());

            }
        });

        //cancel and go back to MainActivity
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        //delete button OnClickListener
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteMessage(currentID);
            }
        });
    }

    //show AlertDialog message when btnViewAll pressed
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    //show AlertDialog message, and delete the current item from the database by pressing yes.
    public void showDeleteMessage(final int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Warning!");
        builder.setMessage("Are you sure to delete current item?");
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabaseHelper.close();
                startActivity(getIntent());
                finishAffinity();
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Item deleted.",Toast.LENGTH_SHORT).show();
                mDatabaseHelper.deleteItem(id);
                mDatabaseHelper.close();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finishAffinity();
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
            enablePromoForAll = promoForAll;
            promoDescription = String.format("Buy %s make item $%s\nPromotion for all category enabled",pCount,pPrice);
            tvPromo.setText(promoDescription);
        }
        else{
            tvPromo.setText(String.format("Buy %s make item $%s",pCount,pPrice));
        }
        promoCount = pCount;
        promoPrice = pPrice;
        promoDiscount = pDiscount;
    }

    @Override
    public void applyDiscountPromo(String pCount, String pPrice, String pDiscount, boolean promoForAll) {
        if (promoForAll){
            enablePromoForAll = promoForAll;
            promoDescription = String.format("Buy %s discount $%s for each item\nPromotion for all category enabled",pCount,pDiscount);
            tvPromo.setText(promoDescription);
        }
        else{
            promoDescription = String.format("Buy %s discount $%s for each item",pCount,pDiscount);
            tvPromo.setText(String.format("Buy %s discount $%s for each item",pCount,pDiscount));
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
        isPromo.setChecked(false);
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
