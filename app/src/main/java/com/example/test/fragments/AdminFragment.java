package com.example.test.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.test.R;
import com.example.test.adapters.MyExpandableListAdapter;
import com.example.test.components.Article;
import com.example.test.components.Report;
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

        List<String> parentItems = new ArrayList<>(Arrays.asList("Báo cáo về bình luận", "Báo cáo về bài đăng", "Các bài đăng"));

        List<String> postItems = new ArrayList<>();
        List<String> reportedComments = new ArrayList<>();
        List<String> reportedArticles = new ArrayList<>();

        Map<String, List<String>> collection = new HashMap<>();

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        for (Article article : dbHelper.getAllArticles()) {
            postItems.add(article.getId() + ". \"" + article.getDishName() + "\" viết bởi " + article.getPublisher());
        }

        for (Report report : dbHelper.getAllCommentReports()) {
            reportedComments.add(report.getId() + ". \"" + report.getCommentContent() + "\" báo cáo bởi " + report.getReporter() + " với lí do: " + report.getReason());
        }

        for (Report report : dbHelper.getAllArticleReports()) {
            reportedArticles.add(report.getId() + ". \"" + report.getArticleName() + "\" báo cáo bởi " + report.getReporter() + " với lí do: " + report.getReason());
        }

        collection.put(parentItems.get(0), reportedComments);
        collection.put(parentItems.get(1), reportedArticles);
        collection.put(parentItems.get(2), postItems);

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