package com.example.test.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.components.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private List<Comment> comments;

    public void setComments(List<Comment> comments) {
        this.comments = new ArrayList<>();
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }

    public void addNewComment(Comment newComment) {
        comments.add(0, newComment);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.commenter.setText(comments.get(position).getCommenter());
        holder.comment_content.setText(comments.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}

class CommentViewHolder extends RecyclerView.ViewHolder {
    ImageView comment_avatar;
    TextView commenter;
    TextView comment_content;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        comment_avatar = itemView.findViewById(R.id.commenter_avatar);
        commenter = itemView.findViewById(R.id.commenter);
        comment_content = itemView.findViewById(R.id.comment_content);

        comment_avatar.setImageResource(R.drawable.user_avatar);
    }
}
