package wtf.yawn.yawnchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    //region Fields

    public static final String TAG = MainActivity.class.getCanonicalName();

    @BindView(R.id.chatsRecycler)
    public RecyclerView mUiChatsRecycler;
    private FirebaseRecyclerAdapter<String, ChatItemViewHolder> mChatItemsAdapter;
    private Context mContext;
    //endregion Fields

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!YawnChatApplication.checkIfSignedIn()) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        ButterKnife.bind(this);
        mContext = this;
        setUpRecycler();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ChooseUserActivity.getCallingIntent(mContext));
            }
        });
    }

    private void setUpRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DatabaseReference chatsRef = YawnChatApplication.getDbReference()
                .child("users/"+YawnChatApplication.getUser().getUid()+"/chats");
        mChatItemsAdapter = new FirebaseRecyclerAdapter<String, ChatItemViewHolder>(String.class,
                R.layout.item_chat, ChatItemViewHolder.class, chatsRef) {
            @Override
            protected void populateViewHolder(final ChatItemViewHolder viewHolder, String model, int position) {
                Log.d(TAG, "populateViewHolder() called with: " + "viewHolder = [" + viewHolder + "], model = [" + model + "], position = [" + position + "]");
                DatabaseReference oneChatRef = YawnChatApplication.getDbReference().child("chats/"+model);
                oneChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: ");
                        final Chat chat = dataSnapshot.getValue(Chat.class);
                        chat.key = dataSnapshot.getKey();
                        int currentUsersIndex = chat.users.indexOf(YawnChatApplication.getUserObject().uid);
                        int chattingUserKeyIndex = currentUsersIndex == 0 ? 1 : 0;
                        final String chattingUserKey = chat.users.get(chattingUserKeyIndex);
                        YawnChatApplication.getDbReference("users/" + chattingUserKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                viewHolder.bind(mContext, user, chat);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        };
        mUiChatsRecycler.setLayoutManager(layoutManager);
        mUiChatsRecycler.setAdapter(mChatItemsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public static class ChatItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.chatWithUser)
        public TextView mUiChatWithUser;

        @BindView(R.id.dateStarted)
        public TextView mUiDateStarted;

        private String mChatKey;

        public ChatItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Context context, User user, Chat chat){
            mChatKey = chat.key;
            mUiChatWithUser.setText(user.username);
            mUiDateStarted.setText(chat.getNiceDate(context));
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: ");
            Intent intent = ChatActivity.getCallingIntent(view.getContext(), mChatKey);
            view.getContext().startActivity(intent);
        }
    }
}
