package com.informationUpload.contentproviders;

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
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashMap;

import com.informationUpload.contentproviders.Informations.Information;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: InformationProvider
 * @Date 2015/12/3
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class InformationProvider extends ContentProvider{

    private static final String LOG_TAG = "InformationProvider";
    private static final String DATABASE_NAME = "informationsDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String INFORMATION_TABLE_NAME = "informationTable";
    private static final String VIDEO_DATA_TABLE_NAME = "videoDataTable";

    private static final String INFORMATION_VIDEO_TABLES = INFORMATION_TABLE_NAME + " AS T1 LEFT JOIN "
            + VIDEO_DATA_TABLE_NAME + " AS T2 ON T1." + Informations.Information.ROWKEY + " = T2." + Informations.VideoData.PARENT_ID;


    private static final String ID_WITH_SPACE = " " + Informations.Information.ID;

    public static class InformationDbHelper extends SQLiteOpenHelper{

        private static volatile InformationDbHelper mInstance;

        private InformationDbHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mInstance = this;
        }

        public static InformationDbHelper getInstance(Context context) {
            if (mInstance == null) {
                synchronized (InformationDbHelper.class) {
                    if (mInstance == null) {
                        mInstance = new InformationDbHelper(context);
                    }
                }
            }
            return mInstance;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + INFORMATION_TABLE_NAME + " ("
                    + Informations.Information.ID + "  INTEGER PRIMARY KEY,"
                    + Informations.Information.ROWKEY + " TEXT,"
                    + Informations.Information.TIME + " INTEGER,"
                    + Informations.Information.USER_ID + " TEXT,"
                    + Informations.Information.TYPE + " INTEGER,"
                    + Informations.Information.STATUS + " INTEGER,"
                    + Informations.Information.LATITUDE + " DOUBLE,"
                    + Informations.Information.LONGITUDE + " DOUBLE,"
                    + Informations.Information.REMARK + " TEXT,"
                    + Informations.Information.ADDRESS+ " TEXT"
            + ");");

            db.execSQL("CREATE TABLE " + VIDEO_DATA_TABLE_NAME + " ("
                    + Informations.VideoData.ID + " INTEGER PRIMARY KEY,"
                    + Informations.VideoData.ROWKEY + " TEXT,"
                    + Informations.VideoData.TIME + " INTEGER,"
                    + Informations.VideoData.TYPE + " INTEGER,"
                    + Informations.VideoData.CONTENT + " TEXT,"
                    + Informations.VideoData.LATITUDE + " DOUBLE,"
                    + Informations.VideoData.LONGITUDE + " DOUBLE,"
                    + Informations.VideoData.REMARK + " TEXT"
                    
            + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + INFORMATION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + VIDEO_DATA_TABLE_NAME);
            onCreate(db);
        }

        public void deleteDb(){
            close();
            SQLiteDatabase db = getWritableDatabase();

            try {
                db.delete(INFORMATION_TABLE_NAME, null, null);
            } catch (Exception e) {
            }

            try {
                db.delete(VIDEO_DATA_TABLE_NAME, null, null);
            } catch (Exception e) {
            }
        }
    }

    private InformationDbHelper mInformationDbHelper;
    private static final UriMatcher uriMatcher;
    private static final int INFORMATION = 1;
    private static final int INFORMATION_ID = 2;

    private static final int VIDEO_DATA = 3;
    private static final int INFORMATION_WITH_VIDEO = 4;

    private static HashMap<String, String> maps;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Informations.AUTHORITY, "Information", INFORMATION);
        uriMatcher.addURI(Informations.AUTHORITY, "Information/#", INFORMATION_ID);
        uriMatcher.addURI(Informations.AUTHORITY, "VideoData", VIDEO_DATA);
        uriMatcher.addURI(Informations.AUTHORITY, "InformationWith_Video", INFORMATION_WITH_VIDEO);

        maps = new HashMap<String, String>();
        maps.put(Informations.Information.ID, Informations.Information.ID);
        maps.put(Informations.Information.ROWKEY, Informations.Information.ROWKEY);
        maps.put(Informations.Information.TIME,  Informations.Information.TIME);
        maps.put(Informations.Information.USER_ID, Informations.Information.USER_ID);
        maps.put(Informations.Information.TYPE, Informations.Information.TYPE);
        maps.put(Informations.Information.STATUS, Informations.Information.STATUS);
        maps.put(Informations.Information.LONGITUDE, Informations.Information.LONGITUDE);
        maps.put(Informations.Information.LATITUDE, Informations.Information.LATITUDE);
        maps.put(Informations.Information.REMARK, Informations.Information.REMARK);
        maps.put(Informations.Information.ADDRESS, Informations.Information.ADDRESS);
        
        maps.put(Informations.VideoData.ID, Informations.VideoData.ID);
        maps.put(Informations.VideoData.ROWKEY, Informations.VideoData.ROWKEY);
        maps.put(Informations.VideoData.TIME, Informations.VideoData.TIME);
        maps.put(Informations.VideoData.TYPE,  Informations.VideoData.TYPE);
        maps.put(Informations.VideoData.CONTENT, Informations.VideoData.CONTENT);
        maps.put(Informations.VideoData.LONGITUDE, Informations.VideoData.LONGITUDE);
        maps.put(Informations.VideoData.LATITUDE, Informations.VideoData.LATITUDE);
        maps.put(Informations.VideoData.REMARK, Informations.VideoData.REMARK);
    }


    @Override
    public boolean onCreate() {
        mInformationDbHelper = mInformationDbHelper.getInstance(getContext());
        return true;
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
        int count = 0;
        String formattedSelection = getFormattedSelection(selection, selectionArgs);
        switch (uriMatcher.match(uri)) {
            case INFORMATION:
                sqb.setTables(INFORMATION_TABLE_NAME);
                sqb.setProjectionMap(maps);
                sqb.setDistinct(true);
                return queryDb(sqb, mInformationDbHelper, uri, projection, selection, selectionArgs, sortOrder);
            case INFORMATION_ID:
                sqb.setTables(INFORMATION_TABLE_NAME);
                sqb.setProjectionMap(maps);
                sqb.setDistinct(true);
                sqb.appendWhere(Informations.Information.ID + "=" + uri.getPathSegments().get(1));
                return queryDb(sqb, mInformationDbHelper, uri, projection, selection, selectionArgs, sortOrder);
            case VIDEO_DATA:
                sqb.setTables(VIDEO_DATA_TABLE_NAME);
                sqb.setProjectionMap(maps);
                sqb.setDistinct(true);
                return queryDb(sqb, mInformationDbHelper, uri, projection, selection, selectionArgs, sortOrder);

            case INFORMATION_WITH_VIDEO:
                sqb.setTables(INFORMATION_VIDEO_TABLES);
                sqb.setProjectionMap(maps);
                sqb.setDistinct(true);
                formattedSelection = getFormattedSelection(selection, selectionArgs);
                if (!TextUtils.isEmpty(formattedSelection)) {
                    String regularQuery = getUpdatedQueryStringWithTableAlias(sqb.buildQuery(projection, formattedSelection, null, null, null, null)).toString();
                    Cursor cursor = mInformationDbHelper.getReadableDatabase().rawQuery(regularQuery, null);
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                    return cursor;
                } else {
                    return null;
                }
            default:
                throw new IllegalArgumentException();
        }
    }


    private String getFormattedSelection(String selection, String[] selectionArgs) {
        // Only support = format style like: "A = ? OP B = ? OP C = ?" currently
        // all args must be set in selectionArgs
        String[] selections = selection.split("(=)|(<>)");
        if (selections != null && selections.length > 0
                && selections.length == selectionArgs.length + 1) {
            StringBuilder formattedSelection =new StringBuilder();
            formattedSelection.append(selection);
            for (int idx = 0; idx < selectionArgs.length; idx++) {
                int idxOfQ = formattedSelection.indexOf("?");
                if (idxOfQ > -1) {
                    formattedSelection.replace(idxOfQ, idxOfQ+1, "'" + selectionArgs[idx] + "'");
                }
            }
            String formattedSelectionString = formattedSelection.toString();
            return formattedSelectionString;
        }
        else {
        }
        return null;
    }

    private StringBuffer getUpdatedQueryStringWithTableAlias(String queryString) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(queryString);
        if (queryString != null) {

            int idxOfId = queryString.indexOf(ID_WITH_SPACE);
            if (idxOfId > -1) {
                stringBuffer.replace(idxOfId + 1, idxOfId + Informations.Information.ID.length() + 1, " T1." + Informations.Information.ID);
            }

            return stringBuffer;
        }
        return stringBuffer;
    }

    private Cursor queryDb(SQLiteQueryBuilder sqb, SQLiteOpenHelper helper, Uri uri, String[] projection, String selection,
                           String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = sqb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mInformationDbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case INFORMATION:
                long _id = db.insert(INFORMATION_TABLE_NAME, null, values);
                if (_id > 0) {
                    Uri uri1 = ContentUris.withAppendedId(Informations.Information.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(uri1, null);
                    return uri1;
                }
                break;
            case VIDEO_DATA:
                 _id = db.insert(VIDEO_DATA_TABLE_NAME, null, values);
                if (_id > 0) {
                    Uri uri1 = ContentUris.withAppendedId(Informations.VideoData.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(uri1, null);
                    return uri1;
                }
                break;
        }
        return null;
    }

    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mInformationDbHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case INFORMATION:
                count = db.delete(INFORMATION_TABLE_NAME, selection, selectionArgs);
                break;
            case INFORMATION_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(INFORMATION_TABLE_NAME, Informations.Information.ID
                        + "="
                        + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                        + ')' : ""), selectionArgs);
                break;
            case VIDEO_DATA:
                count = db.delete(VIDEO_DATA_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mInformationDbHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case INFORMATION:
                count = db.update(INFORMATION_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case INFORMATION_ID:
                String id = uri.getPathSegments().get(1);
                count = db.update(INFORMATION_TABLE_NAME, values, Informations.Information.ID
                        + "="
                        + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection+ ')' : ""), selectionArgs);
                break;
            case VIDEO_DATA:
                count = db.update(VIDEO_DATA_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
