package com.example.tickit.RecyclerViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.R;
import com.example.tickit.Utils.DateProcessing;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MandatesAdapter extends FirestoreRecyclerAdapter<Mandate, MandatesAdapter.MandateHolder> {

    public MandatesAdapter(@NonNull FirestoreRecyclerOptions<Mandate> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MandateHolder holder, int position, @NonNull final Mandate model) {
        ProjectDatabaseCalls.getProjectName(model.getProject_name(), new CallbackString() {
            @Override
            public void onCallBack(String value) {
                holder.projectNameTextView.setText(value);
            }
        });
        String endDate =DateProcessing.dateFormat.format(model.getStop_date());
        holder.editionTextView.setText(endDate.substring(endDate.length()-4));
        holder.positionTextView.setText(model.getPosition());
    }

    @NonNull
    @Override
    public MandateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mandate_card, parent, false);
        return new  MandateHolder(v);
    }

    class MandateHolder extends RecyclerView.ViewHolder{
        TextView positionTextView;
        TextView editionTextView;
        TextView projectNameTextView;

        public MandateHolder(@NonNull View itemView) {
            super(itemView);
            positionTextView = itemView.findViewById(R.id.positionTextView);
            editionTextView = itemView.findViewById(R.id.editionTextView);
            projectNameTextView= itemView.findViewById(R.id.projectNameTextView);
        }
    }
}
