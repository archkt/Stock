package com.example.stock;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;


import static com.example.stock.MainActivity.filtered_items;
import static com.example.stock.ModifyItem.tvTotal2;
import static com.example.stock.ModifyItem.categoryDelta;
import static com.example.stock.ModifyItem.categoryTemp;

public class ModifyItemListAdapter extends ArrayAdapter<Item>{
    private static final String TAG = "ModifyItemListAdapter";

    private Context mContext;
    private int mResource;
    public static int total = 0;
    private int discount = 0;


    public ModifyItemListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final String category = getItem(position).getCategory();
        String name = getItem(position).getName();
        final int price = getItem(position).getPrice();
        final int stock = getItem(position).getStock();
        final int delta = getItem(position).getDelta();
        final String promoDescription = getItem(position).getPromoDescription();
        final int pCount = getItem(position).getPromoDetail()[0];
        final int pPrice = getItem(position).getPromoDetail()[1];
        final int pDiscount = getItem(position).getPromoDetail()[2];
        final Item item = new Item(category, name, price, stock);



        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent,false);

        final TextView tvDelta = convertView.findViewById(R.id.delta);
        TextView modifiableItems = convertView.findViewById(R.id.modifiableItems);
        TextView stockLeft = convertView.findViewById(R.id.stockLeft);
        ImageButton btnPlus = convertView.findViewById(R.id.btnPlus);
        ImageButton btnMinus = convertView.findViewById(R.id.btnMinus);
        final CheckBox promoEnabled = convertView.findViewById(R.id.applyPromoCheck);

        //check apply promo checkbox
        promoEnabled.setChecked(getItem(position).isPromo());
        promoEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(getItem(position).isPromo() == false){
                    promoEnabled.setChecked(false);
                    Toast.makeText(getContext(), "This item is not in promotion!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvDelta.setText(String.format("%s",delta));
        modifiableItems.setText(String.format("%s  %s", item.getCategory(), item.getName()));
        stockLeft.setText(String.format("%s",item.getStock()));

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getItem(position).minusOne();
                Log.d(TAG, "onClick: minus clicked " + position + "   " + getItem(position).getDelta());
                //if delta goes below 0
                if (getItem(position).getDelta() < 0){
                    getItem(position).setDelta(0);
                    Toast.makeText(getContext(),"below 0 is not possible", Toast.LENGTH_SHORT).show();
                }
                //if delta is above 0
                else{
                    if(promoEnabled.isChecked() && promoDescription.contains("Promotion for all category enabled")){
                        categoryDelta.put(category, categoryDelta.get(category)-1);
                        //if category delta is less than pCount
                        if(categoryDelta.get(category) < pCount-1){
                            discount = 0;
                            total -= price;
                            categoryTemp.put(category, categoryTemp.get(category) - price);
                        }
                        //if category delta reaches exactly at minimal promo count
                        else if(categoryDelta.get(category) == pCount-1 && pDiscount == -1){
                            Log.d(TAG, "current Category Total: " + categoryTemp.get(category));
                            int currentCategoryTotal = 0;

                            for(int i =0; i<filtered_items.size() ; i++){
                                if(getItem(i).getCategory().equals(category)){
                                    currentCategoryTotal += getItem(i).getPrice()*getItem(i).getDelta();
                                }

                            }
                            total = total - ((categoryDelta.get(category) + 1) * pPrice) + currentCategoryTotal;

                            categoryTemp.put(category, currentCategoryTotal);
                            Log.d(TAG, "current category total after change: " + categoryTemp.get(category));

                        }
                        //if category delta is more than delta
                        else{
                            //if price promo is applied
                            if (pDiscount == -1){
                                total -= pPrice;
                            }
                            //if discount promo is applied
                            else if (pPrice == -1){
                                discount = 0;
                                total -= price;
                            }
                        }
                    }

                    //if promo is enabled
                    else if(promoEnabled.isChecked()){
                        //if delta is above pCount
                        if(getItem(position).getDelta() < pCount-1){
                            discount = 0;
                            total -= price;
                        }
                        //if delta reaches exactly at minimal promo count
                        else if(getItem(position).getDelta() == pCount-1 && pDiscount == -1){
                            total = total + getItem(position).getDelta()*(price-pPrice) - pPrice;
                            //total = total - price + (getItem(position).getDelta()+1)*(price-pPrice);
                        }
                        else{
                            //if price promo is applied
                            if (pDiscount == -1){
                                total -= pPrice;
                            }
                            //if discount promo is applied
                            else if (pPrice == -1){
                                discount = 0;
                                total -= price;
                            }
                        }

                    }
                    //id promo is NOT enabled
                    else{
                        total -= price;
                    }
                }


                tvDelta.setText(String.format("%s",getItem(position).getDelta()));
                tvTotal2.setText(String.format("Total: $ %s", Integer.toString(total)));
                Log.d(TAG, "categoryHashmap: " + categoryDelta + "categoryTemp: " + categoryTemp);
                Log.d(TAG, "total: " + total);
            }
        });
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItem(position).plusOne();
                Log.d(TAG, "onClick: plus clicked " + position + "   " + getItem(position).getDelta());

                //if delta is less than its stock
                if (getItem(position).getDelta() <= stock){

                    //if promoforCategory is enabled
                    if(promoEnabled.isChecked() && promoDescription.contains("Promotion for all category enabled")){
                        categoryDelta.put(category, categoryDelta.get(category) +1);
                        //if categorydelta is less than promo count
                        if(categoryDelta.get(category) < pCount){
                            total += price;
                            categoryTemp.put(category, categoryTemp.get(category) + price);
                        }
                        //if delta reaches exactly at minimal promo count
                        else if(categoryDelta.get(category) == pCount && pDiscount == -1){
                            total = total + pPrice* categoryDelta.get(category) - categoryTemp.get(category);
                            categoryTemp.put(category, categoryTemp.get(category) + price);
                        }
                        //if category delta satisfies its minimal promo count
                        else{
                            //if price promo is applied
                            if(pDiscount == -1){
                                total += pPrice;
                            }
                            else if(pPrice == -1){
                                discount = pDiscount;
                                total += price;
                            }
                        }

                    }

                    //if promo is applied but promoforCateogory is not enabled
                    else if(promoEnabled.isChecked()){
                        //if delta is less than promo count
                        if(getItem(position).getDelta() < pCount){
                            total += price;
                        }
                        //if delta reaches exactly at minimal promo count
                        else if(getItem(position).getDelta() == pCount && pDiscount == -1){
                            total = total + price - getItem(position).getDelta()*(price-pPrice);
                        }
                        //if delta satisfies its minimal promo count
                        else{
                            //if price promo is applied
                            if (pDiscount == -1){
                                total += pPrice;
                            }
                            //if discount promo is applied
                            else if (pPrice == -1){
                                discount = pDiscount;
                                total += price;
                            }
                        }
                    }

                    //if promo is not applied
                    else{
                        total += price;
                    }
                }

                //if delta is more than its stock
                else{
                    getItem(position).minusOne();
                    Toast.makeText(getContext(), "Can't exceed current stock left", Toast.LENGTH_SHORT).show();
                }

                total -= discount;
                tvDelta.setText(String.format("%s",getItem(position).getDelta()));
                tvTotal2.setText(String.format("Total: $ %s", total));
                Log.d(TAG, "categoryHashmap: " + categoryDelta + "categoryTemp: " + categoryTemp);
            }
        });

        return convertView;

    }
}
