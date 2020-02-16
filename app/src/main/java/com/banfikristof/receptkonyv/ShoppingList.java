package com.banfikristof.receptkonyv;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ShoppingList implements Serializable {

    @Exclude
    public String key;

    public String listName;
    private List<Map<String, String>> ingredients;

    public ShoppingList() {

    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<Map<String, String>> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Map<String, String>> ingredients) {
        this.ingredients = ingredients;
    }

    public String display() {
        String newText = "";
        for (Map<String,String> item : ingredients) {
            newText += item.get("amount") + " " + item.get("unit") + " " + item.get("name") + "\n";
        }
        return newText;
    }

    @Override
    public String toString() {
        return listName;
    }
}
