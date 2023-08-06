package com.example.test.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test.R;
import com.example.test.fragments.CreateStep3Fragment;

import java.util.ArrayList;

public class IngredientListAdapter extends ArrayAdapter<String> {
    ArrayList<String> list;
    Context context;

    public IngredientListAdapter(Context context, ArrayList<String> list) {
        super(context, R.layout.list_row, list);
        this.context = context;
    }

    public void setIngredientList(ArrayList<String> list) {
        this.list = new ArrayList<>();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_row, null);
        }

        TextView name = convertView.findViewById(R.id.name);

        name.setText(list.get(position));

        ImageView remove = convertView.findViewById(R.id.remove);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(position);
            }
        });

        return convertView;
    }

    public void removeItem(int position) {
        StringBuilder updatedIngredientString = new StringBuilder();

        String[] ingredientsArray = CreateStep3Fragment.ingredientString.toString().split(";");
        for (String ingredient : ingredientsArray) {
            if (!ingredient.equals(CreateStep3Fragment.ingredientsList.get(position))) {
                if (updatedIngredientString.length() != 0) {
                    updatedIngredientString.append(';');
                }
                updatedIngredientString.append(ingredient);
            }
        }
        CreateStep3Fragment.ingredientString = updatedIngredientString;

        CreateStep3Fragment.ingredientsList.remove(position);
        setIngredientList(CreateStep3Fragment.ingredientsList);
    }
}
