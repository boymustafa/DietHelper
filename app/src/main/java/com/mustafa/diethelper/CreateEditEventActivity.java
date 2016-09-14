package com.mustafa.diethelper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.text.MessageFormat;

import adapters.SpinnerArrayAdapter;
import db.DatabaseHelper;
import db.EventHelper;
import helpers.DateTimeHelper;
import models.Event;

/**
 * Created by Boy Mustafa on 07/09/16.
 */
public class CreateEditEventActivity extends AppCompatActivity {

    private static final String TAG = "CreateEditEventActivity";

    public static final String KEY_EVENT_PARCELABLE = "EVENT";
    public static final String KEY_POSITION_INT = "POSITION";
    public static final String KEY_ORG_DATE_SERIALIZABLE = "ORG_DATE";
    public static final String KEY_EXCEPTION_SERIAIZABLE = "EXCEPTION";
    public static final int RESULT_ERROR = -2;
    public static final int RESULT_FAILED = -1;
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_INSERTED = 1;
    public static final int RESULT_UPDATED = 2;
    public static final int RESULT_DELETED = 3;
    public static final int REQUEST_CREATE_EDIT = 1;

    private int mPosition;
    private Event event;
    private LocalDate orgDate;

    private ActionBar actionBar;
    private TextView txtDatePicker, txtTimePicker;
    private Spinner spType;
    private EditText etDescriptiojn;

