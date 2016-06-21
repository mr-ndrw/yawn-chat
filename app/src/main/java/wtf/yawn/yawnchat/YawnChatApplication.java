package wtf.yawn.yawnchat;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Andrew on 21.06.2016.
 */
public class YawnChatApplication extends Application {

    private static FirebaseUser user;
    private static FirebaseDatabase database;

    public static FirebaseUser getUser() {
        if (checkIfSignedIn()){
            user = FirebaseAuth.getInstance().getCurrentUser();
        }
        return user;
    }

    public static User getUserObject(){
        User userObject = new User();
        userObject.email = user.getEmail();
        userObject.uid = user.getUid();
        userObject.username = user.getDisplayName();
        userObject.photoUrl = user.getPhotoUrl() != null
                ? user.getPhotoUrl().toString()
                : null;
        return userObject;
    }

    public static void setUser(FirebaseUser user) {
        YawnChatApplication.user = user;
    }

    public static boolean checkIfSignedIn(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static FirebaseDatabase getDatabase() {
        if (database == null){
            database = FirebaseDatabase.getInstance();
        }
        return database;
    }

    public static DatabaseReference getDbReference() {
        return getDatabase().getReference();
    }

    public static DatabaseReference getDbReference(String s){
        return getDatabase().getReference(s);
    }
}
