package com.example.stock;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;


public class Dialog extends AppCompatDialogFragment {
    //Promo Dialog EditText and Checkbox
    private EditText etPromoCount;
    private EditText etPromoPrice;
    private EditText etPromoDiscount;
    private CheckBox checkPromoForCategory;
    private DialogListener listener;


    private static final String TAG = "Dialog";
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog alertDialog = builder.create();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.promo_modify_layout, null);

        //Promo Dialog EditText and Checkbox
        etPromoCount = (EditText) view.findViewById(R.id.etPromoCount);
        etPromoPrice = (EditText) view.findViewById(R.id.etPromoPrice);
        etPromoDiscount = (EditText) view.findViewById(R.id.etPromoDiscount);
        checkPromoForCategory = view.findViewById(R.id.checkPromoForCategory);
        builder.setView(view);
        builder.setTitle("Choose promo");
        builder.setCancelable(false);

        builder.setNegativeButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String pCount = etPromoCount.getText().toString();
                String pPrice = etPromoPrice.getText().toString();
                String pDiscount = etPromoDiscount.getText().toString();

                //if etPromoCount is empty
                if(pCount.matches("")){
                    Toast.makeText(getContext(),"You must enter number",Toast.LENGTH_SHORT).show();
                    listener.cancelEverything("-1","-1","-1");
                    alertDialog.dismiss();
                }

                //if etPromoCount is filled, but etPromoPrice and etPromoDiscount are empty
                else if(!pCount.equals("") && pPrice.equals("") && pDiscount.equals("")){
                    Toast.makeText(getContext(),"You must choose one",Toast.LENGTH_SHORT).show();
                    listener.cancelEverything("-1","-1","-1");
                    alertDialog.dismiss();
                }
                //if etPromoPrice and etPromoDiscount are both filled
                else if(!pPrice.equals("") && !pDiscount.equals("")){
                    Toast.makeText(getContext(),"You must choose one",Toast.LENGTH_SHORT).show();
                    listener.cancelEverything("-1","-1","-1");
                    alertDialog.dismiss();
                }
                //if etPromoCount and etPromoPrice are filled, and etPromoDiscount is empty
                else if((!pCount.equals("") && !pPrice.equals("")) && pDiscount.equals("")){
                    listener.applyPricePromo(pCount, pPrice, "-1", checkPromoForCategory.isChecked());
                }
                //if etPromoCount and etPromoDiscount are filled, and etPromoPrice is empty
                else if((!pCount.equals("") && !pDiscount.equals("")) && pPrice.equals("")){
                    listener.applyDiscountPromo(pCount, "-1", pDiscount, checkPromoForCategory.isChecked());
                }
                else{
                    Toast.makeText(getContext(),"You must enter number",Toast.LENGTH_SHORT).show();
                    listener.cancelEverything("-1","-1","-1");
                    alertDialog.dismiss();
                }

            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                listener.cancelEverything("-1","-1","-1");

            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }

    }

    public interface DialogListener{
        void applyPricePromo(String pCount, String pPrice, String pDiscount, boolean promoForAll);
        void applyDiscountPromo(String pCount, String pPrice, String pDiscount, boolean promoForAll);
        void cancelEverything(String pCount, String pPrice, String pDiscount);
    }
}
