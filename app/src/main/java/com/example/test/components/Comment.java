package com.example.test.components;

public class Comment {
    private String commenter;
    private String content;

    public Comment(String commenter, String content) {
        this.commenter = commenter;
        this.content = content;
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
