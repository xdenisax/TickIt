package com.example.tickit;

import android.content.Context;
import android.graphics.Color;
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
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListViewTasksAdapter extends ArrayAdapter<ProjectTask> {
    private int resourceID;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private TextView taskName, projectName, deadline, assumptionStatusTextView;
    private ProjectTask task;
    View view;

    public ListViewTasksAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ProjectTask> objects) {
        super(context, resource, objects);
        this.resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        task = getItem(position);
        view = LayoutInflater.from(getContext()).inflate(resourceID,null);
        assignViews();

        taskName.setText(task.getTaskName());
        assumptionStatusTextView.setText(task.getMembersWhoAssumed().size()+"/" + task.getNumberOfVolunteers() + " voluntari");

        String date = dateFormat.format(task.getStopDate());
        deadline.setText(date);

        ProjectDatabaseCalls.getProjName(task.getProject(), new CallbackString() {
            @Override
            public void onCallBack(String value) {
                projectName.setText(value);
                Log.d("divisionChek",task.getTaskName() + value);
            }
        });

        setColorsAccordingly();

        return view;
    }

    private void setColorsAccordingly() {

        if(task.getStopDate().getTime() < System.currentTimeMillis()){
            if(isFullyImplemented(task) && task.getMembersWhoAssumed().size()>0){
                setColor(view.getResources().getColor(R.color.transparent_vivid_cyan));
            }else{
                setColor(view.getResources().getColor(R.color.red));
            }
        }else{
            if(isFullyImplemented(task) && task.getMembersWhoAssumed().size()>0){
                setColor(view.getResources().getColor(R.color.green));
            }
        }
    }

    private boolean isFullyImplemented(ProjectTask task) {
        boolean isFullyImplemented = true;
        for(AssumedTasksSituation situation : task.getMembersWhoAssumed()){
            if(situation.getProgress()<2){
                isFullyImplemented=false;
            }
        }
        return isFullyImplemented;
    }

    private void assignViews() {
        taskName = (TextView) view.findViewById(R.id.textViewTaskNameCard);
        projectName = (TextView) view.findViewById(R.id.textViewProjectNameCard);
        deadline = (TextView) view.findViewById(R.id.textViewDeadlineCard);
        assumptionStatusTextView = (TextView) view.findViewById(R.id.assumptionStatusTextView);
    }

    private void setColor(int color) {
        taskName.setTextColor(color);
        projectName.setTextColor(color);
        deadline.setTextColor(color);
        assumptionStatusTextView.setTextColor(color);
    }


}


