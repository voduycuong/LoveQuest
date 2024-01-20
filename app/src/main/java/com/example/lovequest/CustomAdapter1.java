package com.example.lovequest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter1 extends ArrayAdapter<String> {
    private int selectedItem = -1; // -1 indicates no selection

    public CustomAdapter1(Context context, String[] items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_orientation, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textViewItem);
        ImageView imageView = convertView.findViewById(R.id.imageViewTick);

        textView.setText(getItem(position));
        imageView.setVisibility(position == selectedItem ? View.VISIBLE : View.GONE);

        return convertView;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }
}