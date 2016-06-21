package wtf.yawn.yawnchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseUserActivity
        extends AppCompatActivity
        implements ActivityFinisher {

    //region Fields

    private static final String TAG = ChooseUserActivity.class.getCanonicalName();

    @BindView(R.id.usersRecycler)
    public RecyclerView mUiUsersRecycler;

    private FirebaseRecyclerAdapter<User, UserItemViewHolder> mUsersAdapter;

    //endregion Fields

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);
        ButterKnife.bind(this);
        setUpAdapterAndRecycler();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    private void setUpAdapterAndRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DatabaseReference usersRef = YawnChatApplication.getDbReference().child("/users");
        mUsersAdapter = new FirebaseRecyclerAdapter<User, UserItemViewHolder>(User.class,
                R.layout.item_user, UserItemViewHolder.class, usersRef) {
            @Override
            protected void populateViewHolder(UserItemViewHolder viewHolder, User model, int position) {
                String currentKey = mUsersAdapter.getRef(position).getKey();
                if (currentKey.equals(YawnChatApplication.getUserObject().uid)){
                    viewHolder.itemView.setVisibility(View.GONE);
                    return;
                }
                model.uid = currentKey;
                viewHolder.bind(model);
                viewHolder.bindActivityFinisher(ChooseUserActivity.this);
            }
        };
        mUiUsersRecycler.setLayoutManager(layoutManager);
        mUiUsersRecycler.setAdapter(mUsersAdapter);
    }

    public static Intent getCallingIntent(Context ctx){
        Intent intent = new Intent(ctx, ChooseUserActivity.class);
        return intent;
    }

    public static class UserItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.userName)
        public TextView mUiUserName;
        private User user;

        private ActivityFinisher mActivityFinisher;

        public UserItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(User user){
            this.user = user;
            mUiUserName.setText(user.username);
        }

        public void bindActivityFinisher(ActivityFinisher activityFinisher){
            mActivityFinisher = activityFinisher;
        }

        @Override
        public void onClick(View view) {
            List<String> users = new ArrayList<>(Arrays.asList(YawnChatApplication.getUserObject().uid, user.uid));
            Chat chat = new Chat("", users);
            DatabaseReference newChatRef = YawnChatApplication.getDatabase().getReference().child("chats").push();
            String newChatKey = newChatRef.getKey();
            String myUid = YawnChatApplication.getUserObject().uid;
            YawnChatApplication.getDatabase().getReference().child("users/"+myUid+"/chats").push().setValue(newChatKey);
            YawnChatApplication.getDatabase().getReference().child("users/"+user.uid+"/chats").push().setValue(newChatKey);
            newChatRef.setValue(chat);
            mActivityFinisher.finishActivity();
            Log.d(TAG, "onClick: user's current id = " +  YawnChatApplication.getUserObject().uid);
            Log.d(TAG, "onClick: other users's id = " + user.uid);
        }
    }
}
