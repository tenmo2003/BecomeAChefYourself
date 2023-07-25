package com.example.test.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.components.Comment;
import com.example.test.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private List<Comment> comments;

    Context context;

    DatabaseHelper dbHelper;
    public void setContext(Context c) {
        context = c;
    }

    public void setDbHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
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
        holder.dbHelper = dbHelper;

        if (dbHelper.getUserWithUsername(comments.get(position).getCommenter()).getAvatarURL().equals("")) {
            holder.comment_avatar.setImageResource(R.drawable.baseline_person_24);
        } else {
            Glide.with(context).load(dbHelper.getUserWithUsername(comments.get(position).getCommenter()).getAvatarURL()).into(holder.comment_avatar);
        }


        int curPos = position;
        if (MainActivity.loggedInUser != null && MainActivity.loggedInUser.getUsername().equals("admin")) {
            holder.commentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Xác nhận")
                            .setMessage("Bạn có chắc chắn muốn xoá comment này")
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked the "Yes" button, proceed with removal
                                    boolean result = dbHelper.removeComment(comments.get(curPos).getId());
                                    if (result) {
                                        comments.remove(curPos);
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(context, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked the "No" button, do nothing
                                }
                            })
                            .create()
                            .show();
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}

class CommentViewHolder extends RecyclerView.ViewHolder {
    Comment comment;
    ImageView comment_avatar;
    TextView commenter;
    TextView comment_content;

    View commentView;

    Context context;
    DatabaseHelper dbHelper;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        comment_avatar = itemView.findViewById(R.id.commenter_avatar);
        commenter = itemView.findViewById(R.id.commenter);
        comment_content = itemView.findViewById(R.id.comment_content);
        commentView = itemView;


        TextView report = itemView.findViewById(R.id.report_btn);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.loggedInUser == null) {
                    Toast.makeText(context, "Bạn cần đăng nhập trước", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.navigation_login);
                    return;
                }

                final EditText reasonEditText = new EditText(context);
                reasonEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                reasonEditText.setHint("Lí do");
                new AlertDialog.Builder(context)
                        .setTitle("Báo cáo bình luận")
                        .setMessage("Vấn đề của bình luận này:")
                        .setView(reasonEditText)
                        .setPositiveButton("Báo cáo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int comment_id = comment.getId(); // replace with actual comment ID
                                String reporter = MainActivity.loggedInUser.getUsername(); // replace with actual user who reported the comment
                                String reason = reasonEditText.getText().toString();
                                boolean success = dbHelper.reportComment(comment_id, reporter, reason, comment.getArticleID());

                                if (success) {
                                    Toast.makeText(context, "Báo cáo của bạn đã được gửi để xem xét", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Gửi báo cáo thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Huỷ", null)
                        .show();
            }
        });

    }
}
