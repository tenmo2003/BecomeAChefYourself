package com.example.test.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.adapters.NotificationListAdapter;
import com.example.test.components.Notification;
import com.example.test.utils.DatabaseHelper;

import java.util.List;

public class NotificationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        ListView listView = view.findViewById(R.id.list_view);

        List<Notification> notificationList = dbHelper.getNotificationsForUser(MainActivity.loggedInUser.getUsername());

        NotificationListAdapter adapter = new NotificationListAdapter(getActivity(), notificationList);

        listView.setAdapter(adapter);
    }
}