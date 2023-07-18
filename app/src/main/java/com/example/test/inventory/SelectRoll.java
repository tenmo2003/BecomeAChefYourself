package com.example.test.inventory;

import java.io.Serializable;

public class SelectRoll implements Serializable {
    private String name;

    public SelectRoll() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
