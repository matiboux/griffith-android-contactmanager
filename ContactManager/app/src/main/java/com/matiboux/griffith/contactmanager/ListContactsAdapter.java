package com.matiboux.griffith.contactmanager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        ImageView contact_picture;
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
        ContactInfo contactInfo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView != null) viewHolder = (ViewHolder) convertView.getTag();
        else {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_list_contacts, parent, false);

            viewHolder.contact_picture = convertView.findViewById(R.id.contact_picture);
            viewHolder.txv_list_contacts = convertView.findViewById(R.id.txv_list_contacts);

            convertView.setTag(viewHolder);
        }

        if (contactInfo != null) {
            // Set contact picture
            if (contactInfo.picture != null) {
                byte[] bytes = Base64.decode(contactInfo.picture, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                viewHolder.contact_picture.setImageBitmap(bitmap);
            } else {
                BitmapDrawable drawable = (BitmapDrawable) context.getDrawable(R.drawable.default_avatar);
                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();
                    viewHolder.contact_picture.setImageBitmap(bitmap);
                }
            }

            // Set contact name
            viewHolder.txv_list_contacts.setText(contactInfo.getFullName());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}