package models;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Created by Boy Mustafa on 07/09/16.
 */
public class Event implements Parcelable {

    public static final int TYPE_FOOD = 0;
    public static final int TYPE_DRING = 1;
    public static final int TYPE_MED = 2;
    public static final int TYPE_SUPP = 3;
    public static final int TYPE_EXC = 4;
    public static final int TYPE_OTHER = 5;
    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel parcel) {
            return new Event(parcel);
        }

        @Override
        public Event[] newArray(int i) {
            return new Event[i];
        }
    };


    private long mID;
    private LocalDate mDate;
    private LocalTime mTime;
    private int mType;
    private int mSubType;
    private String mDescription;

    public Event(){
        setmID(-1);
    }

    public Event(long id,LocalDate date, LocalTime time, int type, int subType, String desc){
        setmID(id);
        setmDate(date);
        setmTime(time);
        setmType(type);
        setmSubType(subType);
        setmDescription(desc);
    }


    protected Event(Parcel in){
        mID=in.readLong();
        mDate = (LocalDate) in.readSerializable();
        mTime = (LocalTime) in.readSerializable();
        mType = in.readInt();
        mSubType = in.readInt();
        mDescription = in.readString();
    }

    public long getmID() {
        return mID;
    }

    public void setmID(long mID) {
        this.mID = mID;
    }

    public LocalDate getmDate() {
        return mDate;
    }

    public void setmDate(LocalDate mDate) {
        this.mDate = mDate;
    }

    public LocalTime getmTime() {
        return mTime;
    }

    public void setmTime(LocalTime mTime) {
        this.mTime = mTime;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public int getmSubType() {
        return mSubType;
    }

    public void setmSubType(int mSubType) {
        this.mSubType = mSubType;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mID);
        dest.writeSerializable(mDate);
        dest.writeSerializable(mTime);
        dest.writeInt(mType);
        dest.writeInt(mSubType);
        dest.writeString(mDescription);
    }
}
