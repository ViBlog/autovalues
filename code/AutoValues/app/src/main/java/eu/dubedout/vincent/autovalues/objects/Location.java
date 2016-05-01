package eu.dubedout.vincent.autovalues.objects;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Location implements Parcelable{
    public abstract double latitude();
    public abstract double longitude();

}
