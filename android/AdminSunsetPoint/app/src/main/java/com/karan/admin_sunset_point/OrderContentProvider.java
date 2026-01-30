package com.karan.admin_sunset_point;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.karan.admin_sunset_point.data.AppDatabase;

public class OrderContentProvider extends ContentProvider {

    private AppDatabase db;

    private static final String AUTHORITY =
            "com.karan.sunset_point.provider";

    private static final int ORDERS = 1;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "orders", ORDERS);
    }

    @Override
    public boolean onCreate() {
        db = AppDatabase.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {

        if (uriMatcher.match(uri) != ORDERS) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        MatrixCursor cursor = new MatrixCursor(
                new String[]{"json"}
        );

        String json = "";

        cursor.addRow(new Object[]{json});

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return "application/json";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
