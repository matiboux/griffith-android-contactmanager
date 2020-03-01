package com.matiboux.griffith.contactmanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ListContactsAdapter extends ArrayAdapter<ContactInfo> implements View.OnClickListener {

    private List<ContactInfo> objects;
    Context context;

    public ListContactsAdapter(@NonNull Context context, @NonNull List<ContactInfo> objects) {
        super(context, R.layout.adapter_list_contacts, objects);
        this.context = context;
        this.objects = objects;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txv_list_contacts;
    }

    @Override
    public void onClick(View view) {
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        // Get the data item for this position
        ContactInfo dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        //final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_list_contacts, parent, false);

            viewHolder.txv_list_contacts = convertView.findViewById(R.id.txv_list_contacts);

            //result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            //result = convertView;
        }

        /*
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        */

        viewHolder.txv_list_contacts.setText(dataModel.getFullName());
        // Return the completed view to render on screen
        return convertView;
    }
}