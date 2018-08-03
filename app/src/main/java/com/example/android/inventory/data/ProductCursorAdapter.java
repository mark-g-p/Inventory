package com.example.android.inventory.data;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.example.android.inventory.databinding.ProductListItemBinding;


public class ProductCursorAdapter extends CursorAdapter {


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ProductListItemBinding binding = ProductListItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return binding.getRoot();
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        ProductListItemBinding binding = DataBindingUtil.getBinding(view);
        if (binding != null) {
            binding.productName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));

            String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
            binding.price.setText(TextUtils.isEmpty(price) ? "0.00" : price);
            String quantity = cursor.getString(cursor.getColumnIndexOrThrow("quantity"));
            binding.quantity.setText(TextUtils.isEmpty(quantity) ? "0" : quantity);
        }
        view.setTag(binding);
    }
}