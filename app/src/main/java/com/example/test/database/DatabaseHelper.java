package com.example.test.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.test.activities.MainActivity;
import com.example.test.components.Article;
import com.example.test.components.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "chef.db";

    public static final String SQL_CREATE_ENTRIES_USER = "CREATE TABLE user (\n" +
                                                        "  username TEXT PRIMARY KEY,\n" +
                                                        "  password TEXT NOT NULL,\n" +
                                                        "  fullname TEXT NOT NULL, \n" +
                                                        "  points INTEGER DEFAULT 0,\n" +
                                                        "  bio TEXT \n" +
                                                        "); ";

    public static final String SQL_CREATE_ENTRIES_ARTICLE = "CREATE TABLE articles (\n" +
                                                            "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                                            "  publisher TEXT NOT NULL,\n" +
                                                            "  dish_name TEXT NOT NULL,\n" +
                                                            "  meal TEXT CHECK(meal IN ('Bữa sáng', 'Bữa trưa', 'Bữa tối', 'Linh hoạt')) NOT NULL,\n" +
                                                            "  serve_order_class TEXT CHECK(serve_order_class IN ('Món khai vị', 'Món chính', 'Món tráng miệng')) NOT NULL,\n" +
                                                            "  type TEXT CHECK(type IN ('Món thịt', 'Món hải sản', 'Món chay', 'Món canh', 'Món rau', 'Mì', 'Bún', 'Món cuốn', 'Món xôi', 'Món cơm', 'Món bánh mặn', 'Món bánh ngọt')) NOT NULL,\n" +
                                                            "  recipe TEXT NOT NULL,\n" +
                                                            "  ingredients TEXT NOT NULL,\n" +
                                                            "  likes INTEGER DEFAULT 0,\n" +
                                                            "  published_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                                                            "  time_to_make TEXT," +
                                                            "  FOREIGN KEY(publisher) REFERENCES user(username)\n" +
                                                            ");";

    public static final String SQL_CREATE_ENTRIES_BOOKMARK = "CREATE TABLE bookmarks (\n" +
                                                            "  user TEXT NOT NULL,\n" +
                                                            "  article INTEGER NOT NULL,\n" +
                                                            "  FOREIGN KEY(user) REFERENCES user(username),\n" +
                                                            "  FOREIGN KEY(article) REFERENCES article(id),\n" +
                                                            "  PRIMARY KEY(user, article)\n" +
                                                            ");";

    public static final String SQL_CREATE_ENTRIES_COMMENT = "CREATE TABLE comments (\n" +
                                                            "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                                            "  article_id INTEGER NOT NULL,\n" +
                                                            "  commenter TEXT NOT NULL,\n" +
                                                            "  content TEXT NOT NULL,\n" +
                                                            "  FOREIGN KEY(article_id) REFERENCES article(id),\n" +
                                                            "  FOREIGN KEY(commenter) REFERENCES user(username)" +
                                                            ");";

    public static final String SQL_CREATE_ENTRIES_ARTICLE_LIKE = "CREATE TABLE article_likes (\n" +
                                                                "  user TEXT NOT NULL,\n" +
                                                                "  article INTEGER NOT NULL,\n" +
                                                                "  PRIMARY KEY(user, article),\n" +
                                                                "  FOREIGN KEY(user) REFERENCES user(username),\n" +
                                                                "  FOREIGN KEY(article) REFERENCES article(id)\n" +
                                                                ");";

    public static final String SQL_CREATE_ENTRIES_FOLLOWS = " CREATE TABLE follows (\n" +
                                                            "  followed TEXT NOT NULL,\n" +
                                                            "  follower NOT NULL,\n" +
                                                            "  PRIMARY KEY(followed, follower),\n" +
                                                            "  FOREIGN KEY(followed) REFERENCES user(username),\n" +
                                                            "  FOREIGN KEY(follower) REFERENCES user(username)\n" +
                                                            ");";

    public static final int DB_VERSION = 6;

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

        db.execSQL(SQL_CREATE_ENTRIES_FOLLOWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE user ADD COLUMN bio TEXT");
        }
        if (oldVersion < 4) {
            db.execSQL("DROP TABLE IF EXISTS articles");
            db.execSQL(SQL_CREATE_ENTRIES_ARTICLE);
        }
        if (oldVersion < 5) {
            db.execSQL(SQL_CREATE_ENTRIES_FOLLOWS);
        }
        if (oldVersion < 6) {
            db.execSQL("DROP TABLE IF EXISTS article_dislikes");
        }
    }

    public boolean signUpUser(String username, String password, String fullname) {
        SQLiteDatabase db = getWritableDatabase();

        // Check if the username already exists in the database
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username=?", new String[]{username});
        if (cursor.getCount() > 0) {
            // The username already exists, so we cannot add a new user with the same username
            cursor.close();
            return false;
        }

        // The username does not exist, so we can add a new user with the given username, password, and fullname
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("fullname", fullname);
        db.insert("users", null, values);

        cursor.close();
        return true;
    }

    public User userAuthentication(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        // Check if the given username and password combination exists in the database
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username=? AND password=?", new String[]{username, password});

        if (cursor.moveToFirst()) {
            // If the user exists in the database, create a User object and return it
            @SuppressLint("Range") String fullname = cursor.getString(cursor.getColumnIndex("fullname"));
            @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndex("points"));
            @SuppressLint("Range") String bio = cursor.getString(cursor.getColumnIndex("bio"));
            User user = new User(username, password, fullname, points, bio);

            MainActivity.loggedInUser = user;

            cursor.close();
            return user;
        } else {
            // If the user does not exist in the database, return null
            cursor.close();
            return null;
        }
    }

    public boolean addArticle(String dishName, String publisher, String meal, String serve_order_class, String type, String recipe, String ingredients) {
        SQLiteDatabase db = getWritableDatabase();

        // Create a new ContentValues object that contains the values for the new article
        ContentValues values = new ContentValues();
        values.put("dish_name", dishName);
        values.put("publisher", publisher);
        values.put("recipe", recipe);
        values.put("ingredients", ingredients);
        values.put("meal", meal);
        values.put("serve_order_class", serve_order_class);
        values.put("type", type);

        // Insert the new article into the database
        long result = db.insert("articles", null, values);

        return result != -1;
    }

    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "id",
                "dish_name",
                "publisher",
                "meal",
                "serve_order_class",
                "type",
                "recipe",
                "ingredients",
                "likes",
                "published_time",
                "time_to_make"
        };
        Cursor cursor = db.query(
                "articles",
                projection,
                null,
                null,
                null,
                null,
                "published_time DESC"
        );
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String dishName = cursor.getString(cursor.getColumnIndexOrThrow("dish_name"));
                String publisher = cursor.getString(cursor.getColumnIndexOrThrow("publisher"));
                String meal = cursor.getString(cursor.getColumnIndexOrThrow("meal"));
                String serveOrderClass = cursor.getString(cursor.getColumnIndexOrThrow("serve_order_class"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("recipe"));
                String ingredients = cursor.getString(cursor.getColumnIndexOrThrow("ingredients"));
                int likes = cursor.getInt(cursor.getColumnIndexOrThrow("likes"));
                String publishedTime = cursor.getString(cursor.getColumnIndexOrThrow("published_time"));
                String timeToMake = cursor.getString(cursor.getColumnIndexOrThrow("time_to_make"));
                Article article = new Article(id, dishName, publisher, meal, serveOrderClass, type, content, ingredients, likes, publishedTime, timeToMake);
                articles.add(article);
            }
        } finally {
            cursor.close();
        }
        Log.i("Articles", articles.toString());
        return articles;
    }
}
