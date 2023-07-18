package com.example.test.inventory;

import java.util.ArrayList;
import java.util.List;

public class Data {

    public static List<SelectRoll> getSelectList() {
        List<SelectRoll> selectList = new ArrayList<>();

        SelectRoll Meat = new SelectRoll();
        Meat.setName("Meat");
        selectList.add(Meat);

        SelectRoll Seafood = new SelectRoll();
        Seafood.setName("Seafood");
        selectList.add(Seafood);

        SelectRoll Vegetarianfood = new SelectRoll();
        Vegetarianfood.setName("Vegetarian food");
        selectList.add(Vegetarianfood);

        SelectRoll Soup = new SelectRoll();
        Soup.setName("Soup");
        selectList.add(Soup);

        SelectRoll Fruitsandvegetables = new SelectRoll();
        Fruitsandvegetables.setName("Fruits and vegetables");
        selectList.add(Fruitsandvegetables);

        SelectRoll Noodles = new SelectRoll();
        Noodles.setName("Noodles");
        selectList.add(Noodles);

        SelectRoll Ricenoodle = new SelectRoll();
        Ricenoodle.setName("Rice noodle");
        selectList.add(Ricenoodle);

        SelectRoll Roll = new SelectRoll();
        Roll.setName("Roll");
        selectList.add(Roll);

        SelectRoll Stickyrice = new SelectRoll();
        Stickyrice.setName("Sticky rice");
        selectList.add(Stickyrice);

        SelectRoll Rice = new SelectRoll();
        Rice.setName("Rice");
        selectList.add(Rice);

        SelectRoll Savorycakes = new SelectRoll();
        Savorycakes.setName("Savory cakes");
        selectList.add(Savorycakes);

        SelectRoll Sweetcakes = new SelectRoll();
        Sweetcakes.setName("Sweet cakes");
        selectList.add(Sweetcakes);

        return selectList;
    }
}
