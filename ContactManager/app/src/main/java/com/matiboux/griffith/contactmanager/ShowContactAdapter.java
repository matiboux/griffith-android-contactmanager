package com.matiboux.griffith.contactmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ShowContactAdapter extends ArrayAdapter<FieldInfo> implements View.OnClickListener {

    Context context;
    private List<FieldInfo> objects;

    public ShowContactAdapter(@NonNull Context context, @NonNull List<FieldInfo> objects) {
        super(context, R.layout.adapter_show_contact, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public void onClick(View view) {
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        // Get the data item for this position
        FieldInfo fieldInfo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView != null) viewHolder = (ViewHolder) convertView.getTag();
        else {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_show_contact, parent, false);

            viewHolder.txv_field_name = convertView.findViewById(R.id.txv_field_name);
            viewHolder.txv_field_value = convertView.findViewById(R.id.txv_field_value);

            convertView.setTag(viewHolder);
        }

        if (fieldInfo != null) {
            viewHolder.txv_field_name.setText(fieldInfo.name);
            viewHolder.txv_field_value.setText(fieldInfo.value);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txv_field_name;
        TextView txv_field_value;
    }
}