package com.example.tickit.RecyclerViewAdapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Classes.AssumedTasksSituation;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.ProjectTask;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.Fragments.OpenTasks;
import com.example.tickit.R;
import com.example.tickit.Utils.DateProcessing;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class TasksAdapter extends FirestoreRecyclerAdapter<ProjectTask, TasksAdapter.TaskHolder> {

    OnItemLongClickListener longClickListener;
    OnItemClickListener listener;

    public TasksAdapter(@NonNull FirestoreRecyclerOptions<ProjectTask> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final TaskHolder holder, int position, @NonNull ProjectTask model) {
        ProjectDatabaseCalls.getProjectName(model.getProject(), new CallbackString() {
            @Override
            public void onCallBack(String value) {
                holder.projectName.setText(value);
            }
        });
        String assumptionStatus = model.getMembersWhoAssumed().size() +"/"+model.getNumberOfVolunteers() + " si-au asumat";
        holder.assumptionStatusTextView.setText(assumptionStatus);
        holder.deadline.setText(DateProcessing.dateFormat.format(model.getStopDate()));
        holder.taskName.setText(model.getTaskName());
        setColorsOfDeadlines(model, holder);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

        getSnapshots().addChangeEventListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                if(type.equals(ChangeEventType.CHANGED) && isTaskForLoggedInUser(snapshot)){
                    OpenTasks.manageNotification(snapshot.toObject(ProjectTask.class));
                }
            }

            @Override
            public void onDataChanged() {

            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {

            }
        });
    }

    private boolean isTaskForLoggedInUser(DocumentSnapshot snapshot) {
        ProjectTask task = snapshot.toObject(ProjectTask.class);
        if(task!=null){
            if(!task.getDivision().equals(MainActivity.getLoggedInUser().getDepartament())){
                Log.d("mandate", task.getDivision());
                return false;
            }
            for (final Map.Entry<String, Mandate>  mandateEntry: MainActivity.getCurrentMandates().entrySet()){
                if(mandateEntry.getKey().contains("BE-BC") || mandateEntry.getValue().getProject_name().equals(task.getProject())){
                    Log.d("mandate", task.getProject().toString());
                    return true;
                }
            }
            return false;
        }else{
            Log.d("mandate", "null");
            return false;
        }
    }

    @NonNull
    @Override
    public TasksAdapter.TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card, parent, false);
        return new TaskHolder(v);
    }

    class TaskHolder extends RecyclerView.ViewHolder{
        private TextView taskName, projectName, deadline, assumptionStatusTextView;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            taskName = (TextView) itemView.findViewById(R.id.textViewTaskNameCard);
            projectName = (TextView) itemView.findViewById(R.id.textViewProjectNameCard);
            deadline = (TextView) itemView.findViewById(R.id.textViewDeadlineCard);
            assumptionStatusTextView = (TextView) itemView.findViewById(R.id.assumptionStatusTextView);

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
        void onItemLongClick(DocumentSnapshot taskSnapshot, int position);
    }

    public void setOnItemLongClickListener(TasksAdapter.OnItemLongClickListener listener){
        this.longClickListener=listener;
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot taskSnapshot, int position);
    }

    public void setOnItemClickListener(TasksAdapter.OnItemClickListener listener){
        this.listener=listener;
    }

    private void setColorsOfDeadlines(ProjectTask task, TaskHolder holder){
        if(task.getMembersWhoAssumed()!=null && task.getMembersWhoAssumed().size() == task.getNumberOfVolunteers()
                && hasEveryoneFinished(task.getMembersWhoAssumed())
                && MainActivity.getContext()!=null){
            holder.deadline.setTextColor(ContextCompat.getColor(MainActivity.getContext(),R.color.green));
        }

        if( havePassedThreeQuartersOfTime(task) && !hasEveryoneFinished(task.getMembersWhoAssumed()) && MainActivity.getContext()!=null){
            holder.deadline.setTextColor(ContextCompat.getColor(MainActivity.getContext(),R.color.brownish_yellow));
        }

        if( Calendar.getInstance().getTime().after(task.getStopDate()) && !hasEveryoneFinished(task.getMembersWhoAssumed()) && MainActivity.getContext()!=null){
            holder.deadline.setTextColor(ContextCompat.getColor(MainActivity.getContext(),R.color.red));
        }
    }
    private boolean havePassedThreeQuartersOfTime(ProjectTask task){
        Log.d("mandate", task.getTaskName()+ " " +(task.getStopDate().getTime()-task.getStartDate().getTime()/4)+" "+ (task.getStopDate().getTime() - Calendar.getInstance().getTime().getTime()) + " "+ (task.getStopDate().before(Calendar.getInstance().getTime())) );
        return (task.getStopDate().getTime()-task.getStartDate().getTime())/4 > (task.getStopDate().getTime() - Calendar.getInstance().getTime().getTime()) && (task.getStopDate().getTime() - Calendar.getInstance().getTime().getTime()>0);
    }

    private boolean hasEveryoneFinished(ArrayList<AssumedTasksSituation> membersWhoAssumed) {
        if(membersWhoAssumed ==null|| membersWhoAssumed.size()<1){
            return false;
        }
        for (AssumedTasksSituation situation : membersWhoAssumed){
            if(situation.getProgress()<2){
                return false;
            }
        }
        return true;
    }
}
