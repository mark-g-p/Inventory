package com.example.android.inventory;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.android.inventory.data.ProductsContract.ProductEntry;
import com.example.android.inventory.data.ProductsContract.SupplierEntry;
import com.example.android.inventory.databinding.ActivityProductEditorBinding;

public class ProductEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ProductEditor.class.getSimpleName();
    private static final int PRODUCT_LOADER_ID = 0;
    private ActivityProductEditorBinding binding;
    private Uri productUri;
    private boolean productChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            view.performClick();
            productChanged = true;
            return false;
        }
    };

    /**
     * This helper method comes from Pets shelter app from Udacidy ABND.
     * It listens for any touches on EditTexts by users assuming they change data
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_editor);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_editor);

        productUri = getIntent().getData();
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {
                                                      saveProduct();
                                                  }
                                              }
        );
        if (productUri == null) {
            setTitle(getString(R.string.add_product_title));
        } else {
            setTitle(getString(R.string.edit_product_title));
            getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
        }

        binding.productName.setOnTouchListener(touchListener);
        binding.price.setOnTouchListener(touchListener);
        binding.quantity.setOnTouchListener(touchListener);
        binding.supplierName.setOnTouchListener(touchListener);
        binding.supplierPhone.setOnTouchListener(touchListener);
    }

    private void saveProduct() {
        // Read from input fields, input types takes care of negative numbers in price and quantity
        String name = String.valueOf(binding.productName.getText());
        String price = String.valueOf(binding.price.getText());
        String quantity = String.valueOf(binding.quantity.getText());

//        We need supplierName and supplierPhone in array to use as selection arguments
        String[] supplierArg = new String[2];
        supplierArg[0] = String.valueOf(binding.supplierName.getText());
        supplierArg[1] = String.valueOf(binding.supplierPhone.getText());

        if (TextUtils.isEmpty(name)
                || TextUtils.isEmpty(price)
                || TextUtils.isEmpty(quantity)
                || TextUtils.isEmpty(supplierArg[0])
                || TextUtils.isEmpty(supplierArg[1])) {
            Toast.makeText(this, R.string.wrong_data_provided,
                    Toast.LENGTH_SHORT).show();
            return;
        }

//      Get matching supplier from the database
        String[] supplierProjection = {SupplierEntry._ID,
                SupplierEntry.COLUMN_SUPPLIER_NAME,
                SupplierEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };
        Cursor supplierCursor = getContentResolver().query(Uri.parse(SupplierEntry.SUPPLIER_NAME_URI + "/" + supplierArg[0]), supplierProjection,
                null,
                supplierArg,
                null);
        Log.e(TAG, "saveProduct: " + DatabaseUtils.dumpCursorToString(supplierCursor));

//        If we don't find matching supplier, add him to the database, else, use existing entry
        long supplierId;
        if (supplierCursor.moveToFirst()) {
            supplierId = supplierCursor.getInt(supplierCursor.getColumnIndexOrThrow(SupplierEntry._ID));
        } else {
            ContentValues supplierValues = new ContentValues();
            supplierValues.put(SupplierEntry.COLUMN_SUPPLIER_NAME, supplierArg[0]);
            supplierValues.put(SupplierEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierArg[1]);
            Uri supplierUri = getContentResolver().insert(SupplierEntry.CONTENT_URI, supplierValues);
            supplierId = ContentUris.parseId(supplierUri);
        }

        //        Put all product data into the database
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, Double.valueOf(price));
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.valueOf(quantity));
        values.put(ProductEntry._ID_SUPPLIER, supplierId);
//      Depending on activity type add new product or update an existing one
        if (productUri != null) {
            getContentResolver().update(productUri, values, null, null);
        } else {
            getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        }
        finish();
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
        binding.productName.setText("");
        binding.price.setText("");
        binding.quantity.setText("");
        binding.supplierName.setText("");
        binding.supplierPhone.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!productChanged) {
                    finish();
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return true;
    }

    /**
     * This method with changes comes from Pets shelter app from Udacity ABND.
     */
    @Override
    public void onBackPressed() {
        if (!productChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * This method with changes comes from Pets shelter app from Udacity ABND.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes);
        builder.setPositiveButton(R.string.yes_button, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
