package com.example.tickit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tickit.Callbacks.CallbackString;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListViewTasksAdapter extends ArrayAdapter<ProjectTask> {
    int resourceID;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public ListViewTasksAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ProjectTask> objects) {
        super(context, resource, objects);
        this.resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ProjectTask task = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(resourceID,null);

        TextView taskName = (TextView) view.findViewById(R.id.textViewTaskNameCard);
        final TextView projectName = (TextView) view.findViewById(R.id.textViewProjectNameCard);
        TextView deadline = (TextView) view.findViewById(R.id.textViewDeadlineCard);

        taskName.setText(task.getTaskName());

        String date = dateFormat.format(task.getStopDate());
        deadline.setText(date);

        getProjName(task.getProject(), new CallbackString() {
            @Override
            public void onCallBack(String value) {
                projectName.setText(value);
            }
        });
        return view;
    }

    private void getProjName(DocumentReference docRef, final CallbackString callback) {
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String value = documentSnapshot.getString("name");
                    callback.onCallBack(value);
                } else {
                    Log.d("checkRef", "No such document");
                }
            }
        });
    }
}


