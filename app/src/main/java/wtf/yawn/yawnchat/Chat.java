package wtf.yawn.yawnchat;

import android.content.Context;
import android.text.format.DateUtils;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.List;

/**
 * Created by Andrew on 21.06.2016.
 */
public class Chat {
    @Exclude
    public String key;
    public List<String> users;
    public Long dateStarted;

    public Chat(){}

    public Chat(String key, List<String> users){
        this.key = key;
        this.users = users;
        dateStarted = new Date().getTime();
    }

    public String getNiceDate(Context context){
        return DateUtils.getRelativeTimeSpanString(context, dateStarted, true).toString();
    }
}
