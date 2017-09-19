package br.com.aaascp.gerenciadordepedidos.repository.utils.filter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by andre on 19/09/17.
 */

public class DateRange implements Parcelable {

    private Date start;
    private Date end;

    public static final Parcelable.Creator<DateRange> CREATOR =
            new Parcelable.Creator<DateRange>() {
                @Override
                public DateRange createFromParcel(Parcel source) {
                    return new DateRange(source);
                }

                @Override
                public DateRange[] newArray(int size) {
                    return new DateRange[size];
                }
            };

    public DateRange(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    private DateRange(Parcel in) {
        this.start = (Date) in.readSerializable();
        this.end = (Date) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(start);
        dest.writeSerializable(end);
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}
