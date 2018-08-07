package com.example.android.inventory;

import android.app.AlertDialog;
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

import com.example.android.inventory.data.ProductsContract.ProductEntry;
import com.example.android.inventory.data.ProductsContract.SupplierEntry;
import com.example.android.inventory.databinding.ActivityProductEditorBinding;

public class ProductEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ProductEditor.class.getSimpleName();
    private static final int PRODUCT_LOADER_ID = 0;
    //    TODO Validate user input, if a null value is inputted, add a Toast that prompts the user to input the correct information before they can continue.
//    TODO How to add supplier from product editor? Force user to choose from list of existing suppliers?
    private ActivityProductEditorBinding binding;
    private Uri productUri;
    private boolean productChanged = false;

    /**
     * This helper method comes from Pets shelter app from Udacidy ABND.
     * It listens for any touches on EditTexts by users assuming they change data
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            view.performClick();
            productChanged = true;
            return false;
        }
    };

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
            setTitle("Add product");
        } else {
            setTitle("Edit product");
            getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
        }

        binding.productName.setOnTouchListener(touchListener);
        binding.price.setOnTouchListener(touchListener);
        binding.quantity.setOnTouchListener(touchListener);
        binding.supplierName.setOnTouchListener(touchListener);
        binding.supplierPhone.setOnTouchListener(touchListener);
    }

    private void saveProduct() {
        // Read from input fields
        String name = String.valueOf(binding.productName.getText());

        if (TextUtils.isEmpty(name)) {
//            When user does not provide name, save button exits activity
            finish();
            return;
        }

        Double price = Double.valueOf(String.valueOf(binding.price.getText()));
        Integer quantity = Integer.valueOf(String.valueOf(binding.quantity.getText()));
//        We need supplierName and supplierPhone in array to use as selection arguments
        String[] supplierArg = new String[2];
        supplierArg[0] = String.valueOf(binding.supplierName.getText());
        supplierArg[1] = String.valueOf(binding.supplierPhone.getText());

        String[] supplierProjection = {SupplierEntry._ID,
                SupplierEntry.COLUMN_SUPPLIER_NAME,
                SupplierEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        Cursor cursor = getContentResolver().query(Uri.parse(SupplierEntry.SUPPLIER_NAME_URI + "/" + supplierArg[0]), supplierProjection,
                null,
                supplierArg,
                null);
        Log.e(TAG, "saveProduct: " + DatabaseUtils.dumpCursorToString(cursor));
//        values.put(ProductEntry._ID_SUPPLIER, supplierId);

        if (productUri != null) {
            getContentResolver().update(productUri, values, null, null);
        } else {
            getContentResolver().insert(productUri, values);
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
        builder.setMessage("Discard changes?");
        builder.setPositiveButton("Yes", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
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
