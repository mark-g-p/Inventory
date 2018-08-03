package com.example.android.inventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.android.inventory.data.ProductCursorAdapter;
import com.example.android.inventory.data.ProductsContract.ProductEntry;
import com.example.android.inventory.data.ProductsDbHelper;
import com.example.android.inventory.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
//TODO make item on the list clickable. They take user to ProductDetail
//    TODO make button for adding new product, which takes user to ProductEditor
//    TODO add SALE button to the items on the list. It decreases quantity of product by 1
//    TODO add suppliers view as fragment?
    private static final String LOG_TAG = ProductsDbHelper.class.getSimpleName();
    private static final int PRODUCT_LOADER_ID = 0;
    ProductCursorAdapter productCursorAdapter;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


//        Initialize empty Adapter for Loader to populate
        productCursorAdapter = new ProductCursorAdapter(this, null);
        binding.listViewProduct.setAdapter(productCursorAdapter);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDummyProduct();
                recreate();
            }
        });

        // Prepare the loader
        getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
    }

    private void insertDummyProduct() {
        String name = "Apoes";
        int price = 123;
        int quantity = 213;

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry._ID_SUPPLIER, 1);

        Log.e(LOG_TAG, String.valueOf(values));
        // Insert the new row
        getContentResolver().insert(ProductEntry.CONTENT_URI, values);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        //        Create projection of columns which we are interested in.
        String[] projection = {ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY};

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        productCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        productCursorAdapter.swapCursor(null);
    }
}
