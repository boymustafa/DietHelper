package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.text.MessageFormat;
import java.util.ArrayList;

import models.Event;

/**
 * Created by Boy Mustafa on 07/09/16.
 */
public class EventHelper {
    private static final String TAG = "EventHelper";

    public static Event parse(@NonNull final Cursor cursor){
        return new Event(
                cursor.getLong(0),
                LocalDate.parse(cursor.getString(1),DatabaseHelper.DB_DATE_FORMATTER),
                LocalTime.parse(cursor.getString(2),DatabaseHelper.DB_TIME_FORMATTER),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getString(5)
                );
    }

    public static String[] getDBColumns(){
        return new String[]{
                DatabaseHelper.DBC_EVENT_ROW_ID,
                DatabaseHelper.DBC_EVENT_DATE,
                DatabaseHelper.DBC_EVENT_TIME,
                DatabaseHelper.DBC_EVENT_TYPE,
                DatabaseHelper.DBC_EVENT_SUBTYPE,
                DatabaseHelper.DBC_EVENT_SUBTYPE
        };
    }

    public static ContentValues getContentValues(@NonNull final  Event event){
        return getContentValues(event,false);
    }

    public static ContentValues getContentValues(@NonNull final Event event, final boolean includeID){
        final ContentValues values = new ContentValues(includeID ? 6 : 5);

        if (includeID)
            values.put(DatabaseHelper.DBC_EVENT_ROW_ID,event.getmID());

        values.put(DatabaseHelper.DBC_EVENT_DATE,event.getmDate().toString(DatabaseHelper.DB_DATE_FORMATTER));
        values.put(DatabaseHelper.DBC_EVENT_TIME,event.getmDate().toString(DatabaseHelper.DB_TIME_FORMATTER));
        values.put(DatabaseHelper.DBC_EVENT_TYPE,event.getmType());
        values.put(DatabaseHelper.DBC_EVENT_SUBTYPE,event.getmSubType());
        values.put(DatabaseHelper.DBC_EVENT_DESC,event.getmDescription());

        return values;

    }

