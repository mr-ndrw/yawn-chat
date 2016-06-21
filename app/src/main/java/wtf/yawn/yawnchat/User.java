package wtf.yawn.yawnchat;

import com.google.firebase.database.Exclude;

/**
 * Created by Andrew on 21.06.2016.
 */
public class User {
    @Exclude
    public String uid;
    public String username;
    public String email;
    public String photoUrl;
}
