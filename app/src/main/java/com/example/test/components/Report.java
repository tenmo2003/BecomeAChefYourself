package com.example.test.components;

public class Report {
    private int id;
    private String reporter;
    private int articleId;
    private int commentId;
    private String reason;

    private String articleName;
    private String commentContent;

    public Report(int id, String reporter, int articleId, int commentId, String reason, String articleName, String commentContent) {
        this.id = id;
        this.reporter = reporter;
        this.articleId = articleId;
        this.commentId = commentId;
        this.reason = reason;
        this.articleName = articleName;
        this.commentContent = commentContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
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

    public String getReason() {
        return reason;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
