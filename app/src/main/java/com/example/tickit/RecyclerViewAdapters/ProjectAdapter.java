package com.example.tickit.RecyclerViewAdapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tickit.Activities.EditionProfile;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Classes.Project;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.DataBaseCalls.UserDatabaseCalls;
import com.example.tickit.PopUps.AddStrategyPopUp;
import com.example.tickit.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;

public class ProjectAdapter extends FirestoreRecyclerAdapter<Project, ProjectAdapter.ProjectHolder> {
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public ProjectAdapter(@NonNull FirestoreRecyclerOptions<Project> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ProjectHolder holder, int position, @NonNull final Project model) {
        holder.nameTextView.setText(model.getName());
        if(model.getImageLink()!=null){
            ProjectDatabaseCalls.getPhotoUri(model.getImageLink(), new CallbackString() {
                @Override
                public void onCallBack(String value) {
                    Uri photoUri = Uri.parse(value);
                    Glide.with(holder.itemView.getContext()).load(photoUri).apply(RequestOptions.circleCropTransform().centerInside()).into(holder.profileImageView);
                    model.setImageLink(photoUri.toString());
                }
            });
        }
    }

    @NonNull
    @Override
    public ProjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_card, parent, false);
        return new ProjectHolder(v);
    }


    class ProjectHolder extends RecyclerView.ViewHolder{
        ImageView profileImageView;
        TextView nameTextView;

        public ProjectHolder(@NonNull final View itemView) {
            super(itemView);
            profileImageView = (ImageView) itemView.findViewById(R.id.userPicture);
            nameTextView = (TextView) itemView.findViewById(R.id.numeTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(getAdapterPosition()).getReference(), getAdapterPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION && longClickListener!=null){
                        longClickListener.onItemLongClick(getSnapshots().getSnapshot(getAdapterPosition()).getReference(), getAdapterPosition());
                    }
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentReference projectReference, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(DocumentReference projectReference, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.longClickListener=listener;
    }
}
