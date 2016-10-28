package vn.nhan.phiendich.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nhan on 3/10/2016.
 */

public class Scheduler {
    public int id;
    public String title;
    public String description;
    @SerializedName("readingdate")
    public String readingDate;
    public Detail[] details;
}
