package com.example.test.utils;

import android.util.Log;

import com.example.test.activities.MainActivity;
import com.example.test.components.Article;
import com.example.test.components.Comment;
import com.example.test.components.InAppNotification;
import com.example.test.components.Report;
import com.example.test.components.User;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SQLConnection {
    private Connection connection;
    private String url;
    private String user;
    private String password;
    private boolean reconnectingNotification;
    private boolean reconnecting;

    int userID;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public boolean isReconnecting() {
        return reconnectingNotification;
    }

    public void setReconnecting(boolean reconnectingNotification) {
        this.reconnectingNotification = reconnectingNotification;
    }

    public Connection getConnection() {
        return connection;
    }

    public SQLConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        reconnecting = true;
    }

    /**
     * You need url to database server, username and password to log in database server.
     *
     * @see Connection
     * @since 1.0
     */
    public void connectServer() {
        while (connection == null) {
            try {
                Log.i("Database", "Connecting");
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                Log.i("Database", "Connection failed");
                reconnectingNotification = true;
                reconnecting = true;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Get result set of a SQL query.
     *
     * @since 1.0
     */
    public ResultSet getDataQuery(String query) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(query);
            e.printStackTrace();
        }
        return resultSet;
    }


    public int updateQuery(String query) {
        Statement statement;
        int rowsAffected = 0;
        try {
            statement = connection.createStatement();
            rowsAffected = statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rowsAffected;
    }

    public boolean checkUsernameAvailability(String username) {
        String query = "SELECT * FROM user WHERE username='" + username + "'";
        ResultSet resultSet = getDataQuery(query);

        boolean isUsernameAvailable = false;
        try {
            // Check if the username already exists in the database
            if (resultSet.next()) {
                isUsernameAvailable = false; // The username already exists
            } else {
                isUsernameAvailable = true; // The username is available
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isUsernameAvailable;
    }

    public boolean signUpUser(String email, String username, String password, String fullname) {
        if (!checkUsernameAvailability(username)) {
            return false; // Username already exists, cannot sign up
        }

        // The username does not exist, so we can add a new user with the given username, password, and fullname
        String insertQuery = "INSERT INTO user (email, username, password, fullname) VALUES ('" + email + "', '" + username + "', '" + Sha256Encryption.getSHA256Hash(password) + "', '" + fullname + "')";
        int rows = updateQuery(insertQuery);

        return rows > 0;
    }

    public int userAuthentication(String username, String password) {
        String query = "SELECT * FROM user WHERE username='" + username + "' AND password='" + Sha256Encryption.getSHA256Hash(password) + "'";
        ResultSet resultSet = getDataQuery(query);

        int authenticationResult = 0;
        try {
            if (resultSet.next()) {
                // If the user exists in the database, create a User object and return it
                String fullname = resultSet.getString("fullname");
                String email = resultSet.getString("email");
                int points = resultSet.getInt("points");
                String bio = resultSet.getString("bio");
                String imageURL = resultSet.getString("avatar");
                int banned = resultSet.getInt("banned");
                int reportLevel = resultSet.getInt("reportLevel");
                User user = new User(username, fullname, points, bio, imageURL, banned, reportLevel, email);

                if (banned == 1) {
                    authenticationResult = -1; // User is banned
                } else {
                    authenticationResult = 1; // Authentication success
                    MainActivity.loggedInUser = user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return authenticationResult;
    }

    public void increaseReportLevelForUser(String username) {
        String selectQuery = "SELECT reportLevel FROM user WHERE username='" + username + "'";
        ResultSet resultSet = getDataQuery(selectQuery);

        int currentReportLevel = 0;
        try {
            if (resultSet.next()) {
                currentReportLevel = resultSet.getInt("reportLevel");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Increment the reportLevel by 1
        int newReportLevel = currentReportLevel + 1;
        String updateQuery = "UPDATE user SET reportLevel=" + newReportLevel + " WHERE username='" + username + "'";
        int rows = updateQuery(updateQuery);
    }

    public void banUser(String username) {
        String updateQuery = "UPDATE user SET banned=1 WHERE username='" + username + "'";
        int rows = updateQuery(updateQuery);
    }

    public List<User> getAllUser() {
        String query = "SELECT * FROM user";
        ResultSet resultSet = getDataQuery(query);

        List<User> userList = new ArrayList<>();

        try {
            // Loop through the resultSet to extract each user's information
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String fullname = resultSet.getString("fullname");
                String email = resultSet.getString("email");
                int points = resultSet.getInt("points");
                String bio = resultSet.getString("bio");
                String imageURL = resultSet.getString("avatar");
                int banned = resultSet.getInt("banned");
                int reportLevel = resultSet.getInt("reportLevel");

                // Create a User object and add it to the list
                User user = new User(username, fullname, points, bio, imageURL, banned, reportLevel, email);
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return the list of users
        return userList;
    }

    public User getUserWithUsername(String username) {
        String query = "SELECT * FROM user WHERE username='" + username + "'";
        ResultSet resultSet = getDataQuery(query);

        User user = null;
        try {
            if (resultSet.next()) {
                String fullname = resultSet.getString("fullname");
                String email = resultSet.getString("email");
                int points = resultSet.getInt("points");
                String bio = resultSet.getString("bio");
                String imageURL = resultSet.getString("avatar");
                int banned = resultSet.getInt("banned");
                int reportLevel = resultSet.getInt("reportLevel");
                user = new User(username, fullname, points, bio, imageURL, banned, reportLevel, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public int getLastCommentID() {
        String query = "SELECT MAX(id) FROM comments";
        ResultSet resultSet = getDataQuery(query);

        int maxID = -1;
        try {
            if (resultSet.next()) {
                maxID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return maxID;
    }

    public boolean addArticle(String dishName, String publisher, String meal, String serve_order_class, String type, String recipe, String ingredients, String timeToMake, String imgURL) {
        String insertArticleQuery = "INSERT INTO articles (dish_name, publisher, meal, serve_order_class, type, recipe, ingredients, time_to_make, image) VALUES ('" +
                dishName + "', '" + publisher + "', '" + meal + "', '" + serve_order_class + "', '" + type + "', '" + recipe + "', '" + ingredients + "', '" + timeToMake + "', '" + imgURL + "')";

        int rowsAffected = updateQuery(insertArticleQuery);

        // Insert into notifications table for all followers
        if (rowsAffected > 0) {
            List<String> followers = getFollowersForUser(publisher);
            if (!followers.isEmpty()) {

                StringBuilder notificationQuery = new StringBuilder("INSERT INTO notifications (user, type, action_by, article_id, articleName) VALUES ");
                for (String follower : followers) {
                    notificationQuery.append("('").append(follower).append("', 'FOLLOWING_POST', '").append(publisher).append("', LAST_INSERT_ID(), '").append(dishName).append("'), ");
                }
                notificationQuery.deleteCharAt(notificationQuery.length() - 2); // Remove the trailing comma and space
                System.out.println(notificationQuery.toString());
                updateQuery(notificationQuery.toString());
            }
        }

        return rowsAffected > 0;
    }

    public List<String> getFollowersForUser(String username) {
        List<String> followers = new ArrayList<>();
        String followersQuery = "SELECT follower FROM follows WHERE followed = '" + username + "'";

        ResultSet resultSet = getDataQuery(followersQuery);
        try {
            while (resultSet.next()) {
                followers.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return followers;
    }

    public boolean editArticle(int articleId, String dishName, String publisher, String meal, String serve_order_class, String type, String recipe, String ingredients, String timeToMake, String imgURL) {
        String updateQuery = "UPDATE articles SET dish_name='" + dishName + "', publisher='" + publisher + "', recipe='" + recipe + "', ingredients='" + ingredients + "', meal='" + meal + "', serve_order_class='" + serve_order_class + "', type='" + type + "', time_to_make='" + timeToMake + "', image='" + imgURL + "' WHERE id=" + articleId;
        int rows = updateQuery(updateQuery);

        return rows > 0;
    }

    public boolean removeArticle(int articleID) {
        String publisherQuery = "SELECT publisher, dish_name FROM articles WHERE id=" + articleID;

        try {
            ResultSet resultSet = getDataQuery(publisherQuery);
            String publisher = "";
            String dishName = "";
            if (resultSet.next()) {
                publisher = resultSet.getString("publisher");
                dishName = resultSet.getString("dish_name");
            }
            resultSet.close();

            if (!publisher.isEmpty()) {
                String deleteQuery = "DELETE FROM articles WHERE id=" + articleID;
                int rowsAffected = updateQuery(deleteQuery);

                if (rowsAffected > 0 && MainActivity.loggedInUser.getUsername().equals("admin")) {
                    String notificationQuery = "INSERT INTO notifications (user, type, action_by, article_id, articleName) VALUES ('" +
                            publisher + "', 'REPORT_ARTICLE', 'admin', " + articleID + ", '" + dishName + "')";
                    updateQuery(notificationQuery);

                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }


    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();

        String query = "SELECT * FROM articles ORDER BY id DESC";
        ResultSet resultSet = getDataQuery(query);

        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String dishName = resultSet.getString("dish_name");
                String publisher = resultSet.getString("publisher");
                String meal = resultSet.getString("meal");
                String serveOrderClass = resultSet.getString("serve_order_class");
                String type = resultSet.getString("type");
                String content = resultSet.getString("recipe");
                String ingredients = resultSet.getString("ingredients");
                int likes = getTotalLikeCount(id);
                int comments = getTotalCommentCount(id);
                String publishedTime = resultSet.getString("published_time");
                publishedTime = publishedTime.substring(0, publishedTime.length() - 2);
                String timeToMake = resultSet.getString("time_to_make");
                String imgURL = resultSet.getString("image");
                Article article = new Article(id, dishName, publisher, meal, serveOrderClass, type, content, ingredients, likes, comments, publishedTime, timeToMake, imgURL);
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }

    public List<Article> getNArticlesFromIndex(int startIndex, int count) {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT * FROM articles ORDER BY published_time DESC LIMIT " + startIndex + ", " + count;
        ResultSet resultSet = getDataQuery(query);

        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String dishName = resultSet.getString("dish_name");
                String publisher = resultSet.getString("publisher");
                String meal = resultSet.getString("meal");
                String serveOrderClass = resultSet.getString("serve_order_class");
                String type = resultSet.getString("type");
                String content = resultSet.getString("recipe");
                String ingredients = resultSet.getString("ingredients");
                int likes = getTotalLikeCount(id);
                int comments = getTotalCommentCount(id);
                String publishedTime = resultSet.getString("published_time");
                publishedTime = publishedTime.substring(0, publishedTime.length() - 2);
                String timeToMake = resultSet.getString("time_to_make");
                String imgURL = resultSet.getString("image");
                Article article = new Article(id, dishName, publisher, meal, serveOrderClass, type, content, ingredients, likes, comments, publishedTime, timeToMake, imgURL);
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }

    public Article getArticleWithId(int id) {
        String query = "SELECT * FROM articles WHERE id=" + id;
        ResultSet resultSet = getDataQuery(query);
        Article article = null;

        try {
            if (resultSet.next()) {
                String dishName = resultSet.getString("dish_name");
                String publisher = resultSet.getString("publisher");
                String meal = resultSet.getString("meal");
                String serveOrderClass = resultSet.getString("serve_order_class");
                String type = resultSet.getString("type");
                String content = resultSet.getString("recipe");
                String ingredients = resultSet.getString("ingredients");
                int likes = getTotalLikeCount(id);
                int comments = getTotalCommentCount(id);
                String publishedTime = resultSet.getString("published_time");
                publishedTime = publishedTime.substring(0, publishedTime.length() - 2);
                String timeToMake = resultSet.getString("time_to_make");
                String imgURL = resultSet.getString("image");
                article = new Article(id, dishName, publisher, meal, serveOrderClass, type, content, ingredients, likes, comments, publishedTime, timeToMake, imgURL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return article;
    }

    public List<Article> getArticlesFromUser(String username) {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT * FROM articles WHERE publisher='" + username + "' ORDER BY published_time DESC";
        ResultSet resultSet = getDataQuery(query);

        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String dishName = resultSet.getString("dish_name");
                String publisher = resultSet.getString("publisher");
                String meal = resultSet.getString("meal");
                String serveOrderClass = resultSet.getString("serve_order_class");
                String type = resultSet.getString("type");
                String content = resultSet.getString("recipe");
                String ingredients = resultSet.getString("ingredients");
                int likes = getTotalLikeCount(id);
                int comments = getTotalCommentCount(id);
                String publishedTime = resultSet.getString("published_time");
                publishedTime = publishedTime.substring(0, publishedTime.length() - 2);
                String timeToMake = resultSet.getString("time_to_make");
                String imgURL = resultSet.getString("image");
                Article article = new Article(id, dishName, publisher, meal, serveOrderClass, type, content, ingredients, likes, comments, publishedTime, timeToMake, imgURL);
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }

    public List<Article> getUserSavedArticles(String username) {
        List<Article> savedArticles = new ArrayList<>();
        String query = "SELECT articles.id, dish_name, publisher, meal, serve_order_class, type, recipe, ingredients, likes, published_time, time_to_make, image FROM articles INNER JOIN bookmarks ON articles.id = bookmarks.article WHERE bookmarks.user='" + username + "' ORDER BY published_time DESC";
        ResultSet resultSet = getDataQuery(query);

        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String dishName = resultSet.getString("dish_name");
                String publisher = resultSet.getString("publisher");
                String meal = resultSet.getString("meal");
                String serveOrderClass = resultSet.getString("serve_order_class");
                String type = resultSet.getString("type");
                String content = resultSet.getString("recipe");
                String ingredients = resultSet.getString("ingredients");
                int likes = getTotalLikeCount(id);
                int comments = getTotalCommentCount(id);
                String publishedTime = resultSet.getString("published_time");
                publishedTime = publishedTime.substring(0, publishedTime.length() - 2);
                String timeToMake = resultSet.getString("time_to_make");
                String imgURL = resultSet.getString("image");
                Article article = new Article(id, dishName, publisher, meal, serveOrderClass, type, content, ingredients, likes, comments, publishedTime, timeToMake, imgURL);
                savedArticles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return savedArticles;
    }

    public boolean addFollow(String follower, String followed) {
        String query = "INSERT INTO follows (follower, followed) VALUES ('" + follower + "', '" + followed + "')";
        int rows = updateQuery(query);

        if (rows > 0) {
            // Insert a notification for the user being followed
            String notificationQuery = "INSERT INTO notifications (user, type, action_by) VALUES ('" +
                    followed + "', 'FOLLOW', '" + follower + "')";
            updateQuery(notificationQuery);

            return true;
        }

        return false;
    }


    public boolean removeFollow(String follower, String followed) {
        String query = "DELETE FROM follows WHERE follower='" + follower + "' AND followed='" + followed + "'";
        int rows = updateQuery(query);
        return rows > 0;
    }

    public boolean isFollowing(String follower, String followed) {
        String query = "SELECT * FROM follows WHERE follower='" + follower + "' AND followed='" + followed + "'";
        ResultSet resultSet = getDataQuery(query);
        boolean isFollowing = false;

        try {
            isFollowing = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isFollowing;
    }

    public int getTotalFollowCount(String username) {
        String query = "SELECT COUNT(*) FROM follows WHERE followed='" + username + "'";
        ResultSet resultSet = getDataQuery(query);
        int count = 0;

        try {
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int getTotalCommentCount(int article_id) {
        String query = "SELECT COUNT(*) FROM comments WHERE article_id=" + article_id;
        ResultSet resultSet = getDataQuery(query);
        int count = 0;

        try {
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int getTotalLikeCount(int article_id) {
        String query = "SELECT COUNT(*) FROM article_likes WHERE article=" + article_id;
        ResultSet resultSet = getDataQuery(query);
        int count = 0;

        try {
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int getTotalArticleCount(String username) {
        String query = "SELECT COUNT(id) FROM articles WHERE publisher='" + username + "'";
        ResultSet resultSet = getDataQuery(query);
        int count = 0;

        try {
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public boolean reportComment(int comment_id, String reporter, String reason, int articleID) {
        String query = "INSERT INTO reports (comment_id, reporter, reason, article_id) VALUES ("
                + comment_id + ", '" + reporter + "', '" + reason + "', " + articleID + ")";
        int rows = updateQuery(query);
        return rows > 0;
    }

    public boolean reportArticle(int articleID, String reporter, String reason) {
        String query = "INSERT INTO reports (article_id, reporter, reason) VALUES ("
                + articleID + ", '" + reporter + "', '" + reason + "')";
        int rows = updateQuery(query);
        return rows > 0;
    }

    public boolean addBookmark(String user, int articleID) {
        String query = "INSERT INTO bookmarks (user, article) VALUES ('" + user + "', " + articleID + ")";
        int rows = updateQuery(query);
        return rows > 0;
    }

    public boolean removeBookmark(String user, int articleID) {
        String query = "DELETE FROM bookmarks WHERE user='" + user + "' AND article=" + articleID;
        int rows = updateQuery(query);
        return rows > 0;
    }

    public boolean checkBookmarked(String user, int articleID) {
        String query = "SELECT * FROM bookmarks WHERE user='" + user + "' AND article=" + articleID;
        ResultSet resultSet = getDataQuery(query);
        boolean bookmarked = false;

        try {
            bookmarked = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookmarked;
    }

    public List<Comment> getCommentWithArticleID(String articleID) {
        List<Comment> commentList = new ArrayList<>();
        String query = "SELECT id, commenter, content FROM comments WHERE article_id=" + articleID + " ORDER BY id DESC";
        ResultSet resultSet = getDataQuery(query);

        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String commenter = resultSet.getString("commenter");
                String content = resultSet.getString("content");

                Comment comment = new Comment(id, commenter, content, Integer.parseInt(articleID));
                commentList.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commentList;
    }

    public boolean addComment(String articleID, String commenter, String content) {
        String query = "INSERT INTO comments (article_id, commenter, content) VALUES ("
                + articleID + ", '" + commenter + "', '" + content + "')";
        int rows = updateQuery(query);

        if (rows > 0) {
            // Get the publisher of the article
            String publisherQuery = "SELECT publisher FROM articles WHERE id=" + articleID;

            try {
                ResultSet resultSet = getDataQuery(publisherQuery);
                String publisher = "";
                if (resultSet.next()) {
                    publisher = resultSet.getString("publisher");
                }
                resultSet.close();

                if (!publisher.isEmpty()) {
                    // Insert a notification for the publisher
                    String notificationQuery = "INSERT INTO notifications (user, type, action_by, article_id, comment_id, commentContent) VALUES ('" +
                            publisher + "', 'COMMENT', '" + commenter + "', " + articleID + ", LAST_INSERT_ID(), '" + content + "')";
                    updateQuery(notificationQuery);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // In case of an exception, the comment is already added, but the notification is not, so we can return true here.
                return true;
            }

            return true;
        }

        return false;
    }


    public boolean removeComment(int id) {
        String commenterQuery = "SELECT commenter, content, article_id FROM comments WHERE id = " + id;
        ResultSet rs = getDataQuery(commenterQuery);
        String commenter, commentContent;
        int articleID;

        try {
            if (rs.next()) {
                commenter = rs.getString(1);
                commentContent = rs.getString(2);
                articleID = rs.getInt(3);
                String query = "DELETE FROM comments WHERE id=" + id;
                int rows = updateQuery(query);
                if (rows > 0 && MainActivity.loggedInUser.getUsername().equals("admin")) {
                    String notificationQuery = "INSERT INTO notifications (user, type, action_by, article_id, commentContent) VALUES ('" +
                            commenter + "', 'REPORT_COMMENT', 'admin', " + articleID + ", '" + commentContent + "')";
                    updateQuery(notificationQuery);

                    return true;
                }
                return rows > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public boolean checkLiked(String user, String articleID) {
        String query = "SELECT * FROM article_likes WHERE user='" + user + "' AND article=" + articleID;
        ResultSet resultSet = getDataQuery(query);
        boolean liked = false;

        try {
            liked = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return liked;
    }

    public boolean addLike(String user, int articleID) {
        String query = "INSERT INTO article_likes (user, article) VALUES ('" + user + "', " + articleID + ")";
        int rows = updateQuery(query);

        if (rows > 0) {
            // Get the publisher of the article
            String publisherQuery = "SELECT publisher, dish_name FROM articles WHERE id = " + articleID;
            ResultSet resultSet = getDataQuery(publisherQuery);
            String publisher = null;
            String dishName = null;
            try {
                if (resultSet.next()) {
                    publisher = resultSet.getString(1);
                    dishName = resultSet.getString(2);
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (publisher != null) {
                // Insert a notification for the publisher of the article
                String notificationQuery = "INSERT INTO notifications (user, type, action_by, article_id, articleName) VALUES ('" +
                        publisher + "', 'LIKE', '" + user + "', " + articleID + ", '" + dishName + "')";
                updateQuery(notificationQuery);
            }

            return true;
        }

        return false;
    }


    public boolean removeLike(String user, int articleID) {
        String query = "DELETE FROM article_likes WHERE user='" + user + "' AND article=" + articleID;
        int rows = updateQuery(query);
        return rows > 0;
    }

    public List<Report> getAllCommentReports() {
        List<Report> reportList = new ArrayList<>();
        String selectQuery = "SELECT r.id, r.reporter, r.article_id, r.comment_id, r.reason, c.content " +
                "FROM reports r " +
                "LEFT JOIN comments c ON r.comment_id = c.id " +
                "WHERE r.comment_id IS NOT NULL";

        ResultSet resultSet = getDataQuery(selectQuery);

        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String reporter = resultSet.getString("reporter");
                int articleId = resultSet.getInt("article_id");
                int commentId = resultSet.getInt("comment_id");
                String reason = resultSet.getString("reason");
                String content = resultSet.getString("content");

                Report report = new Report(id, reporter, articleId, commentId, reason, null, content);
                reportList.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportList;
    }


    public List<Report> getAllArticleReports() {
        List<Report> reportList = new ArrayList<>();
        String selectQuery = "SELECT r.id, r.reporter, r.article_id, a.dish_name, r.comment_id, r.reason " +
                "FROM reports r " +
                "LEFT JOIN articles a ON r.article_id = a.id " +
                "WHERE r.comment_id IS NULL";

        ResultSet resultSet = getDataQuery(selectQuery);

        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String reporter = resultSet.getString("reporter");
                int articleId = resultSet.getInt("article_id");
                String dishName = resultSet.getString("dish_name");
                int commentId = resultSet.getInt("comment_id");
                String reason = resultSet.getString("reason");

                Report report = new Report(id, reporter, articleId, commentId, reason, dishName, null);
                reportList.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportList;
    }

    public int getArticleIDWithReportID(int reportID) {
        int articleID = -1;

        String selectQuery = "SELECT articles.id AS article_id " +
                "FROM reports " +
                "INNER JOIN articles ON reports.article_id = articles.id " +
                "WHERE reports.id = " + reportID;

        ResultSet resultSet = getDataQuery(selectQuery);

        try {
            if (resultSet.next()) {
                articleID = resultSet.getInt("article_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articleID;
    }

    public boolean removeReport(int reportID) {
        String query = "DELETE FROM reports WHERE id=" + reportID;
        int rows = updateQuery(query);
        return rows > 0;
    }

    public boolean updateProfile(String username, String fullname, String bio, String avatarURL, boolean imageChanged) {
        String query;
        if (imageChanged) {
            query = "UPDATE user SET fullname='" + fullname + "', bio='" + bio + "', avatar='" + avatarURL + "' WHERE username='" + username + "'";
        } else {
            query = "UPDATE user SET fullname='" + fullname + "', bio='" + bio + "' WHERE username='" + username + "'";
        }

        int rows = updateQuery(query);

        return rows > 0;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        String query = "SELECT password FROM user WHERE username='" + username + "'";
        ResultSet resultSet = getDataQuery(query);

        try {
            if (resultSet.next()) {
                String currentPassword = resultSet.getString("password");
                if (Sha256Encryption.getSHA256Hash(oldPassword).equals(currentPassword)) {
                    query = "UPDATE user SET password='" + Sha256Encryption.getSHA256Hash(newPassword) + "' WHERE username='" + username + "'";
                    int rows = updateQuery(query);
                    return rows > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean changePassword(String username, String newPassword) {
        String query = "UPDATE user SET password='" + Sha256Encryption.getSHA256Hash(newPassword) + "' WHERE username='" + username + "'";
        int rows = updateQuery(query);
        return rows > 0;
    }

    public List<Integer> getUserProfileStats(String username) {
        List<Integer> userProfileStats = new ArrayList<>();
        String recipesQuery = "SELECT COUNT(*) FROM articles WHERE publisher = '" + username + "'";
        String followersQuery = "SELECT COUNT(*) FROM follows WHERE followed = '" + username + "'";
        String likesQuery = "SELECT COUNT(*) FROM article_likes al " +
                "INNER JOIN articles a ON al.article = a.id " +
                "WHERE a.publisher = '" + username + "'";

        int numRecipes = 0;
        int numFollowers = 0;
        int numLikes = 0;

        ResultSet resultSet;

        // Get the number of recipes for the user
        resultSet = getDataQuery(recipesQuery);
        try {
            if (resultSet.next()) {
                numRecipes = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        userProfileStats.add(numRecipes);

        // Get the number of followers for the user
        resultSet = getDataQuery(followersQuery);
        try {
            if (resultSet.next()) {
                numFollowers = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        userProfileStats.add(numFollowers);

        // Get the number of likes accumulated by the user
        resultSet = getDataQuery(likesQuery);
        try {
            if (resultSet.next()) {
                numLikes = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        userProfileStats.add(numLikes);

        return userProfileStats;
    }

    public List<InAppNotification> getNotificationsForUser(String username) {
        List<InAppNotification> notificationList = new ArrayList<>();

        String query = "SELECT id, user, type, action_by, article_id, comment_id, created_time, commentContent, articleName " +
                "FROM notifications WHERE user='" + username + "' ORDER BY created_time DESC";
        try (ResultSet rs = getDataQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String user = rs.getString("user");
                String type = rs.getString("type");
                String actionBy = rs.getString("action_by");
                int articleID = rs.getInt("article_id");
                int commentID = rs.getInt("comment_id");
                String createdTime = rs.getString("created_time");
                createdTime = createdTime.substring(0, createdTime.length() - 2);
                String commentContent = rs.getString("commentContent");
                String articleName = rs.getString("articleName");

                InAppNotification notification = new InAppNotification(id, user, type, actionBy, articleID, commentID, createdTime, commentContent, articleName);
                notificationList.add(notification);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return notificationList;
    }


    public boolean removeNotification(int id) {
        String query = "DELETE FROM notifications WHERE id=" + id;
        int rows = updateQuery(query);

        return rows > 0;
    }

    public Comment getCommentWithID(int commentId) {
        String query = "SELECT id, article_id, commenter, content FROM comments WHERE id = " + commentId;

        ResultSet resultSet = getDataQuery(query);

        Comment comment = null;
        try {
            if (resultSet != null && resultSet.next()) {
                int id = resultSet.getInt("id");
                int articleId = resultSet.getInt("article_id");
                String commenter = resultSet.getString("commenter");
                String content = resultSet.getString("content");
                // Add other fields if needed

                comment = new Comment(id, commenter, content, articleId);
                // Set other fields of the comment if added
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return comment;
    }

    private String postedTime(String publishedTime) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            int periodAsSecond = subTime(publishedTime, dateTimeFormatter.format(now));

            int days = periodAsSecond / 3600 / 24;
            if (days >= 3) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM");

                Date date = null;
                try {
                    date = inputFormat.parse(publishedTime);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                // Format the Date object to the desired output format
                assert date != null;
                return outputFormat.format(date);
            }

            if (days >= 1) {
                return days + " ngày trước";
            }

            if (periodAsSecond >= 3600) {
                return periodAsSecond / 3600 + " giờ trước";
            }
            if (periodAsSecond >= 60) {
                return periodAsSecond / 60 + " phút trước";
            }
            return "Vừa xong";
        }
        return "Đã từ rất lâu";
    }

    private int subTime(String past, String now) {
        //DateTime format yyyy-MM-dd HH:mm:ss

        //Convert DateTime to array
        List<String> pastDate = Arrays.asList(past.split(" ")[0].split("-"));
        List<String> pastTime = Arrays.asList(past.split(" ")[1].split(":"));
        List<String> pastList = new ArrayList<>();
        pastList.addAll(pastDate);
        pastList.addAll(pastTime);

        List<String> nowDate = Arrays.asList(now.split(" ")[0].split("-"));
        List<String> nowTime = Arrays.asList(now.split(" ")[1].split(":"));
        List<String> nowList = new ArrayList<>();
        nowList.addAll(nowDate);
        nowList.addAll(nowTime);

        //Change time from GMT+0 to GMT+7
        //pastList.set(3, String.valueOf(Integer.parseInt(pastList.get(4)) + 7));
        //Sub year for year, month for month,...
        int[] period = new int[6];
        for (int i = 0; i < 6; i++) {
            period[i] = Integer.parseInt(nowList.get(i)) - Integer.parseInt(pastList.get(i));
        }

        return (period[0] * 365 + period[1] * 30 + period[2]) * 24 * 3600
                + (period[3] * 3600 + period[4] * 60 + period[5]);
    }



    /**
     * Close connection to SQL Server.
     *
     * @since 1.0
     */
    public void addClosingWork() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }
}
