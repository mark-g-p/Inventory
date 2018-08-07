package com.example.android.inventory;

import android.content.ContentUris;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.android.inventory.data.ProductCursorAdapter;
import com.example.android.inventory.data.ProductsContract.ProductEntry;
import com.example.android.inventory.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PRODUCT_LOADER_ID = 0;
    ProductCursorAdapter productCursorAdapter;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        productCursorAdapter = new ProductCursorAdapter(this, null);
        binding.listViewProduct.setAdapter(productCursorAdapter);

        binding.listViewProduct.setEmptyView(binding.emptyView);
        binding.listViewProduct.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent productIntent = new Intent(MainActivity.this, ProductDetail.class);

                Log.e(TAG, "onItemClick: " + ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id));
                productIntent.setData(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id));
                startActivity(productIntent);
            }
        });
        binding.addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addProductIntent = new Intent(MainActivity.this, ProductEditor.class);
                startActivity(addProductIntent);
            }
        });
        // Prepare the loader
        getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {ProductEntry.TABLE_NAME_DOT_ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY};

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
