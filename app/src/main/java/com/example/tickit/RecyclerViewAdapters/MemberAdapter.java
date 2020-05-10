package com.example.tickit.RecyclerViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tickit.Classes.User;
import com.example.tickit.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class MemberAdapter extends FirestoreRecyclerAdapter<User, MemberAdapter.MemberHolder> {

    OnItemLongClickListener longClickListener;
    OnItemClickListener listener;
    public MemberAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MemberHolder holder, int position, @NonNull User model) {
        if(model.getFirstName()==null){
            holder.userName.setText(model.getEmail());
        }else{
            holder.userName.setText(model.getLastName()+ " " + model.getFirstName());
        }
        if(model.getProfilePicture()==null){
            Glide.with(holder.itemView.getContext()).load(R.drawable.account_cyan).apply(RequestOptions.circleCropTransform()).into(holder.profilePicture);
        }else{
            Glide.with(holder.itemView.getContext()).load(model.getProfilePicture()).apply(RequestOptions.circleCropTransform()).into(holder.profilePicture);
        }
     }

    @NonNull
    @Override
    public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.member_cardview, parent,false);
        return new MemberHolder(v);
    }

    class MemberHolder extends RecyclerView.ViewHolder{
        ImageView profilePicture;
        TextView userName;
        public MemberHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = (ImageView) itemView.findViewById(R.id.userPicture);
            userName= (TextView) itemView.findViewById(R.id.nameTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION && longClickListener!=null){
                        longClickListener.onItemLongClick(getSnapshots().getSnapshot(getAdapterPosition()), getAdapterPosition());
                    }
                    return true;
                }
            });
        }
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(DocumentSnapshot memberReference, int position);
    }

    public void setOnItemLongClickListener(MemberAdapter.OnItemLongClickListener listener){
        this.longClickListener=listener;
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot memberReference, int position);
    }

    public void setOnItemClickListener(MemberAdapter.OnItemClickListener listener){
        this.listener=listener;
    }
}
