package br.com.aaascp.gerenciadordepedidos.repository.utils.filter;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Pair;


/**
 * Created by andre on 19/09/17.
 */

final public class OrderFilter implements Parcelable {

    public enum Status {
        PROCESSED,
        TO_PROCESS,
        ALL
    }

    private String id;

    @Nullable
    private DateRange period;
    private Status status;

    public static final Parcelable.Creator<OrderFilter> CREATOR =
            new Parcelable.Creator<OrderFilter>() {
                public OrderFilter createFromParcel(Parcel source) {
                    return new OrderFilter(source);
                }

                public OrderFilter[] newArray(int size) {
                    return new OrderFilter[size];
                }
            };


    public OrderFilter(
            String id,
            DateRange period,
            Status status) {

        this.id = id;
        this.period = period;
        this.status = status;
    }

    private OrderFilter(Parcel in) {
        this.id = in.readString();
        this.period = in.readParcelable(DateRange.class.getClassLoader());
        this.status = (Status) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeParcelable(period, 0);
        out.writeSerializable(status);
    }

    public Pair<Boolean, String> getId() {
        return new Pair<Boolean, String>(
                id != null,
                id);
    }

    public Pair<Boolean, DateRange> getPeriod() {
        return new Pair<Boolean, DateRange>(
                period != null,
                period);
    }

    public Pair<Boolean, Status> getStatus() {
        return new Pair<Boolean, Status>(
                status != null,
                status);
    }

    static final public class Builder {

        private String id;
        private DateRange period;
        private Status status;

        public Builder() {
            this.id = "";
            this.period = null;
            this.status = Status.ALL;
        }

        public OrderFilter build() {
            return new OrderFilter(
                    id,
                    period,
                    status);
        }

        public Builder id(String value) {
            this.id = value;
            return this;
        }

        public Builder period(DateRange value) {
            this.period = value;
            return this;
        }

        public Builder status(Status value) {
            this.status = value;
            return this;
        }
    }
}


