package com.example.android.inventory;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.inventory.data.ProductsContract.SupplierEntry;
import com.example.android.inventory.databinding.ActivityProductDetailBinding;

import static com.example.android.inventory.data.ProductsContract.ProductEntry;

public class ProductDetail extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER_ID = 0;
    private static final String TAG = ProductDetail.class.getSimpleName();
    //TODO displays the Product Name, Price, Quantity, Supplier Name, and Supplier Phone Number
// TODO contains buttons that increase and decrease the available quantity displayed (don't allow negatives).
// TODO contains a button to delete the product record entirely.
// TODO   contains a button to order from the supplier via an intent to a phone app
    private ActivityProductDetailBinding binding;
    private Uri productUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);

        productUri = getIntent().getData();

        getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                SupplierEntry.COLUMN_SUPPLIER_NAME,
                SupplierEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this, productUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.e(TAG, DatabaseUtils.dumpCursorToString(data));
        if (data.moveToFirst()) {
            binding.productName.setText(data.getString(data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME)));
            binding.price.setText(data.getString(data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE)));
            binding.quantity.setText(data.getString(data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY)));
            binding.supplierName.setText(data.getString(data.getColumnIndexOrThrow(SupplierEntry.COLUMN_SUPPLIER_NAME)));
            binding.supplierPhone.setText(data.getString(data.getColumnIndexOrThrow(SupplierEntry.COLUMN_SUPPLIER_PHONE_NUMBER)));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
