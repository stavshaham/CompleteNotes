package com.stav.completenotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listView = listView.findViewById(R.id.settingsListView);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        List<String> settingsCategory = new ArrayList<>();
        settingsCategory.add("User Settings");
        settingsCategory.add("Privacy");
        settingsCategory.add("Permission Settings");

        ArrayAdapter arrayAdapter = new ArrayAdapter(view.getContext().getApplicationContext(),
                android.R.layout.simple_list_item_1, settingsCategory);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            if (position == 0) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserSettingsFragment());
            } if (position == 1) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new PrivacySettingsFragment());
            } else {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new PermissionSettingsFragment());
            }
        });
    }
}