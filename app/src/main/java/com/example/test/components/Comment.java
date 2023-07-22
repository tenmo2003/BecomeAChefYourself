package com.example.test.components;

public class Comment {
    private int id;
    private String commenter;
    private String content;

    private int articleID;

    public Comment(int id, String commenter, String content, int articleID) {
        this.id = id;
        this.commenter = commenter;
        this.content = content;
        this.articleID = articleID;
    }

    public int getArticleID() {
        return articleID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
