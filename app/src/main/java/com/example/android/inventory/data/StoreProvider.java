package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.inventory.data.ProductsContract.ProductEntry;
import com.example.android.inventory.data.ProductsContract.SupplierEntry;

import java.util.Locale;

/**
 * {@link ContentProvider} for Store app.
 */
public class StoreProvider extends ContentProvider {

    public static final String TAG = StoreProvider.class.getSimpleName();

    private static final int PRODUCTS = 100;

    private static final int PRODUCT_ID = 101;

    private static final int SUPPLIERS = 200;

    private static final int SUPPLIER_ID = 201;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_PRODUCTS, PRODUCTS);
        uriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_PRODUCTS + "/#", PRODUCT_ID);

        uriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_SUPPLIERS, SUPPLIERS);
        uriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_SUPPLIERS + "/#", SUPPLIER_ID);
    }

    //
    ProductsDbHelper dbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        dbHelper = new ProductsDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
//        if (uri == null){
//            Log.e(TAG, "query: ERROR" );
//        }
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match) {

//                Join both tables on Supplier_id when displaying products
            case PRODUCTS:
               cursor = database.query(String.format(Locale.US, "%s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                        ProductEntry.TABLE_NAME, SupplierEntry.TABLE_NAME, ProductEntry.TABLE_NAME, ProductEntry._ID_SUPPLIER, SupplierEntry.TABLE_NAME, SupplierEntry._ID),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            //                Join both tables on Supplier_id when displaying products. How to avoid duplicating code from previous case?
            case PRODUCT_ID:
                selection = ProductEntry.TABLE_NAME_DOT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(String.format(Locale.US, "%s LEFT OUTER JOIN %s ON %s.%s=%s.%s",
                        ProductEntry.TABLE_NAME, SupplierEntry.TABLE_NAME, ProductEntry.TABLE_NAME, ProductEntry._ID_SUPPLIER, SupplierEntry.TABLE_NAME, SupplierEntry._ID),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                Log.e(TAG, DatabaseUtils.dumpCursorToString(cursor));
                break;
            case SUPPLIERS:
                cursor = database.query(SupplierEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SUPPLIER_ID:
                selection = SupplierEntry.TABLE_NAME_DOT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(SupplierEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Validate new data from the given ContentValues.
     */
    private void productDataValidation(ContentValues values) {
//        Data validation,
//        product_name cannot be null or empty string,
//        price can be null, it defaults to 0 inside database, has to be non-negative number
//        quantity can be null, it defaults to 0 inside database, has to be non-negative number
//        check if key exist
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null || TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Product requires a name");
            }
            int price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (price < 0 || quantity < 0) {
                throw new IllegalArgumentException("Price and quantity needs to be non-negative number");
            }
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            case SUPPLIERS:
                return insertSupplier(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
//    Check if data is valid
        if (values.size() != ProductEntry.NUMBER_OF_ADDITIONAL_COLUMNS) {
            throw new IllegalArgumentException("Product inserting requires"
                    + ProductEntry.NUMBER_OF_ADDITIONAL_COLUMNS
                    + "values");
        }
        productDataValidation(values);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);

        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it or null if insert failed
        return (id != -1) ? ContentUris.withAppendedId(uri, id) : null;
    }
    /**
     * Insert a supplier into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertSupplier(Uri uri, ContentValues values) {
//    Check if data is valid
        if (values.size() != SupplierEntry.NUMBER_OF_ADDITIONAL_COLUMNS) {
            throw new IllegalArgumentException("Product inserting requires "
                    + SupplierEntry.NUMBER_OF_ADDITIONAL_COLUMNS
                    + " values");
        }
//        productDataValidation(values);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(SupplierEntry.TABLE_NAME, null, values);

        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it or null if insert failed
        return (id != -1) ? ContentUris.withAppendedId(uri, id) : null;
    }

    /**
     * Update data with the given ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update products in the database with the given content values.
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        //    Check if data is valid
        productDataValidation(values);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        getContext().getContentResolver().notifyChange(uri, null);
        return database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}