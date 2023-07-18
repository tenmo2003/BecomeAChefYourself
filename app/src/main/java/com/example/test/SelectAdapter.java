package com.example.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.test.inventory.SelectRoll;

import java.util.List;

public class SelectAdapter extends BaseAdapter {

    private Context context;
    private List<SelectRoll> selectList;

    public SelectAdapter(Context context, List<SelectRoll> selectList) {
        this.context = context;
        this.selectList = selectList;
    }


    @Override
    public int getCount() {
        return selectList != null ? selectList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_select, viewGroup, false);

        TextView txtName = rootView.findViewById(R.id.name);

        txtName.setText(selectList.get(i).getName());

        return rootView;
    }
}
