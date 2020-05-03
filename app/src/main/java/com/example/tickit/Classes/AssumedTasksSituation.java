package com.example.tickit.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class AssumedTasksSituation implements Parcelable {
    private User user;
    private int progress;
    private boolean isNeedingHelp;

    public AssumedTasksSituation(){}

    public AssumedTasksSituation(User user, int progress, boolean isNeedingHelp) {
        this.user = user;
        this.progress = progress;
        this.isNeedingHelp = isNeedingHelp;
    }

    protected AssumedTasksSituation(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        progress = in.readInt();
        isNeedingHelp = in.readByte() != 0;
    }

    public static final Creator<AssumedTasksSituation> CREATOR = new Creator<AssumedTasksSituation>() {
        @Override
        public AssumedTasksSituation createFromParcel(Parcel in) {
            return new AssumedTasksSituation(in);
        }

        @Override
        public AssumedTasksSituation[] newArray(int size) {
            return new AssumedTasksSituation[size];
        }
    };

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isNeedingHelp() {
        return isNeedingHelp;
    }

    public void setNeedingHelp(boolean needingHelp) {
        isNeedingHelp = needingHelp;
    }

    @Override
    public String toString() {
        return "AssumedTasksSituation{" +
                "user=" + user +
                ", progress=" + progress +
                ", isNeedingHelp=" + isNeedingHelp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeInt(progress);
        dest.writeByte((byte) (isNeedingHelp ? 1 : 0));
    }
}
