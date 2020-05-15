package com.example.tickit.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Classes.AssumedTasksSituation;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.Classes.ProjectTask;
import com.example.tickit.R;
import com.example.tickit.RecyclerViewAdapters.TasksAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ListViewTasksAdapter extends ArrayAdapter<ProjectTask> {
    private int resourceID;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private TextView taskName, projectName, deadline, assumptionStatusTextView;
    private ProjectTask task;
   // private String project;
    private ArrayList<String> projectNames = new ArrayList<>();
    View view;

    public ListViewTasksAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ProjectTask> objects, ArrayList<String> projectName) {
        super(context, resource, objects);
        this.resourceID = resource;
        //this.projectNames.addAll(projectName);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        task = getItem(position);
        //project = projectNames.get(position);
        view = LayoutInflater.from(getContext()).inflate(resourceID,null);
        assignViews();

        taskName.setText(task.getTaskName());
        assumptionStatusTextView.setText(task.getMembersWhoAssumed().size()+"/" + task.getNumberOfVolunteers() + " voluntari");

        String date = dateFormat.format(task.getStopDate());
        deadline.setText(date);
       // projectName.setText(project);


        setColorsOfDeadlines(task);
        return view;
    }

    private void setColorsOfDeadlines(ProjectTask task){
        if(task.getMembersWhoAssumed()!=null && task.getMembersWhoAssumed().size() == task.getNumberOfVolunteers()
                && hasEveryoneFinished(task.getMembersWhoAssumed())
                && MainActivity.getContext()!=null){
            deadline.setTextColor(ContextCompat.getColor(MainActivity.getContext(),R.color.green));
        }

        if( havePassedThreeQuartersOfTime(task) && !hasEveryoneFinished(task.getMembersWhoAssumed()) && MainActivity.getContext()!=null){
           deadline.setTextColor(ContextCompat.getColor(MainActivity.getContext(),R.color.brownish_yellow));
        }

        if( Calendar.getInstance().getTime().after(task.getStopDate()) && !hasEveryoneFinished(task.getMembersWhoAssumed()) && MainActivity.getContext()!=null){
            deadline.setTextColor(ContextCompat.getColor(MainActivity.getContext(),R.color.red));
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
            if(situation.getProgress()<2 &&  MainActivity.getLoggedInUser() !=null && situation.getUser().getEmail().equals(MainActivity.getLoggedInUser().getEmail())){
                return false;
            }
        }
        return true;
    }

    private void assignViews() {
        taskName = (TextView) view.findViewById(R.id.textViewTaskNameCard);
        projectName = (TextView) view.findViewById(R.id.textViewProjectNameCard);
        deadline = (TextView) view.findViewById(R.id.textViewDeadlineCard);
        assumptionStatusTextView = (TextView) view.findViewById(R.id.assumptionStatusTextView);
    }

}


