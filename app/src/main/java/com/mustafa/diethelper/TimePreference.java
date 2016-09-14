package com.mustafa.diethelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import org.joda.time.LocalTime;

import db.DatabaseHelper;
import helpers.DateTimeHelper;

/**
 * Created by Boy Mustafa on 08/09/16.
 */
public class TimePreference extends android.preference.DialogPreference {

    private static final String DEFAULT_VALUE = "00:00";
    private int hour,minute;
    private TimePicker picker = null;

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        setPositiveButtonText(context.getString(android.R.string.ok));
        setNegativeButtonText(context.getString(android.R.string.cancel));

        setDialogTitle("");//remove title bar
    }

    public TimePreference(Context context, AttributeSet attrs){
        this(context,attrs,android.R.attr.preferenceStyle);
    }

    public TimePreference(Context context){
        this(context,null);
    }

    public void setTime(int hour, int minute){
        boolean wasBlocking = shouldDisableDependents();
        this.hour = hour;
        this.minute = minute;
        persistString(getLocalTime().toString(DatabaseHelper.DB_TIME_FORMATTER));
        boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking){
            notifyDependencyChange(isBlocking);
        }
    }

    public int getHour(){
        return this.hour;
    }

    public int getMinute(){
        return this.minute;
    }

    public LocalTime getLocalTime(){
        return new LocalTime(hour,minute);
    }

    protected Object onGetDefaultValue(TypedArray a, int index){
        return a.getString(index);
    }

    protected void onSetInitialValue(Boolean restoreValue, Object defaultValue){
        String value;
        if(restoreValue){
            if (defaultValue == null){
                value = getPersistedString(DEFAULT_VALUE);
            } else {
                value = getPersistedString((String) defaultValue);
            }
        } else {
            value = (String) defaultValue;
        }

        LocalTime time = LocalTime.parse(value,DatabaseHelper.DB_TIME_FORMATTER);
        setTime(time.getHourOfDay(),time.getMinuteOfHour());
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        return picker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        picker.setIs24HourView(DateTimeHelper.is24HourMode(getContext()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            picker.setHour(hour);
            picker.setMinute(minute);
        } else {
            picker.setCurrentHour(hour);
            picker.setCurrentMinute(minute);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult){
            // TimePicker methods renamed at API 23
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                hour = picker.getHour();
                minute = picker.getMinute();
            } else {
                hour = picker.getCurrentHour();
                minute = picker.getCurrentMinute();
            }

            String value = getLocalTime().toString(DatabaseHelper.DB_TIME_FORMATTER);
            if (callChangeListener(value)){
                persistString(value);
            }
        }
    }

    protected Parcelable onSaveInstanceState(){
        Parcelable superState = super.onSaveInstanceState();
        if (isPersistent())
            return superState;
        else {
            TimePreference.SavedState myState = new TimePreference.SavedState(superState);
            myState.mHour = hour;
            myState.mMinute = minute;
            return myState;
        }
    }

    protected void onRestoreInstanceState(Parcelable state){
        if (state != null && state.getClass().equals(TimePreference.SavedState.class)){
            TimePreference.SavedState myState = (SavedState) state;
            super.onRestoreInstanceState(myState.getSuperState());
            setTime(myState.mHour,myState.mMinute);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private static class SavedState extends BaseSavedState{
        public static final Creator<TimePreference.SavedState> CREATOR = new Creator<TimePreference.SavedState>() {
            public TimePreference.SavedState createFromParcel(Parcel in) {
                return new TimePreference.SavedState(in);
            }

            public TimePreference.SavedState[] newArray(int size) {
                return new TimePreference.SavedState[size];
            }
        };

        int mHour, mMinute;

        public SavedState(Parcel source) {
            super(source);
            mHour = source.readInt();
            mMinute = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mHour);
            dest.writeInt(mMinute);
        }
    }
}
