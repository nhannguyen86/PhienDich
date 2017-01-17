package vn.nhan.phiendich.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nhan on 29/9/2016.
 */

public class AuthenticationModel extends BaseModel {
    public long id;
    public String username;
    public String pass;
    public String email;
    public String displayname;
    public String firstname;
    public String lastname;
    public String avatar;
    @SerializedName("is_donated")
    public boolean isDonated;
}
