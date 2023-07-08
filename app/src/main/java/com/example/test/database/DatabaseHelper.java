package com.example.test.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "chef.db";

    public static final String SQL_CREATE_ENTRIES_USER = "CREATE TABLE user (\n" +
                                                        "  username TEXT PRIMARY KEY,\n" +
                                                        "  password TEXT NOT NULL,\n" +
                                                        "  points INTEGER DEFAULT 0\n" +
                                                        "); ";

    public static final String SQL_CREATE_ENTRIES_ARTICLE = "CREATE TABLE article (\n" +
                                                            "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                                            "  publisher TEXT NOT NULL,\n" +
                                                            "  dish_name TEXT NOT NULL,\n" +
                                                            "  meal TEXT CHECK(meal IN ('Bữa sáng', 'Bữa trưa', 'Bữa tối', 'Flexible')) NOT NULL,\n" +
                                                            "  serve_order_class TEXT CHECK(serve_order_class IN ('Món khai vị', 'Món chính', 'Món tráng miệng')) NOT NULL,\n" +
                                                            "  type TEXT CHECK(type IN ('Món thịt', 'Món hải sản', 'Món chay', 'Món canh', 'Món rau', 'Mì', 'Bún', 'Món cuốn', 'Món xôi', 'Món cơm', 'Món bánh mặn', 'Món bánh ngọt')) NOT NULL,\n" +
                                                            "  content TEXT NOT NULL,\n" +
                                                            "  likes INTEGER DEFAULT 0,\n" +
                                                            "  dislikes INTEGER DEFAULT 0,\n" +
                                                            "  published_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                                                            "  FOREIGN KEY(publisher) REFERENCES user(username)\n" +
                                                            ");";

    public static final String SQL_CREATE_ENTRIES_BOOKMARK = "CREATE TABLE bookmark (\n" +
                                                            "  user TEXT NOT NULL,\n" +
                                                            "  article INTEGER NOT NULL,\n" +
                                                            "  FOREIGN KEY(user) REFERENCES user(username),\n" +
                                                            "  FOREIGN KEY(article) REFERENCES article(id),\n" +
                                                            "  PRIMARY KEY(user, article)\n" +
                                                            ");";

    public static final String SQL_CREATE_ENTRIES_COMMENT = "CREATE TABLE comment (\n" +
                                                            "  id INTEGER PRIMARY KEY,\n" +
                                                            "  article_id INTEGER NOT NULL,\n" +
                                                            "  commenter TEXT NOT NULL,\n" +
                                                            "  content TEXT NOT NULL,\n" +
                                                            "  FOREIGN KEY(article_id) REFERENCES article(id),\n" +
                                                            "  FOREIGN KEY(commenter) REFERENCES user(username)" +
                                                            ");";

    public static final String SQL_CREATE_ENTRIES_ARTICLE_LIKE = "CREATE TABLE article_like (\n" +
                                                                "  user TEXT NOT NULL,\n" +
                                                                "  article INTEGER NOT NULL,\n" +
                                                                "  PRIMARY KEY(user, article),\n" +
                                                                "  FOREIGN KEY(user) REFERENCES user(username),\n" +
                                                                "  FOREIGN KEY(article) REFERENCES article(id)\n" +
                                                                ");";

    public static final String SQL_CREATE_ENTRIES_ARTICLE_DISLIKE = "CREATE TABLE article_dislike (\n" +
                                                                "  user TEXT NOT NULL,\n" +
                                                                "  article INTEGER NOT NULL,\n" +
                                                                "  PRIMARY KEY(user, article),\n" +
                                                                "  FOREIGN KEY(user) REFERENCES user(username),\n" +
                                                                "  FOREIGN KEY(article) REFERENCES article(id)\n" +
                                                                ");";

    public static final int DB_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_USER);

        db.execSQL(SQL_CREATE_ENTRIES_ARTICLE);

        db.execSQL(SQL_CREATE_ENTRIES_BOOKMARK);

        db.execSQL(SQL_CREATE_ENTRIES_COMMENT);

        db.execSQL(SQL_CREATE_ENTRIES_ARTICLE_LIKE);

        db.execSQL(SQL_CREATE_ENTRIES_ARTICLE_DISLIKE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        onCreate(db);
    }
}
