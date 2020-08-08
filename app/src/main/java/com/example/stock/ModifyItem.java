package com.example.stock;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import static com.example.stock.MainActivity.filtered_items;
import static com.example.stock.MainActivity.startingDate;
import static com.example.stock.MainActivity.todayTotal;
import static com.example.stock.ModifyItemListAdapter.total;

public class ModifyItem extends AppCompatActivity {


    private static final String TAG = "ModifyItem page";
    public static TextView tvTotal2;
    private int itemID, itemStock;
    public static HashMap<String, Integer> categoryDelta;
    public static HashMap<String, Integer> categoryTemp;
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnew);

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        Cursor categoryData = mDatabaseHelper.getDistinctItemCategory();
        categoryDelta = new HashMap<String, Integer>();
        categoryTemp = new HashMap<String, Integer>();
        while(categoryData.moveToNext()){
            categoryDelta.put(categoryData.getString(0),0);
            categoryTemp.put(categoryData.getString(0),0);
        }


        TextView categoryAndName = (TextView) findViewById(R.id.categoryAndName);
        ListView listView = (ListView) findViewById(R.id.listView2);
        Button btnApply = (Button) findViewById(R.id.btnApply);
        Button btnCreate = (Button) this.findViewById(R.id.create_new);
        Button btnCancel = (Button) findViewById(R.id.btnModifyCancel);

        categoryAndName.setText("Category/Name");

        tvTotal2 = (TextView) findViewById(R.id.tvTotal2);

        ModifyItemListAdapter adapter = new ModifyItemListAdapter(getApplicationContext(), R.layout.modify_adapter_view_layout, filtered_items);
        listView.setAdapter(adapter);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(Item i : filtered_items){
                    Cursor dataID = mDatabaseHelper.getItemID((i.getName()));
                    Cursor dataStock = mDatabaseHelper.getItemStock(i.getName());
                    itemID = -1;
                    itemStock = -1;
                    while(dataID.moveToNext()){
                        itemID = dataID.getInt(0);
                    }
                    while(dataStock.moveToNext()){
                        itemStock = dataStock.getInt(0);
                    }
                    mDatabaseHelper.updateStock(itemID,i.getStock()- i.getDelta());

                }

                mDatabaseHelper.updateTotal(startingDate, todayTotal + total);
                Log.d(TAG, "onClick: todayTotal + total == " + (todayTotal + total));
                total = 0;
                mDatabaseHelper.close();

                Intent intentGoMain = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intentGoMain);
                finishAffinity();
                Toast.makeText(getApplicationContext(),"changed", Toast.LENGTH_SHORT).show();

            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total = 0;
                Intent intentGoCreateNew = new Intent(getApplicationContext(), CreateNew.class);
                startActivity(intentGoCreateNew);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total = 0;
                Intent intentGoMain = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intentGoMain);
                finishAffinity();
            }
        });




    }
}
