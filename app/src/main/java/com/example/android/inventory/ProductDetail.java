package com.example.android.inventory;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.android.inventory.data.ProductsContract.SupplierEntry;
import com.example.android.inventory.databinding.ActivityProductDetailBinding;

import static com.example.android.inventory.data.ProductsContract.ProductEntry;

public class ProductDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final int PRODUCT_LOADER_ID = 0;
    private static final String TAG = ProductDetail.class.getSimpleName();
    private ActivityProductDetailBinding binding;
    private Uri productUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);

        productUri = getIntent().getData();
        getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
        binding.plusButton.setOnClickListener(this);
        binding.minusButton.setOnClickListener(this);
        binding.callSupplierButton.setOnClickListener(this);
        binding.deleteButton.setOnClickListener(this);
        binding.editButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int quantity = Integer.parseInt(String.valueOf(binding.quantity.getText()));
        String supplierPhoneNumber = String.valueOf(binding.supplierPhone.getText());
        switch (view.getId()) {
            case R.id.minus_button:
//                quantity cannot be negative
                if (quantity > 0) {
                    ContentValues values = new ContentValues();
                    // update product with smaller quantity
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, --quantity);
                    getContentResolver().update(productUri, values, null, null);
                    binding.quantity.setText(Integer.toString(quantity));
                }
                break;
            case R.id.plus_button:
                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, ++quantity);
                getContentResolver().update(productUri, values, null, null);
                binding.quantity.setText(Integer.toString(quantity));
                break;
            case R.id.call_supplier_button:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + supplierPhoneNumber));
                startActivity(callIntent);
                break;
            case R.id.delete_button:
                showDeleteConfirmationDialog();
                break;
            case R.id.edit_button:
                Intent editIntent = new Intent(ProductDetail.this, ProductEditor.class);
                editIntent.setData(productUri);
                startActivity(editIntent);
                break;
        }
    }


    /**
     * This method with changes comes from Pets shelter app from Udacity ABND.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deletion_prompt);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel_deletion, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
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

    private void deleteProduct() {
        if (productUri != null) {
            int deletedRows = getContentResolver().delete(productUri, null, null);
            if (deletedRows > 0) {
                Toast.makeText(this, R.string.on_delete_success,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.on_delete_failure,
                        Toast.LENGTH_SHORT).show();
            }
        }
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

    }
}
