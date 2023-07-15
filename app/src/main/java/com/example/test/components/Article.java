package com.example.test.components;

public class Article {
    private int id;
    private String dishName;
    private String publisher;
    private String meal;
    private String serveOrderClass;
    private String type;
    private String recipe;
    private String ingredients;
    private int likes;
    private String publishedTime;
    private String timeToMake;

    public Article(int id, String dishName, String publisher, String meal, String serveOrderClass, String type, String content, String ingredients, int likes, String publishedTime, String timeToMake) {
        this.id = id;
        this.dishName = dishName;
        this.publisher = publisher;
        this.meal = meal;
        this.serveOrderClass = serveOrderClass;
        this.type = type;
        this.recipe = content;
        this.ingredients = ingredients;
        this.likes = likes;
        this.publishedTime = publishedTime;
        this.timeToMake = timeToMake;
    }

    public int getId() {
        return id;
    }

    public String getDishName() {
        return dishName;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getMeal() {
        return meal;
    }

    public String getServeOrderClass() {
        return serveOrderClass;
    }

    public String getType() {
        return type;
    }

    public String getRecipe() {
        return recipe;
    }

    public int getLikes() {
        return likes;
    }

    public String getPublishedTime() {
        return publishedTime;
    }

    public String getTimeToMake() {
        return timeToMake;
    }

    public String getIngredients() {
        return ingredients;
    }
}