package com.example.test.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.adapters.MyExpandableListAdapter;
import com.example.test.components.Article;
import com.example.test.components.Report;
import com.example.test.components.User;
import com.example.test.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminFragment extends Fragment {

    ExpandableListView listView;
    ExpandableListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Đăng xuất");
                builder.setMessage("Bạn chắc chắn muốn đăng xuất chứ?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform logout action here
                        MainActivity.loggedInUser = null;
                        Navigation.findNavController(view).navigate(R.id.navigation_login);
                    }
                });
                builder.setNegativeButton("Huỷ", null);
                AlertDialog dialog = builder.create();
                dialog.show();

                // Get the positive and negative buttons
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                // Set the text color of the positive button
                positiveButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.mainTheme));

                // Set the text color of the negative button
                negativeButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.mainTheme));
            }
        });

        List<String> parentItems = new ArrayList<>(Arrays.asList("Báo cáo về bình luận", "Báo cáo về bài đăng", "Danh sách các bài đăng", "Danh sách các người dùng"));

        List<String> postItems = new ArrayList<>();
        List<String> reportedComments = new ArrayList<>();
        List<String> reportedArticles = new ArrayList<>();
        List<String> users = new ArrayList<>();

        Map<String, List<String>> collection = new HashMap<>();

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        for (Article article : dbHelper.getAllArticles()) {
            postItems.add(article.getId() + ". \"" + article.getDishName() + "\" viết bởi '" + article.getPublisher() + "'");
        }

        for (Report report : dbHelper.getAllCommentReports()) {
            reportedComments.add(report.getId() + ". \"" + report.getCommentContent() + "\" báo cáo bởi '" + report.getReporter() + "' với lí do: " + report.getReason());
        }

        for (Report report : dbHelper.getAllArticleReports()) {
            reportedArticles.add(report.getId() + ". \"" + report.getArticleName() + "\" báo cáo bởi '" + report.getReporter() + "' với lí do: " + report.getReason());
        }

        for (User user : dbHelper.getAllUser()) {
            users.add((users.size() + 1) + ". " + user.getUsername() + ". Mức độ cảnh cáo: " + user.getReportLevel());
        }

        collection.put(parentItems.get(0), reportedComments);
        collection.put(parentItems.get(1), reportedArticles);
        collection.put(parentItems.get(2), postItems);
        collection.put(parentItems.get(3), users);

        listView = view.findViewById(R.id.parent_list);
        adapter = new MyExpandableListAdapter(getActivity(), parentItems, collection);

        listView.setAdapter(adapter);

//        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            int lastExpandedPos = -1;
//            @Override
//            public void onGroupExpand(int i) {
//                if (lastExpandedPos != -1 && i != lastExpandedPos) {
//                    listView.collapseGroup(lastExpandedPos);
//                }
//                lastExpandedPos = i;
//            }
//        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                String selected = adapter.getChild(i, i1).toString();
                return true;
            }
        });

    }
}