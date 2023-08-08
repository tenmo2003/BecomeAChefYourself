package com.example.test.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.adapters.NotificationListAdapter;
import com.example.test.components.InAppNotification;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class NotificationFragment extends Fragment {

    public static MutableLiveData<Boolean> updated = new MutableLiveData<>(false);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ListView listView = view.findViewById(R.id.list_view);

//        AtomicReference<List<InAppNotification>> notificationList = new AtomicReference<>();
//        MainActivity.runTask(() -> {
//            MainActivity.notificationList = MainActivity.sqlConnection.getNotificationsForUser(MainActivity.loggedInUser.getUsername());
//            notificationList.set(MainActivity.sqlConnection.getNotificationsForUser(MainActivity.loggedInUser.getUsername()));
//        }, () -> {
//            Log.i("DETAIL", String.valueOf(notificationList.get().size()));
        NotificationListAdapter adapter = new NotificationListAdapter(getActivity());


        listView.setAdapter(adapter);
        updated.observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    System.out.println("UPDATED");
                    adapter.notifyDataSetChanged();
                    updated.setValue(false);
                }
            }
        });
//        }, MainActivity.progressDialog);
    }
}