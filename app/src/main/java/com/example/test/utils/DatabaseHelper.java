package com.example.test.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.test.activities.MainActivity;
import com.example.test.components.Article;
import com.example.test.components.Comment;
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
                                                            "  image TEXT," +
                                                            "  FOREIGN KEY(publisher) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                                                            ");";

    public static final String SQL_CREATE_ENTRIES_BOOKMARK = "CREATE TABLE bookmarks (\n" +
                                                            "  user TEXT NOT NULL,\n" +
                                                            "  article INTEGER NOT NULL,\n" +
                                                            "  FOREIGN KEY(user) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                                                            "  FOREIGN KEY(article) REFERENCES articles(id) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                                                            "  PRIMARY KEY(user, article)\n" +
                                                            ");";

    public static final String SQL_CREATE_ENTRIES_COMMENT = "CREATE TABLE comments (\n" +
                                                            "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                                            "  article_id INTEGER NOT NULL,\n" +
                                                            "  commenter TEXT NOT NULL,\n" +
                                                            "  content TEXT NOT NULL,\n" +
                                                            "  FOREIGN KEY(article_id) REFERENCES articles(id) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                                                            "  FOREIGN KEY(commenter) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE" +
                                                            ");";

    public static final String SQL_CREATE_ENTRIES_ARTICLE_LIKE = "CREATE TABLE article_likes (\n" +
                                                                "  user TEXT NOT NULL,\n" +
                                                                "  article INTEGER NOT NULL,\n" +
                                                                "  PRIMARY KEY(user, article),\n" +
                                                                "  FOREIGN KEY(user) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                                                                "  FOREIGN KEY(article) REFERENCES articles(id) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                                                                ");";

    public static final String SQL_CREATE_ENTRIES_FOLLOWS = " CREATE TABLE follows (\n" +
                                                            "  followed TEXT NOT NULL,\n" +
                                                            "  follower TEXT NOT NULL,\n" +
                                                            "  PRIMARY KEY(followed, follower),\n" +
                                                            "  FOREIGN KEY(followed) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                                                            "  FOREIGN KEY(follower) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                                                            ");";
    public static final int DB_VERSION = 7;

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
        if (oldVersion < 7) {
            db.execSQL("DROP TABLE IF EXISTS article_likes");
            db.execSQL("DROP TABLE IF EXISTS bookmarks");
            //db.execSQL("DROP TABLE IF EXISTS comments");
            //db.execSQL(SQL_CREATE_ENTRIES_COMMENT);
            db.execSQL(SQL_CREATE_ENTRIES_BOOKMARK);
            db.execSQL(SQL_CREATE_ENTRIES_ARTICLE_LIKE);
        }
    }

    public boolean signUpUser(String username, String password, String fullname) {
        SQLiteDatabase db = getWritableDatabase();

        // Check if the username already exists in the database
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username=?", new String[]{username});
        if (cursor.getCount() > 0) {
            // The username already exists, so we cannot add a new user with the same username
            cursor.close();
            db.close();
            return false;
        }

        // The username does not exist, so we can add a new user with the given username, password, and fullname
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("fullname", fullname);
        db.insert("users", null, values);

        cursor.close();
        db.close();
        return true;
    }

    public boolean userAuthentication(String username, String password) {
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
            db.close();
            return true;
        } else {
            // If the user does not exist in the database, return null
            cursor.close();
            db.close();
            return false;
        }
    }

    public User getUserWithUsername(String username) {
        SQLiteDatabase db = getWritableDatabase();

        // Check if the given username and password combination exists in the database
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username=?", new String[]{username});

        if (cursor.moveToFirst()) {
            // If the user exists in the database, create a User object and return it
            @SuppressLint("Range") String fullname = cursor.getString(cursor.getColumnIndex("fullname"));
            @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndex("points"));
            @SuppressLint("Range") String bio = cursor.getString(cursor.getColumnIndex("bio"));
            User user = new User(username, fullname, points, bio);

            cursor.close();
            db.close();
            return user;
        } else {
            // If the user does not exist in the database, return null
            cursor.close();
            db.close();
            return null;
        }
    }

    public int getNextArticleID() {
        SQLiteDatabase db = this.getReadableDatabase();
        int maxID = (int) DatabaseUtils.longForQuery(db, "SELECT last_insert_rowid() FROM articles", null);
        db.close();
        return maxID + 1;
    }

    public boolean addArticle(String dishName, String publisher, String meal, String serve_order_class, String type, String recipe, String ingredients, String timeToMake, String imgURL) {
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
        values.put("time_to_make", timeToMake);
        values.put("image", imgURL);

        // Insert the new article into the database
        long result = db.insert("articles", null, values);

        db.close();
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
                "time_to_make",
                "image"
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
                int comments = getTotalCommentCount(id);
                String publishedTime = cursor.getString(cursor.getColumnIndexOrThrow("published_time"));
                String timeToMake = cursor.getString(cursor.getColumnIndexOrThrow("time_to_make"));
                String imgURL = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                Article article = new Article(id, dishName, publisher, meal, serveOrderClass, type, content, ingredients, likes, comments, publishedTime, timeToMake, imgURL);
                articles.add(article);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return articles;
    }

    public List<Article> getArticlesFromUser(String username) {
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
                "time_to_make",
                "image"
        };
        String selection = "publisher=?";
        String[] selectionArgs = { username };
        Cursor cursor = db.query(
                "articles",
                projection,
                selection,
                selectionArgs,
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
                int comments = getTotalCommentCount(id);
                String publishedTime = cursor.getString(cursor.getColumnIndexOrThrow("published_time"));
                String timeToMake = cursor.getString(cursor.getColumnIndexOrThrow("time_to_make"));
                String imgURL = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                Article article = new Article(id, dishName, publisher, meal, serveOrderClass, type, content, ingredients, likes, comments, publishedTime, timeToMake, imgURL);
                articles.add(article);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return articles;
    }

    public List<Article> getUserSavedArticles(String username) {
        List<Article> savedArticles = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "articles.id",
                "dish_name",
                "publisher",
                "meal",
                "serve_order_class",
                "type",
                "recipe",
                "ingredients",
                "likes",
                "published_time",
                "time_to_make",
                "image"
        };
        String selection = "bookmarks.user=?";
        String[] selectionArgs = { username };
        String sortOrder = "published_time DESC";
        String table = "articles INNER JOIN bookmarks ON articles.id = bookmarks.article";
        Cursor cursor = db.query(
                table,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
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
                int comments = getTotalCommentCount(id);
                String publishedTime = cursor.getString(cursor.getColumnIndexOrThrow("published_time"));
                String timeToMake = cursor.getString(cursor.getColumnIndexOrThrow("time_to_make"));
                String imgURL = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                Article article = new Article(id, dishName, publisher, meal, serveOrderClass, type, content, ingredients, likes, comments, publishedTime, timeToMake, imgURL);
                savedArticles.add(article);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return savedArticles;
    }

    public boolean addFollow(String follower, String followed) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("follower", follower);
        values.put("followed", followed);

        long result = db.insert("follows", null, values);

        db.close();

        return result != -1;
    }

    public boolean removeFollow(String follower, String followed) {
        SQLiteDatabase db = getWritableDatabase();

        // Define the table name and the WHERE clause for the delete operation
        String tableName = "follows";
        String whereClause = "follower=? AND followed=?";

        // Define the arguments to replace the placeholders in the WHERE clause
        String[] whereArgs = {follower, followed};

        // Perform the delete operation and get the number of rows affected
        int rowsDeleted = db.delete(tableName, whereClause, whereArgs);

        // Close the database
        db.close();

        // Return true if at least one row was deleted, false otherwise
        return rowsDeleted > 0;
    }

    public boolean isFollowing(String follower, String followed) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM follows WHERE follower=? AND followed=?", new String[] {follower, followed});

        boolean isFollowing = cursor.getCount() > 0;

        // Close the cursor and the database
        cursor.close();
        db.close();

        // Return the result
        return isFollowing;
    }

    public int getTotalFollowCount(String username) {
        SQLiteDatabase db = getReadableDatabase();

        // Define the query to get the count of followers for the given user
        String query = "SELECT COUNT(*) FROM follows WHERE followed=?";

        // Define the arguments to replace the placeholder in the query
        String[] args = {username};

        // Execute the query and obtain the result cursor
        Cursor cursor = db.rawQuery(query, args);

        // Get the count from the first column of the first row of the cursor
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        // Close the cursor and the database
        cursor.close();
        db.close();

        // Return the count
        return count;
    }

    public int getTotalCommentCount(int article_id) {
        SQLiteDatabase db = getReadableDatabase();

        // Define the query to get the count of followers for the given user
        String query = "SELECT COUNT(*) FROM comments WHERE article_id=?";

        // Define the arguments to replace the placeholder in the query
        String[] args = {String.valueOf(article_id)};

        // Execute the query and obtain the result cursor
        Cursor cursor = db.rawQuery(query, args);

        // Get the count from the first column of the first row of the cursor
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        // Close the cursor and the database
        cursor.close();
        db.close();

        // Return the count
        return count;
    }

    public boolean addBookmark(String user, int articleID) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("user", user);
        values.put("article", articleID);

        long result = db.insert("bookmarks", null, values);

        db.close();

        return result != -1;
    }

    public boolean removeBookmark(String user, int articleID) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = db.delete("bookmarks", "user=? AND article=?", new String[] {user, String.valueOf(articleID)});

        db.close();

        return rowsDeleted > 0;
    }

    public boolean checkBookmarked(String user, int articleID) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM bookmarks WHERE user=? AND article=?", new String[] {user, String.valueOf(articleID)});

        boolean bookmarked = cursor.moveToFirst();

        cursor.close();
        db.close();

        return bookmarked;
    }

    public List<Comment> getCommentWithArticleID(String articleID) {
        List<Comment> commentList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                "commenter",
                "content"
        };
        String selection = "article_id=?";
        String[] selectionArgs = { articleID };
        Cursor cursor = db.query(
                "comments",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );


        try {
            while (cursor.moveToNext()) {
                String commenter = cursor.getString(cursor.getColumnIndexOrThrow("commenter"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));

                Comment comment = new Comment(commenter, content);
                commentList.add(comment);
            }
        } finally {
            cursor.close();
            db.close();
        }

        return commentList;
    }
}
