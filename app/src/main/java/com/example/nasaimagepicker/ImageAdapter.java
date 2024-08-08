package com.example.nasaimagepicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> savedImageList;

    public ImageAdapter(Context context, ArrayList<String> savedImageList) {
        this.context = context;
        this.savedImageList = savedImageList;
    }

    @Override
    public int getCount() {
        return savedImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return savedImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        }

        TextView textViewDate = convertView.findViewById(R.id.text_view_date);
        ImageView imageView = convertView.findViewById(R.id.image_view);

        String savedImageData = savedImageList.get(position);
        String[] parts = savedImageData.split(",");

        if (parts.length >= 3) {
            String date = parts[0];
            String imagePath = parts[2];

            textViewDate.setText(date);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        } else {
            textViewDate.setText(R.string.error_loading_image);
            imageView.setImageResource(R.drawable.error_placeholder);
        }

        return convertView;
    }
}
