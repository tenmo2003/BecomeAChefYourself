package com.example.test.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.utils.DatabaseHelper;

import java.util.List;
import java.util.Map;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private Map<String, List<String>> collection;
    private List<String> groupList;

    private DatabaseHelper dbHelper;

    public MyExpandableListAdapter(Context context, List<String> groupList, Map<String, List<String>> collection) {
        this.context = context;
        this.collection = collection;
        this.groupList = groupList;
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getGroupCount() {
        return collection.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return collection.get(groupList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return groupList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return collection.get(groupList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String name = groupList.get(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.group_item, null);

            TextView item = view.findViewById(R.id.name);
            item.setTypeface(null, Typeface.BOLD);
            item.setText(name);

            return view;
        }

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String child = getChild(i, i1).toString();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = view.findViewById(R.id.name);
        ImageView delete = view.findViewById(R.id.delete);

        item.setText(child);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage("Do you want to remove?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int ii) {
                        List<String> childList = collection.get(groupList.get(i));
                        childList.remove(i1);
                        if (child.equals("Báo cáo về bình luận")) {
                            // Split the string by the dot (.)
                            String[] parts = child.split("\\.");

                            // Get the first part and remove any leading or trailing white spaces
                            String idString = parts[0].trim();

                            // Parse the ID string to an integer
                            int id = Integer.parseInt(idString);

                            dbHelper.removeReport(id);
                        } else {
                            // Split the string by the dot (.)
                            String[] parts = child.split("\\.");

                            // Get the first part and remove any leading or trailing white spaces
                            String idString = parts[0].trim();

                            // Parse the ID string to an integer
                            int id = Integer.parseInt(idString);

                            dbHelper.removeArticle(id);
                        }
                        notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();

                // Split the string by the dot (.)
                String[] parts = child.split("\\.");

                // Get the first part and remove any leading or trailing white spaces
                String idString = parts[0].trim();

                // Parse the ID string to an integer
                int id = Integer.parseInt(idString);

                args.putInt("reportID", id);
                Navigation.findNavController(view).navigate(R.id.navigation_article, args);
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
