package wtf.yawn.yawnchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {

    //region Fields

    public static final String TAG = ChatActivity.class.getCanonicalName();
    public static final String CHAT_KEY_EXTRA_ID = "wtf.yawn.yawnchat.CHAT-KEY";

    @BindView(R.id.messagesRecycler)
    public RecyclerView mUiMessagesRecycler;

    public FirebaseRecyclerAdapter<Message, MessageViewHolder> messagesAdapter;

    @BindView(R.id.sendButton)
    public Button mUiSendButton;

    @BindView(R.id.messageEditText)
    public EditText mUiMessageEditText;

    private String mChatKey;

    //endregion Fields

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra(CHAT_KEY_EXTRA_ID) != null){
            mChatKey = getIntent().getStringExtra(CHAT_KEY_EXTRA_ID);
        }
        setUpAdapterAndRecycler();
        mUiSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(mUiMessageEditText.getText().toString(), YawnChatApplication.getUserObject().uid);
                FirebaseDatabase.getInstance().getReference().child("chats-messages/"+mChatKey).push().setValue(message);
                mUiMessageEditText.setText("");
            }
        });
        mUiMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mUiSendButton.setEnabled((charSequence.toString().trim().length() > 0));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setUpAdapterAndRecycler() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DatabaseReference messagesRef = YawnChatApplication.getDbReference().child("chats-messages/"+mChatKey);
        messagesAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class,
                R.layout.item_message, MessageViewHolder.class, messagesRef) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                Log.d(TAG, "populateViewHolder() called with: " + "viewHolder = [" + viewHolder + "], model = [" + model + "], position = [" + position + "]");
                viewHolder.bind(model, Objects.equals(model.user, YawnChatApplication.getUserObject().uid));
            }
        };
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messagesCount = messagesAdapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messagesCount - 1) && lastVisiblePosition == (positionStart -1 ))){
                    mUiMessagesRecycler.scrollToPosition(positionStart);
                }
            }
        });
        mUiMessagesRecycler.setLayoutManager(layoutManager);
        mUiMessagesRecycler.setAdapter(messagesAdapter);
    }

    public static Intent getCallingIntent(Context context, String chatKey){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(CHAT_KEY_EXTRA_ID, chatKey);
        return intent;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.message)
        public TextView mUiMessage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Message message, boolean belongsToUser){
            mUiMessage.setText(message.message);
            RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) mUiMessage.getLayoutParams();
            int align = belongsToUser ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT;
            layout.addRule(align);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
