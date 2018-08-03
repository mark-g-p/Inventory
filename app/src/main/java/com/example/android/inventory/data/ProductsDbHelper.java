package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventory.data.ProductsContract.ProductEntry;
import com.example.android.inventory.data.ProductsContract.SupplierEntry;

public class ProductsDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = ProductsDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "store.db";

    ProductsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        Create table of suppliers
        String SQL_CREATE_SUPPLIERS_TABLE = String.format("CREATE TABLE %s(" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT, " +
                        "%s TEXT);",
                SupplierEntry.TABLE_NAME,
                SupplierEntry._ID,
                SupplierEntry.COLUMN_SUPPLIER_NAME,
                SupplierEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        sqLiteDatabase.execSQL(SQL_CREATE_SUPPLIERS_TABLE);
        Log.e(LOG_TAG, SQL_CREATE_SUPPLIERS_TABLE);

//        Create table of products
        String SQL_CREATE_PRODUCTS_TABLE = String.format("CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT NOT NULL, " +
                        "%s INTEGER NOT NULL, " +
                        "%s INTEGER DEFAULT 0, " +
                        "%s INTEGER DEFAULT 0, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s));",
                ProductEntry.TABLE_NAME,
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry._ID_SUPPLIER,
                ProductEntry._ID, SupplierEntry.TABLE_NAME, SupplierEntry._ID);
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        Log.e(LOG_TAG, SQL_CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_PRODUCTS = "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;
        String SQL_DELETE_SUPPLIERS = "DROP TABLE IF EXISTS " + SupplierEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_PRODUCTS);
        db.execSQL(SQL_DELETE_SUPPLIERS);
        onCreate(db);
    }
}
