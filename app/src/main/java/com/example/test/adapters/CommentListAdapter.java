package com.example.test.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.components.Comment;
import com.example.test.components.User;
import com.example.test.fragments.ArticleFragment;
import com.example.test.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CommentListAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private List<Comment> comments;

    Context context;

    public void setContext(Context c) {
        context = c;
    }


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
        holder.comment = comments.get(position);
        holder.commenter.setText(comments.get(position).getCommenter());
        holder.comment_content.setText(comments.get(position).getContent());
        holder.context = context;

        AtomicBoolean checkAvatar = new AtomicBoolean(false);
        AtomicReference<String> url = new AtomicReference<>();
        AtomicReference<String> commenterName = new AtomicReference<>();
        MainActivity.runTask(() -> {
            User user = MainActivity.sqlConnection.getUserWithUsername(comments.get(position).getCommenter());
            commenterName.set(user.getFullname());
            checkAvatar.set(user.getAvatarURL().equals(""));
            url.set(user.getAvatarURL());
        }, () -> {
            if (checkAvatar.get()) {
                holder.comment_avatar.setImageResource(R.drawable.baseline_person_24);
            } else {
                Glide.with(context).load(url.get()).into(holder.comment_avatar);
            }
            holder.commenter.setText(commenterName.get());
        }, null);

        int curPos = position;
        if (MainActivity.loggedInUser != null && (MainActivity.loggedInUser.getUsername().equals("admin") || MainActivity.loggedInUser.getUsername().equals(holder.comment.getCommenter()))) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    AlertDialog alertDialog = builder.setTitle("Xác nhận")
                            .setMessage("Bạn có chắc chắn muốn xoá comment '" + comments.get(curPos).getContent() + "'")
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked the "Yes" button, proceed with removal
                                    MainActivity.runTask(() -> {
                                        MainActivity.sqlConnection.removeComment(comments.get(curPos).getId());
                                        if (!MainActivity.loggedInUser.getUsername().equals(comments.get(curPos).getCommenter()))
                                            MainActivity.sqlConnection.increaseReportLevelForUser(comments.get(curPos).getCommenter());
                                    }, () -> {
                                        comments.remove(curPos);
                                        notifyDataSetChanged();
                                        HomeFragment.commentAdded--;
                                        ArticleFragment.commentTextView.setText(String.valueOf(Integer.parseInt(ArticleFragment.commentTextView.getText().toString().split("\\s+")[0]) - 1) + " bình luận");
                                    }, MainActivity.progressDialog);

                                }
                            })
                            .setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked the "No" button, do nothing
                                }
                            })
                            .create();
                    alertDialog.show();


                    // Get the positive and negative buttons
                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);


                    // Set the text color of the positive button
                    positiveButton.setTextColor(ContextCompat.getColor(context, R.color.mainTheme));

                    // Set the text color of the negative button
                    negativeButton.setTextColor(ContextCompat.getColor(context, R.color.mainTheme));
                }
            });
        } else {
            holder.delete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

class CommentViewHolder extends RecyclerView.ViewHolder {
    Comment comment;
    ImageView comment_avatar;
    TextView commenter;
    TextView comment_content;

    View commentView;

    ImageView delete;

    Context context;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        comment_avatar = itemView.findViewById(R.id.commenter_avatar);
        commenter = itemView.findViewById(R.id.commenter);
        comment_content = itemView.findViewById(R.id.comment_content);
        commentView = itemView;
        delete = itemView.findViewById(R.id.remove_btn);

        ImageView report = itemView.findViewById(R.id.report_btn);
        if (MainActivity.loggedInUser == null) {
            report.setVisibility(View.GONE);
        } else {
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MainActivity.loggedInUser == null) {
                        MainActivity.toast.setText("Bạn cần đăng nhập trước");
                        MainActivity.toast.show();
                        Navigation.findNavController(view).navigate(R.id.navigation_login);
                        return;
                    }

                    final EditText reasonEditText = new EditText(context);
                    reasonEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    reasonEditText.setHint("Lí do");
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("Báo cáo bình luận")
                            .setMessage("Vấn đề của bình luận này:")
                            .setView(reasonEditText)
                            .setPositiveButton("Báo cáo", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Your positive button click logic
                                    MainActivity.runTask(() -> {
                                        MainActivity.sqlConnection.reportComment(comment.getId(), MainActivity.loggedInUser.getUsername(), reasonEditText.getText().toString(), comment.getArticleID());
                                    });
                                }
                            })
                            .setNegativeButton("Huỷ", null)
                            .show();

                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                    positiveButton.setTextColor(ContextCompat.getColor(context, R.color.mainTheme));
                    negativeButton.setTextColor(ContextCompat.getColor(context, R.color.mainTheme));
                }
            });
        }
        comment_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("username", comment.getCommenter());
                Navigation.findNavController(view).navigate(R.id.navigation_profile, args);
            }
        });

        commenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("username", comment.getCommenter());
                Navigation.findNavController(view).navigate(R.id.navigation_profile, args);
            }
        });


    }
}
