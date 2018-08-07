package com.example.android.inventory.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.example.android.inventory.data.ProductsContract.ProductEntry;
import com.example.android.inventory.databinding.ProductListItemBinding;


public class ProductCursorAdapter extends CursorAdapter {


    private static final String TAG = ProductCursorAdapter.class.getSimpleName();

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ProductListItemBinding binding = ProductListItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return binding.getRoot();
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        ProductListItemBinding binding = DataBindingUtil.getBinding(view);
        if (binding != null) {
            binding.productName.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME)));
            // get uri of product to use when we update quantity after clicking SALE button
            final Uri productUri = Uri.withAppendedPath(ProductEntry.CONTENT_URI, String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry._ID))));

            String price = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));
            binding.price.setText(TextUtils.isEmpty(price) ? "0.00" : price);
            final String quantity = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            binding.quantity.setText(TextUtils.isEmpty(quantity) ? "0" : quantity);

            final int quantityForSale = Integer.parseInt(quantity);
//           Disable button if product quantity is 0
            if (quantityForSale == 0) {
                binding.saleButton.setEnabled(false);
            } else {
                binding.saleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ContentValues values = new ContentValues();
                        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityForSale - 1);
                        context.getContentResolver().update(productUri,
                                values,
                                null,
                                null
                        );
                    }
                });
            }
        }
        view.setTag(binding);
    }
}