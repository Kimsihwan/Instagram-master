package com.example.test.instagram.Likes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.test.instagram.Home.ChatActivity;
import com.example.test.instagram.Profile.ProfileActivity;
import com.example.test.instagram.R;
import com.example.test.instagram.Utils.BottomNavigationViewHelper;
import com.example.test.instagram.Utils.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikesActivity extends AppCompatActivity {

    private static final String TAG = "LikesActivity";
    private static final int ACTIVITY_NUM = 3;

    private RecyclerView mFriendsList;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mUsersettingDB;

    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;

    private Context mContext = LikesActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Log.d(TAG, "onCreate: stared.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //mFriendsList = mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        try {
            mCurrent_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("following").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);



        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUserDatabase.keepSynced(true);

        mUsersettingDB = FirebaseDatabase.getInstance().getReference().child("users_account_settings");

        mUsersettingDB.keepSynced(true);




        //mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(mContext));
       setupBottomNavigationView();
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        FirebaseRecyclerAdapter<Friends, FriendViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendViewHolder>(

                Friends.class,
                R.layout.users_single_layout,
                FriendViewHolder.class,
                mFriendsDatabase


        ) {
            @Override
            protected void populateViewHolder(final FriendViewHolder friendViewHolder, Friends friends, int i) {
                friendViewHolder.setDate(friends.getDate());

                final String list_user_id = getRef(i).getKey();

                mUsersettingDB.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userThumb = dataSnapshot.child("profile_photo").getValue().toString();
                        friendViewHolder.setUserImage(userThumb, mContext);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("username").getValue().toString();
                        //String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {
                            String  userOnline = dataSnapshot.child("online").getValue().toString();
                            friendViewHolder.setUserOnline(userOnline);
                        }

                        friendViewHolder.setName(userName);
                        //friendViewHolder.setUserImage(userThumb, getContext());

                        friendViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{"프로필 보기", "메시지 보내기"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                builder.setTitle("옵션 선택");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        // click event for each item.
                                        if(i == 0) {
                                            Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                                            profileIntent.putExtra("user_id", list_user_id);
                                            startActivity(profileIntent);

                                        }
                                        if(i == 1) {
                                            Intent chatIntent = new Intent(mContext, ChatActivity.class);
                                            chatIntent.putExtra("user_id", list_user_id);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);
                                        }
                                    }
                                });

                                builder.show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mFriendsList.setAdapter(friendsRecyclerViewAdapter);
    }





    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDate(String date) {

            TextView userStatusView = mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);

        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.ic_android).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = mView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true")) {

                userOnlineView.setVisibility(View.VISIBLE);

            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }


    }




    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
