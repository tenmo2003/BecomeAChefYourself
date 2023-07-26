package com.example.test.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;

import com.example.test.activities.MainActivity;
import com.example.test.components.Article;
import com.example.test.components.Comment;
import com.example.test.components.Report;
import com.example.test.components.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "chef.db";

    public static final String SQL_CREATE_ENTRIES_USER = "CREATE TABLE user (\n" +
                                                        "  username TEXT PRIMARY KEY,\n" +
                                                        "  password TEXT NOT NULL,\n" +
                                                        "  fullname TEXT NOT NULL, \n" +
                                                        "  email TEXT NOT NULL, \n" +
                                                        "  points INTEGER DEFAULT 0,\n" +
                                                        "  avatar TEXT,\n" +
                                                        "  bio TEXT \n," +
                                                        "  banned INTEGER DEFAULT 0, \n" +
                                                        "  reportLevel INTEGER DEFAULT 0 \n" +
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

    public static final String SQL_CREATE_ENTRIES_REPORTS = "CREATE TABLE reports (\n" +
                                                            "  id INTEGER PRIMARY KEY,\n" +
                                                            "  reporter TEXT NOT NULL,\n" +
                                                            "  article_id INTEGER,\n" +
                                                            "  comment_id INTEGER,\n" +
                                                            "  reason TEXT NOT NULL,\n" +
                                                            "  FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                                                            "  FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                                                            ");";
    public static final int DB_VERSION = 9;

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

        db.execSQL(SQL_CREATE_ENTRIES_REPORTS);
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
            db.execSQL("DROP TABLE IF EXISTS comments");
            db.execSQL(SQL_CREATE_ENTRIES_COMMENT);
            db.execSQL(SQL_CREATE_ENTRIES_BOOKMARK);
            db.execSQL(SQL_CREATE_ENTRIES_ARTICLE_LIKE);
        }

        if (oldVersion < 8) {
            db.execSQL(SQL_CREATE_ENTRIES_REPORTS);
        }

        if (oldVersion < 9) {
            db.execSQL("DROP TABLE IF EXISTS bookmarks");
            db.execSQL(SQL_CREATE_ENTRIES_BOOKMARK);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
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
        db.insert("user", null, values);

        cursor.close();
        db.close();
        return true;
    }

    public int userAuthentication(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        // Check if the given username and password combination exists in the database
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username=? AND password=?", new String[]{username, password});

        if (cursor.moveToFirst()) {
            // If the user exists in the database, create a User object and return it
            @SuppressLint("Range") String fullname = cursor.getString(cursor.getColumnIndex("fullname"));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
            @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndex("points"));
            @SuppressLint("Range") String bio = cursor.getString(cursor.getColumnIndex("bio"));
            @SuppressLint("Range") String imageURL = cursor.getString(cursor.getColumnIndex("avatar"));
            @SuppressLint("Range") int banned = cursor.getInt(cursor.getColumnIndex("banned"));
            @SuppressLint("Range") int reportLevel = cursor.getInt(cursor.getColumnIndex("reportLevel"));
            User user = new User(username, fullname, points, bio, imageURL, banned, reportLevel, email);

            if (banned == 1) {
                return -1;
            }

            MainActivity.loggedInUser = user;

            cursor.close();
            db.close();
            return 1;
        } else {
            // If the user does not exist in the database, return null
            cursor.close();
            db.close();
            return 0;
        }
    }

    @SuppressLint("Range")
    public void increaseReportLevelForUser(String username) {
        SQLiteDatabase db = getWritableDatabase();

        // First, retrieve the current reportLevel for the specified user
        Cursor cursor = db.query("user", new String[]{"reportLevel"}, "username=?", new String[]{username}, null, null, null);

        int currentReportLevel = 0;
        if (cursor.moveToFirst()) {
            currentReportLevel = cursor.getInt(cursor.getColumnIndex("reportLevel"));
        }

        cursor.close();

        // Increment the reportLevel by 1
        ContentValues values = new ContentValues();
        values.put("reportLevel", currentReportLevel + 1);

        // Update the user row with the new reportLevel
        db.update("user", values, "username=?", new String[]{username});

        db.close();
    }

    public void banUser(String username) {
        SQLiteDatabase db = getWritableDatabase();

        // Set the 'banned' column to 1 for the specified user
        ContentValues values = new ContentValues();
        values.put("banned", 1);
        db.update("user", values, "username=?", new String[]{username});

        db.close();
    }


    public List<User> getAllUser() {
        SQLiteDatabase db = getReadableDatabase();
        List<User> userList = new ArrayList<>();

        // Query to select all users from the "user" table
        Cursor cursor = db.rawQuery("SELECT * FROM user", null);

        // Loop through the cursor to extract each user's information
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("username"));
            @SuppressLint("Range") String fullname = cursor.getString(cursor.getColumnIndex("fullname"));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
            @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndex("points"));
            @SuppressLint("Range") String bio = cursor.getString(cursor.getColumnIndex("bio"));
            @SuppressLint("Range") String imageURL = cursor.getString(cursor.getColumnIndex("avatar"));
            @SuppressLint("Range") int banned = cursor.getInt(cursor.getColumnIndex("banned"));
            @SuppressLint("Range") int reportLevel = cursor.getInt(cursor.getColumnIndex("reportLevel"));

            // Create a User object and add it to the list
            User user = new User(username, fullname, points, bio, imageURL, banned, reportLevel, email);
            userList.add(user);
        }

        // Close the cursor and database
        cursor.close();
        db.close();

        // Return the list of users
        return userList;
    }

    public User getUserWithUsername(String username) {
        SQLiteDatabase db = getWritableDatabase();

        // Check if the given username and password combination exists in the database
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username=?", new String[]{username});

        if (cursor.moveToFirst()) {
            // If the user exists in the database, create a User object and return it
            @SuppressLint("Range") String fullname = cursor.getString(cursor.getColumnIndex("fullname"));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
            @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndex("points"));
            @SuppressLint("Range") String bio = cursor.getString(cursor.getColumnIndex("bio"));
            @SuppressLint("Range") String imageURL = cursor.getString(cursor.getColumnIndex("avatar"));
            @SuppressLint("Range") int banned = cursor.getInt(cursor.getColumnIndex("banned"));
            @SuppressLint("Range") int reportLevel = cursor.getInt(cursor.getColumnIndex("reportLevel"));
            User user = new User(username, fullname, points, bio, imageURL, banned, reportLevel, email);

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

    public int getLastCommentID() {
        SQLiteDatabase db = this.getReadableDatabase();
        int maxID = (int) DatabaseUtils.longForQuery(db, "SELECT MAX(id) FROM comments", null);
        db.close();
        return maxID;
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

    public boolean editArticle(int articleId, String dishName, String publisher, String meal, String serve_order_class, String type, String recipe, String ingredients, String timeToMake, String imgURL) {
        SQLiteDatabase db = getWritableDatabase();

        // Create a new ContentValues object that contains the updated values for the article
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

        // Update the article in the database
        int rowsAffected = db.update("articles", values, "id=?", new String[]{String.valueOf(articleId)});

        db.close();

        // Check if the update was successful
        return rowsAffected > 0;
    }

    public boolean removeArticle(int articleID) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Define the WHERE clause to identify the article to be removed
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(articleID)};

        // Delete the article from the database
        int rowsDeleted = db.delete("articles", whereClause, whereArgs);

        db.close();

        // Return true if at least one row was deleted, indicating successful removal
        return rowsDeleted > 0;
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
                "id DESC"
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
                int likes = getTotalLikeCount(id);
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

    public List<Article> getNArticlesFromIndex(int startIndex, int count) {
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
        String sortOrder = "published_time DESC";
        String limitClause = startIndex + ", " + count; // limitClause specifies the range of articles to return
        Cursor cursor = db.query(
                "articles",
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                limitClause
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
                int likes = getTotalLikeCount(id);
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

    public Article getArticleWithId(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
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
        String selection = "id = ?";
        String[] selectionArgs = { String.valueOf(id) };
        Cursor cursor = db.query(
                "articles",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        Article article = null;
        try {
            if (cursor.moveToFirst()) {
                String dishName = cursor.getString(cursor.getColumnIndexOrThrow("dish_name"));
                String publisher = cursor.getString(cursor.getColumnIndexOrThrow("publisher"));
                String meal = cursor.getString(cursor.getColumnIndexOrThrow("meal"));
                String serveOrderClass = cursor.getString(cursor.getColumnIndexOrThrow("serve_order_class"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("recipe"));
                String ingredients = cursor.getString(cursor.getColumnIndexOrThrow("ingredients"));
                int likes = getTotalLikeCount(id);
                int comments = getTotalCommentCount(id);
                String publishedTime = cursor.getString(cursor.getColumnIndexOrThrow("published_time"));
                String timeToMake = cursor.getString(cursor.getColumnIndexOrThrow("time_to_make"));
                String imgURL = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                article = new Article(id, dishName, publisher, meal, serveOrderClass, type, content, ingredients, likes, comments, publishedTime, timeToMake, imgURL);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return article;
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
                int likes = getTotalLikeCount(id);
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
                int likes = getTotalLikeCount(id);
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

    public int getTotalLikeCount(int article_id) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(*) FROM article_likes WHERE article=?";

        String[] args = {String.valueOf(article_id)};

        Cursor cursor = db.rawQuery(query, args);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    public int getTotalArticleCount(String username) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(id) FROM articles WHERE publisher=?";

        String[] args = {username};

        Cursor cursor = db.rawQuery(query, args);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    public boolean reportComment(int comment_id, String reporter, String reason, int articleID) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("reporter", reporter);
        values.put("comment_id", comment_id);
        values.put("reason", reason);
        values.put("article_id", articleID);
        long result = db.insert("reports", null, values);
        return result != -1;
    }

    public boolean reportArticle(int articleID, String reporter, String reason) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("reporter", reporter);
        values.put("reason", reason);
        values.put("article_id", articleID);
        long result = db.insert("reports", null, values);
        return result != -1;
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
                "id",
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
                "id DESC"
        );


        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String commenter = cursor.getString(cursor.getColumnIndexOrThrow("commenter"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));

                Comment comment = new Comment(id, commenter, content, Integer.parseInt(articleID));
                commentList.add(comment);
            }
        } finally {
            cursor.close();
            db.close();
        }

        return commentList;
    }

    public boolean addComment(String articleID, String commenter, String content) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("article_id", articleID);
        values.put("commenter", commenter);
        values.put("content", content);

        long result = db.insert("comments", null, values);

        db.close();

        return result != -1;
    }

    public boolean removeComment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("comments", "id = ?", new String[]{String.valueOf(id)});
        db.close();

        return rowsAffected > 0;
    }

    public boolean checkLiked(String user, String articleID) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM article_likes WHERE user=? AND article=?", new String[] {user, String.valueOf(articleID)});

        boolean liked = cursor.moveToFirst();

        cursor.close();
        db.close();

        return liked;
    }

    public boolean addLike(String user, int articleID) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("user", user);
        values.put("article", articleID);

        long result = db.insert("article_likes", null, values);

        db.close();

        return result != -1;
    }

    public boolean removeLike(String user, int articleID) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = db.delete("article_likes", "user=? AND article=?", new String[] {user, String.valueOf(articleID)});

        db.close();

        return rowsDeleted > 0;
    }

    public List<Report> getAllCommentReports() {
        List<Report> reportList = new ArrayList<>();
        String selectQuery = "SELECT r.id, r.reporter, r.article_id, r.comment_id, r.reason, c.content " +
                "FROM reports r " +
                "LEFT JOIN comments c ON r.comment_id = c.id " +
                "WHERE r.comment_id IS NOT NULL";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String reporter = cursor.getString(cursor.getColumnIndex("reporter"));
                @SuppressLint("Range") int articleId = cursor.getInt(cursor.getColumnIndex("article_id"));
                @SuppressLint("Range") int commentId = cursor.getInt(cursor.getColumnIndex("comment_id"));
                @SuppressLint("Range") String reason = cursor.getString(cursor.getColumnIndex("reason"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));

                Report report = new Report(id, reporter, articleId, commentId, reason, null, content);
                reportList.add(report);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reportList;
    }

    public List<Report> getAllArticleReports() {
        List<Report> reportList = new ArrayList<>();
        String selectQuery = "SELECT r.id, r.reporter, r.article_id, a.dish_name, r.comment_id, r.reason " +
                "FROM reports r " +
                "LEFT JOIN articles a ON r.article_id = a.id " +
                "WHERE r.comment_id IS NULL";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String reporter = cursor.getString(cursor.getColumnIndex("reporter"));
                @SuppressLint("Range") int articleId = cursor.getInt(cursor.getColumnIndex("article_id"));
                @SuppressLint("Range") String dishName = cursor.getString(cursor.getColumnIndex("dish_name"));
                @SuppressLint("Range") int commentId = cursor.getInt(cursor.getColumnIndex("comment_id"));
                @SuppressLint("Range") String reason = cursor.getString(cursor.getColumnIndex("reason"));

                Report report = new Report(id, reporter, articleId, commentId, reason, dishName, null);
                reportList.add(report);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reportList;
    }

    @SuppressLint("Range")
    public int getArticleIDWithReportID(int reportID) {
        int articleID = -1;

        // Define the SQL query to join the comments and articles tables and retrieve the article ID
        String selectQuery = "SELECT articles.id AS article_id " +
                "FROM reports " +
                "INNER JOIN articles ON reports.article_id = articles.id " +
                "WHERE reports.id = ?";

        // Get a readable database object
        SQLiteDatabase db = this.getReadableDatabase();

        // Execute the query and get a cursor to retrieve the results
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(reportID)});

        // Check if the cursor contains any rows and retrieve the article ID
        if (cursor.moveToFirst()) {
            articleID = cursor.getInt(cursor.getColumnIndex("article_id"));
        }

        // Close the cursor and database objects
        cursor.close();
        db.close();

        // Return the article ID
        return articleID;
    }

    public boolean removeReport(int reportID) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Define the WHERE clause to identify the report to be removed
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(reportID)};

        // Delete the report from the database
        int rowsDeleted = db.delete("reports", whereClause, whereArgs);

        db.close();

        // Return true if at least one row was deleted, indicating successful removal
        return rowsDeleted > 0;
    }

    public boolean updateProfile(String username, String fullname,
                                 String bio, String avatarURL, boolean imageChanged) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("fullname", fullname);
        values.put("bio", bio);

        if (imageChanged) {
            values.put("avatar", avatarURL);
        }

        int cursor = db.update(
                "user",
                values,
                "username=?",
                new String[] {username}
        );

        return cursor > 0;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        Cursor cursor = db.rawQuery("SELECT password FROM user WHERE username=?", new String[] {username});
        if (cursor.moveToFirst()) {
            String currentPassword = "";
            currentPassword = cursor.getString(0);
            if (oldPassword.equals(currentPassword)) {
                values.put("password", newPassword);
            } else {
                return false;
            }
        }

        int updateRow = db.update(
                "user",
                values,
                "username=?",
                new String[] {username}
        );

        return updateRow > 0;
    }

    public boolean changePassword(String username, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int updateRow = db.update(
                "user",
                values,
                "username=?",
                new String[] {username}
        );

        return updateRow > 0;
    }

    public List<Integer> getUserProfileStats(String username) {
        List<Integer> userProfileStats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Get the number of recipes for the user
        String recipesQuery = "SELECT COUNT(*) FROM articles WHERE publisher = ?";
        String[] recipesArgs = {username};
        Cursor recipesCursor = db.rawQuery(recipesQuery, recipesArgs);
        int numRecipes = 0;
        if (recipesCursor.moveToFirst()) {
            numRecipes = recipesCursor.getInt(0);
        }
        recipesCursor.close();
        userProfileStats.add(numRecipes);

        // Get the number of followers for the user
        String followersQuery = "SELECT COUNT(*) FROM follows WHERE followed = ?";
        String[] followersArgs = {username};
        Cursor followersCursor = db.rawQuery(followersQuery, followersArgs);
        int numFollowers = 0;
        if (followersCursor.moveToFirst()) {
            numFollowers = followersCursor.getInt(0);
        }
        followersCursor.close();
        userProfileStats.add(numFollowers);

        // Get the number of likes accumulated by the user
        String likesQuery = "SELECT COUNT(*) FROM article_likes al " +
                "INNER JOIN articles a ON al.article = a.id " +
                "WHERE a.publisher = ?";
        String[] likesArgs = {username};
        Cursor likesCursor = db.rawQuery(likesQuery, likesArgs);
        int numLikes = 0;
        if (likesCursor.moveToFirst()) {
            numLikes = likesCursor.getInt(0);
        }
        likesCursor.close();
        userProfileStats.add(numLikes);

        db.close();

        return userProfileStats;
    }
}
