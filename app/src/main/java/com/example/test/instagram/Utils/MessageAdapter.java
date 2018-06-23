package com.example.test.instagram.Utils;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
          View  v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;
        public TextView displaytime;
        public LinearLayout linearLayout_destination;
        public RelativeLayout relativeLayout_message_single_layout;

        public MessageViewHolder(View view) {
            super(view);

            displaytime = (TextView) view.findViewById(R.id.time_text_layout);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);

            relativeLayout_message_single_layout = (RelativeLayout) view.findViewById(R.id.message_single_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        mAuth = FirebaseAuth.getInstance();

        String current_id = mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(i);
        String from_user = c.getFrom();
        String message_type = c.getType();

        if(!from_user.equals(current_id)){  //내가 아니라면
            viewHolder.messageText.setTextColor(Color.BLACK);
            viewHolder.messageText.setBackgroundResource(R.drawable.leftbubble);
            viewHolder.profileImage.setVisibility(View.VISIBLE);
            viewHolder.displayName.setVisibility(View.VISIBLE);
            viewHolder.displaytime.setVisibility(View.VISIBLE);
            viewHolder.relativeLayout_message_single_layout.setGravity(Gravity.LEFT);

        }else {
            viewHolder.displaytime.setVisibility(View.INVISIBLE);
            viewHolder.displayName.setVisibility(View.INVISIBLE);
            viewHolder.profileImage.setVisibility(View.INVISIBLE);
            viewHolder.messageText.setBackgroundResource(R.drawable.rightbubble);
            viewHolder.messageText.setTextColor(Color.BLACK);
            viewHolder.relativeLayout_message_single_layout.setGravity(Gravity.RIGHT);
        }


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(from_user);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 String name = dataSnapshot.child("username").getValue().toString();
                 viewHolder.displayName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users_account_settings").child(from_user);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("profile_photo").getValue().toString();
               Picasso.get().load(image).placeholder(R.drawable.ic_android).into(viewHolder.profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

            viewHolder.messageText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
