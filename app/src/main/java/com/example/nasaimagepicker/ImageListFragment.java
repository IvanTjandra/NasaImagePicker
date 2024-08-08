package com.example.nasaimagepicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Map;

public class ImageListFragment extends Fragment {

    private ListView listView;
    private ArrayList<String> savedImageList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_list, container, false);

        listView = view.findViewById(R.id.list_view_saved_images);
        sharedPreferences = getActivity().getSharedPreferences("NASA_IMAGES", getActivity().MODE_PRIVATE);
        savedImageList = new ArrayList<>();

        loadSavedImages();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, savedImageList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = savedImageList.get(position);
                String[] parts = selectedItem.split(",");
                String url = parts[1];

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedItem = savedImageList.get(position);
                String[] parts = selectedItem.split(",");
                final String date = parts[0];

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.confirm_delete_title)
                        .setMessage(R.string.confirm_delete_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove(date);
                                editor.apply();

                                savedImageList.remove(selectedItem);
                                adapter.notifyDataSetChanged();

                                Toast.makeText(getContext(), R.string.image_deleted, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();

                return true;
            }
        });

        return view;
    }

    private void loadSavedImages() {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            savedImageList.add(entry.getKey() + "," + entry.getValue().toString());
        }
    }
}
