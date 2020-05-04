package com.example.tickit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tickit.R;
import com.example.tickit.Classes.User;

import java.util.ArrayList;

public class ListViewMemberAdapter extends ArrayAdapter<User> implements Filterable {
    int resourceID;
    private ArrayList<User> mOriginalValues; // Original Values
    private ArrayList<User> mDisplayedValues;    // Values to be displayed
    private ArrayList<User> filteredArrayList = new ArrayList<User>();

    public ListViewMemberAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects)  {
        super(context, resource, objects);
        this.resourceID = resource;
        mOriginalValues = objects;
        mDisplayedValues = objects;
    }

    @Override
    public int getCount() {
        return mDisplayedValues.size();
    }

    @Override
    public User getItem(int position) {
        return mDisplayedValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       User user = mDisplayedValues.get(position);
       LayoutInflater inflater = LayoutInflater.from(getContext());
       View view = inflater.inflate(resourceID,null);

        ImageView profilImageView = (ImageView) view.findViewById(R.id.userPicture);
        TextView numeTextView =(TextView) view.findViewById(R.id.numeTextView);


        if(user.getLastName()!=null || user.getFirstName()!= null){
            Glide.with(getContext()).load(user.getProfilePicture()).apply(RequestOptions.circleCropTransform()).into(profilImageView);
            numeTextView.setText(user.getLastName() + " " + user.getFirstName());
        }else{
            Glide.with(getContext()).load(R.drawable.account_cyan).apply(RequestOptions.centerCropTransform()).into(profilImageView);
            numeTextView.setText(user.getEmail());
        }

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                filteredArrayList = new ArrayList<User>();
                if(mOriginalValues==null){
                    mOriginalValues = new ArrayList<User>(mDisplayedValues);
                }
                if (constraint == null || constraint.length() == 0) {
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).getEmail();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            filteredArrayList.add(new User(mOriginalValues.get(i).getFirstName(), mOriginalValues.get(i).getLastName(), mOriginalValues.get(i).getPhoneNumber(), mOriginalValues.get(i).getEmail(), mOriginalValues.get(i).getProfilePicture(),mOriginalValues.get(i).getDepartament(), mOriginalValues.get(i).getMandates()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = filteredArrayList.size();
                    results.values = filteredArrayList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDisplayedValues = (ArrayList<User>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

        };
        return filter;
    }
}
