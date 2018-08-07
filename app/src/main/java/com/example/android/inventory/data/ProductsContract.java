package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class ProductsContract {
    static final String CONTENT_AUTHORITY = "com.example.android.inventory";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_PRODUCTS = "products";
    static final String PATH_SUPPLIERS = "suppliers";
    static final String PATH_SUPPLIERS_NAME = "name";

    //  Prevent user from creating object ProductsContract.
    private ProductsContract() {
    }

    public static final class SupplierEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUPPLIERS);
        public static final Uri SUPPLIER_NAME_URI = Uri.withAppendedPath(CONTENT_URI, PATH_SUPPLIERS_NAME);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of suppliers.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single supplier.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;


        public static final String TABLE_NAME = "suppliers";


        public static final int NUMBER_OF_ADDITIONAL_COLUMNS = 2;
        public static final String _ID = BaseColumns._ID;
        //        Use this when joining tables to avoid 'ambiguous column name' error from SQLite
        public static final String TABLE_NAME_DOT_ID = TABLE_NAME + '.' + _ID;
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

    }

    public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
       /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String TABLE_NAME = "products";

        public static final int NUMBER_OF_ADDITIONAL_COLUMNS = 4;
        public static final String _ID = BaseColumns._ID;
//        Use this when joining tables to avoid 'ambiguous column name' error from SQLite
        public static final String TABLE_NAME_DOT_ID = TABLE_NAME + '.' + _ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String _ID_SUPPLIER = "supplier_id";
    }
}
