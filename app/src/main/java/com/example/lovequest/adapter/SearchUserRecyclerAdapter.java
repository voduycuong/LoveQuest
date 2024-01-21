package com.example.lovequest.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovequest.ChatActivity;
import com.example.lovequest.R;
import com.example.lovequest.model.UserModel;
import com.example.lovequest.utils.AndroidUtil;
import com.example.lovequest.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        String username = model.getUsername();
        String userId = model.getUserId();
        String currentUserId = FirebaseUtil.getCurrentUserId();

        // Set the username text
        if (username != null) {
            holder.usernameText.setText(username);
            if (userId != null && userId.equals(currentUserId)) {
                holder.usernameText.setText(username + " (Me)");
            }
        }

        // Load profile image
        if (userId != null) {
            FirebaseUtil.getOtherUserPhotoRef(userId).getDownloadUrl()
                    .addOnCompleteListener(t -> {
                        if (t.isSuccessful() && t.getResult() != null) {
                            Uri uri = t.getResult();
                            AndroidUtil.loadProfileImage(context, uri, holder.profilePic);
                        }
                    });
        }

        // Set item click listener
        if (userId != null && model != null) {
            holder.itemView.setOnClickListener(v -> {
                // Navigate to chat activity
                Intent intent = new Intent(context, ChatActivity.class);
                AndroidUtil.transferUserData(intent, model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
        }
    }


    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
