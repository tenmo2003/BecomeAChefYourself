package com.example.test.components;

public class Notification {
    private int id;
    private String user;
    private String type;
    private String actionBy;
    private int articleId;
    private int commentId;
    private String createdTime;
    private String commentContent;
    private String articleName;

    // Constructor
    public Notification(int id, String user, String type, String actionBy, int articleId, int commentId, String createdTime, String commentContent, String articleName) {
        this.id = id;
        this.user = user;
        this.type = type;
        this.actionBy = actionBy;
        this.articleId = articleId;
        this.commentId = commentId;
        this.createdTime = createdTime;
        this.commentContent = commentContent;
        this.articleName = articleName;
    }

    // Getters and Setters (Optional if you use Lombok or IDE-generated methods)

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActionBy() {
        return actionBy;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }


    @Override
    public String toString() {
        String actionMessage;
        switch (type) {
            case "LIKE":
                actionMessage = actionBy + " đã thích bài viết '" + articleName + "' của bạn     " + createdTime;
                break;
            case "COMMENT":
                actionMessage = actionBy + " đã bình luận '" + commentContent + "'     " + createdTime;
                break;
            case "FOLLOW":
                actionMessage = actionBy + " đã theo dõi bạn        " + createdTime;
                break;
            // Add more cases for other types if needed
            default:
                actionMessage = "Notification type: " + type;
                break;
        }

        return actionMessage;
    }
}