    public static boolean insert(@NonNull final Context context, @NonNull final Event event) throws SQLiteException{

        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            return insert(db,event);
        } finally {
            databaseHelper.close();
        }

    }

    public static boolean insert(@NonNull final SQLiteDatabase writeableDB, @NonNull final Event event) throws SQLiteException{

        long id = writeableDB.insert(DatabaseHelper.DBT_EVENT,DatabaseHelper.DBC_EVENT_DESC,
                EventHelper.getContentValues(event,event.getmID()>0));

        if(id>=0){
            event.setmID(id);
            return true;
        }

        return false;
    }

    public static boolean update(@NonNull final Context context, @NonNull final Event event) throws SQLiteException {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            return update(db,event);
        }finally {
            databaseHelper.close();
        }
    }

    public static boolean update(@NonNull final SQLiteDatabase writeableDb, @NonNull final Event event){
        return writeableDb.update(DatabaseHelper.DBT_EVENT,EventHelper.getContentValues(event),
                DatabaseHelper.DBC_EVENT_ROW_ID + " == ?",
                new String[]{Long.toString(event.getmID())}) > 0;

    }

    public static boolean delete(@NonNull final SQLiteDatabase writeableDB,@NonNull final Event event) throws SQLiteException{
        return writeableDB.delete(DatabaseHelper.DBT_EVENT,
                DatabaseHelper.DBC_EVENT_ROW_ID + " == ?",
                new String[]{Long.toString(event.getmID())}) > 0;
    }

    public static boolean delete(@NonNull final Context context, @NonNull final Event event) throws SQLiteException{
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            return delete(db,event);
        } finally {
            databaseHelper.close();
        }
    }

    @Nullable
    public static Event getEventByID(@NonNull final Context context, final long id){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        try {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return getEventByID(db,id);
        } finally {
            databaseHelper.close();
        }
    }

    @Nullable
    public static Event getEventByID(@NonNull final SQLiteDatabase readableDB,final long id) throws SQLiteException{

        Cursor cursor = null;
        try {
            cursor = readableDB.query(DatabaseHelper.DBT_EVENT, getDBColumns(),
                    DatabaseHelper.DBC_EVENT_ROW_ID + " == ?", new String[]{Long.toString(id)},
                    null, null, null);

            if (cursor.moveToFirst())
                return parse(cursor);
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        return null;

    }

    @Nullable
    public static ArrayList<Event> getEventByDate(@NonNull final SQLiteDatabase readableDB, @NonNull final LocalDate date) throws SQLiteException{

        Cursor cursor = null;
        try {
            cursor = readableDB.query(DatabaseHelper.DBT_EVENT, EventHelper.getDBColumns(),
                    DatabaseHelper.DBC_EVENT_DATE + " = ?", new String[]{date.toString(DatabaseHelper.DB_DATE_FORMATTER)},
                    null, null, DatabaseHelper.DBC_EVENT_TIME + "," + DatabaseHelper.DBC_EVENT_ROW_ID);

            ArrayList<Event> list;
            if (cursor.moveToFirst()){
                Log.d(TAG, MessageFormat.format("There are {0,number,integer} records on {1}.",cursor.getCount(),date));
                list = new ArrayList<>(cursor.getCount());

                do {
                    list.add(parse(cursor));
                } while (cursor.moveToNext());

                return list;
            }

        } finally {
            if (cursor!=null)
                cursor.close();
        }

        return null;

    }

    @Nullable
    public static ArrayList<Event> getEventByDate(@NonNull final Context context,@NonNull final LocalDate date) throws SQLiteException{

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        try {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return getEventByDate(db,date);
        } finally {
            databaseHelper.close();
        }

    }

    @Nullable
    public static ArrayList<Event> getEventByDateRange(@NonNull final SQLiteDatabase readableDB,
                                                       @NonNull final LocalDate startDate,
                                                       @NonNull final LocalDate endDate)
        throws SQLiteException,IllegalArgumentException{
        if (startDate.isAfter(endDate))
            throw new IllegalArgumentException("startDate must be <= endDate");

        Cursor cursor = null;
        try {
            cursor = readableDB.query(DatabaseHelper.DBT_EVENT, EventHelper.getDBColumns(),
                    DatabaseHelper.DBC_EVENT_DATE + " >= ? AND " + DatabaseHelper.DBC_EVENT_DATE + " <= ?",
                    new String[]{startDate.toString(DatabaseHelper.DB_DATE_FORMATTER), endDate.toString(DatabaseHelper.DB_DATE_FORMATTER)},
                    null, null, DatabaseHelper.DBC_EVENT_DATE + "," + DatabaseHelper.DBC_EVENT_TIME + "," + DatabaseHelper.DBC_EVENT_ROW_ID);

            ArrayList<Event> list;
            if (cursor.moveToFirst()){
                Log.d(TAG,MessageFormat.format("There are {0,number,integer} records between {1} and {2}.",cursor.getCount(),startDate,endDate));
                list = new ArrayList<>(cursor.getCount());

                do {
                    list.add(parse(cursor));
                }while (cursor.moveToNext());

                return list;
            }
        } finally {
            if (cursor!=null)
                cursor.close();
        }

        return null;

    }

    @Nullable
    public static ArrayList<Event> getEventByDateRange(@NonNull final Context context,
                                                       @NonNull final LocalDate startDate,
                                                       @NonNull final LocalDate endDate)
        throws SQLiteException, IllegalArgumentException{
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        try {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return getEventByDateRange(db,startDate,endDate);
        } finally {
            databaseHelper.close();
        }
    }

    public static int hasEventToday(@NonNull final SQLiteDatabase readableDB) throws SQLiteException{
        Cursor cursor = null;
        try {
            cursor = readableDB.query(DatabaseHelper.DBT_EVENT, new String[]{"COUNT(*)",},
                    DatabaseHelper.DBC_EVENT_DATE + " = ?", new String[]{LocalDate.now().toString(DatabaseHelper.DB_DATE_FORMATTER)},
                    null, null, null);

            if (cursor.moveToFirst())
                return cursor.getInt(0);

        } finally {
            if (cursor!=null)
                cursor.close();
        }

        return 0;
    }


    public static int hasEventToday(@NonNull final Context context) throws SQLiteException {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        try {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return hasEventToday(db);
        } finally {
            databaseHelper.close();
        }
    }
}