    private MenuItem menuItemDelete,menuItemCopy;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }
        actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState!=null){
            event = savedInstanceState.getParcelable(KEY_EVENT_PARCELABLE);
            mPosition = savedInstanceState.getInt(KEY_POSITION_INT,-1);
            orgDate = (LocalDate) savedInstanceState.getSerializable(KEY_ORG_DATE_SERIALIZABLE);

        } else {
            event = getIntent().getParcelableExtra(KEY_EVENT_PARCELABLE);
            mPosition = getIntent().getIntExtra(KEY_POSITION_INT,-1);
            orgDate = null;
        }

        spType = (Spinner) findViewById(R.id.spTypes);
        txtDatePicker = (TextView) findViewById(R.id.tvDatePicer);
        txtTimePicker = (TextView) findViewById(R.id.tvTimePicker);
        etDescriptiojn = (EditText) findViewById(R.id.etDesc);

        switch (event.getmType()){
            case Event.TYPE_FOOD:
                setSpinnerContents(spType,R.array.spinner_event_food_types,event.getmSubType());
                break;
            case Event.TYPE_DRING:
                setSpinnerContents(spType,R.array.spinner_event_drink_types,event.getmSubType());
                break;
            default:
                setSpinnerContents(spType,R.array.spinner_event_types,event.getmType() - 2,2,R.array.spinner_event_types_res);

                ImageView icon = (ImageView) findViewById(R.id.ivTyoe);
                if (icon!=null){
                    icon.setVisibility(View.GONE);
                }
                break;
        }

        if (event.getmID() == -1){
            if (event.getmTime()==null){
                event.setmTime(new LocalTime());
            }
        } else {
            if (actionBar!=null)
                actionBar.setTitle(getString(R.string.activity_edit_event_title));

            etDescriptiojn.setText(event.getmDescription());
            if (orgDate==null)
                orgDate = event.getmDate();
        }

        txtDatePicker.setText(DateTimeHelper.convertLocalDateToString(event.getmDate()));
        txtTimePicker.setText(DateTimeHelper.convertLocalTimeToString(this, event.getmTime()));
    }

    private void setSpinnerContents(Spinner spinner, @ArrayRes int spinnerContents, int selectedIndex){
        setSpinnerContents(spinner,spinnerContents,selectedIndex,0,0);
    }


    private void setSpinnerContents(Spinner spinner, @ArrayRes int spinnerContents, int selectedIndex,
                                    final int offset, @ArrayRes int spinnerIcons){
        final SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(CreateEditEventActivity.this,
                spinnerContents,spinnerIcons,false,offset);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedIndex);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(KEY_EVENT_PARCELABLE,event);
        intent.putExtra(KEY_POSITION_INT,mPosition);
        intent.putExtra(KEY_ORG_DATE_SERIALIZABLE,orgDate);

        setResult(RESULT_CANCELED,intent);

        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_EVENT_PARCELABLE,event);
        outState.putInt(KEY_POSITION_INT,mPosition);
        outState.putSerializable(KEY_ORG_DATE_SERIALIZABLE,orgDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_create_edit_event_activity,menu);


        if (mPosition >= 0){
            menuItemDelete = menu.findItem(R.id.action_delete);
            menuItemDelete.setVisible(true);

            menuItemCopy = menu.findItem(R.id.action_copy);
            menuItemCopy.setVisible(true);
        } else {
            menuItemDelete = menuItemCopy = null;
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        intent.putExtra(KEY_EVENT_PARCELABLE,event);
        intent.putExtra(KEY_POSITION_INT,mPosition);
        intent.putExtra(KEY_ORG_DATE_SERIALIZABLE,orgDate);

        SQLiteDatabase db = null;

        try{
            final DatabaseHelper dbHelper = new DatabaseHelper(CreateEditEventActivity.this);
            db = dbHelper.getWritableDatabase();

            switch (item.getItemId()){
                case android.R.id.home:
                    setResult(RESULT_CANCELED,intent);
                    CreateEditEventActivity.this.finish();
                    return true;
                case R.id.action_save:
                    switch (event.getmType()){
                        case Event.TYPE_FOOD:
                            event.setmSubType(spType.getSelectedItemPosition());
                            break;
                        case Event.TYPE_DRING:
                            event.setmSubType(spType.getSelectedItemPosition());
                            break;
                        default:
                            event.setmType(spType.getSelectedItemPosition() +
                                    ((SpinnerArrayAdapter) spType.getAdapter()).getOffset());
                            break;
                    }

                    event.setmDescription(etDescriptiojn.getText().toString());
                    BackupManager backupManager = new BackupManager(this);

                    if (mPosition >= 0){
                        if (EventHelper.update(db,event)){
                            setResult(RESULT_UPDATED,intent);
                            backupManager.dataChanged();
                        } else {
                            Log.e(TAG,"Update operation failed.");
                            setResult(RESULT_FAILED,intent);
                        }
                    } else {
                        if (EventHelper.insert(db,event)){
                            Log.d(TAG, MessageFormat.format("A new record inserted to the database with id {0,number,integer}.", event.getmID()));
                            setResult(RESULT_INSERTED,intent);

                            backupManager.dataChanged();
                        } else {
                            Log.e(TAG,"Insert operation failed.");
                            setResult(RESULT_FAILED, intent);
                        }
                    }

                    CreateEditEventActivity.this.finish();
                    return true;

                case R.id.action_delete:
                    if (EventHelper.delete(db,event)){
                        if (!event.getmDate().isEqual(orgDate)){
                            event.setmDate(orgDate);
                            intent.putExtra(KEY_EVENT_PARCELABLE,event);
                        }
                        setResult(RESULT_DELETED,intent);
                    } else {
                        Log.e(TAG,"Delete operation returned false.");
                        setResult(RESULT_FAILED);
                    }

                    CreateEditEventActivity.this.finish();
                    return true;

                case R.id.action_calendar_today:
                    orgDate = null;
                    mPosition = -1;
                    event.setmID(-1);
                    event.setmDate(new LocalDate());
                    txtDatePicker.setText(DateTimeHelper.convertLocalDateToString(event.getmDate()));
                    menuItemCopy.setVisible(false);
                    menuItemDelete.setVisible(false);

                    if (actionBar!=null){
                        actionBar.setTitle(getString(R.string.activity_create_event_title));
                    }

                    return true;

            }
        }catch (SQLiteException e) {
            Log.e(TAG, "Content cannot be prepared probably a DB issue.", e);

            intent.putExtra(KEY_EXCEPTION_SERIAIZABLE, e);
            setResult(RESULT_ERROR, intent);
        } catch (Exception e) {
            Log.e(TAG, "Content cannot be prepared.", e);

            intent.putExtra(KEY_EXCEPTION_SERIAIZABLE, e);
            setResult(RESULT_ERROR, intent);
        } finally {
            if (null != db && db.isOpen()) {
                db.close();
            }
        }




        return super.onOptionsItemSelected(item);
    }

    public void onDatePickerButtonClicked(View view){
        DatePickerDialog datePicker = new DatePickerDialog(CreateEditEventActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfyear, int dayOfMonth) {
                        event.setmDate(new LocalDate(year,monthOfyear + 1,dayOfMonth));
                        Log.d(TAG,MessageFormat.format("date selected {0}",event.getmDate()));

                        txtDatePicker.setText(DateTimeHelper.convertLocalDateToString(event.getmDate()));
                    }
                },event.getmDate().getYear(),event.getmDate().getMonthOfYear() - 1, event.getmDate().getDayOfMonth());

        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE,getString(android.R.string.ok),datePicker);
        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE,getString(android.R.string.cancel),datePicker);

        datePicker.show();

    }

    public void onTimePickerButtonClicked(View view){
        TimePickerDialog timePicker = new TimePickerDialog(CreateEditEventActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        event.setmTime(new LocalTime(hourOfDay, minute));
                        Log.d(TAG, MessageFormat.format("time selected {0}", event.getmTime()));

                        txtTimePicker.setText(DateTimeHelper.convertLocalTimeToString(CreateEditEventActivity.this, event.getmTime()));
                    }
                }, event.getmTime().getHourOfDay(), event.getmTime().getMinuteOfHour(), DateTimeHelper.is24HourMode(this)
        );

        timePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(android.R.string.ok), timePicker);
        timePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel), timePicker);

        timePicker.show();
    }
}
