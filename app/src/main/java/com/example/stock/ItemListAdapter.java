package com.example.stock;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

import static com.example.stock.MainActivity.items;

public class ItemListAdapter extends ArrayAdapter<Item> {
    private static final String TAG = "ItemListAdapter";

    private Context mContext;
    private int mResource;

    static class ViewHolder {
        TextView name;
        TextView price;
        TextView stock;
    }

    public long getItemId(int position) {

        return items.get(position).getID();
    }

    public ItemListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String category = getItem(position).getCategory();
        String name = getItem(position).getName();
        int price = getItem(position).getPrice();
        int stock = getItem(position).getStock();

        Item item = new Item(category, name, price, stock);

        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent,false);

            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.tvItems);
            holder.price = (TextView) convertView.findViewById(R.id.tvPrice);
            holder.stock = (TextView) convertView.findViewById(R.id.tvStock);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.name.setText(item.getCategory() + "   " + item.getName());
        holder.price.setText(" $ " + Integer.toString(item.getPrice()));
        holder.stock.setText(Integer.toString(item.getStock()) + " left");

        return convertView;

    }
}
