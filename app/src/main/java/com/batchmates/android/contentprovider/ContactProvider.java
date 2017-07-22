package com.batchmates.android.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Android on 7/22/2017.
 */

public class ContactProvider extends ContentProvider {

    public static final String PROVIDER_NAME=
            "com.batchmates.android.contentprovider.ContactProvider";


    static final String URL="content://"+ PROVIDER_NAME+"/cpcontacts";

    static final Uri CONTENT_URL=Uri.parse(URL);

    static final String id="id";

    static final String name="name";

    static final int uriCode=1;

    private static HashMap<String,String> values;

    static final UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        uriMatcher.addURI(PROVIDER_NAME,"cpcontacts",uriCode);
    }


    private SQLiteDatabase sqlDB;
    static final String DATABASE_NAME="myContacts";
    static final String TABLE_NAME="names";
    static final int DATABASE_VERSION=1;
    private static String CREATE_DB_TABLE="CREATE TABLE "+ TABLE_NAME+" (id INTEGER PRIMARY KEY AUTOINCREMENT, "+" name TEXT NOT NULL);";

    @Override
    public boolean onCreate() {


        DatabaseHelper dbHelper= new DatabaseHelper(getContext());
        sqlDB=dbHelper.getWritableDatabase();

        if(sqlDB!=null)
        {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);


        switch (uriMatcher.match(uri))
        {
            case uriCode:
                queryBuilder.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: "+uri);
        }


        Cursor cursor=queryBuilder.query(sqlDB,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri))
        {
            case uriCode:
                return "vnd.android.cursor.dir/cpcontacts";
            default:
                throw new IllegalArgumentException("Unsuported Uri: "+uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        long RowId=sqlDB.insert(TABLE_NAME,null,contentValues);

        if (RowId>0)
        {
            Uri _uri= ContentUris.withAppendedId(CONTENT_URL,RowId);
            getContext().getContentResolver().notifyChange(_uri,null);
            return _uri;
        }
        else
        {
            Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        int rowsDeleted=0;
        switch (uriMatcher.match(uri))
        {
            case uriCode:
                rowsDeleted=sqlDB.delete(TABLE_NAME,s,strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: "+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        int rowsUpdated=0;
        switch (uriMatcher.match(uri))
        {
            case uriCode:
                rowsUpdated=sqlDB.update(TABLE_NAME,contentValues,s,strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: "+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;

    }

    private class DatabaseHelper extends SQLiteOpenHelper{

        DatabaseHelper(Context context)
        {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);

        }


        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            sqLiteDatabase.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(sqLiteDatabase);

        }
    }
}
