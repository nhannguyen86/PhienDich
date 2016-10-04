package vn.nhan.phiendich.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nhan on 3/10/2016.
 */
public class Detail {
    public long id;
    public String content;
    public String title;
    public String typename;
    @SerializedName("audio_file")
    public String audioFile;
    @SerializedName("audio_length")
    public String audioLength;
}
