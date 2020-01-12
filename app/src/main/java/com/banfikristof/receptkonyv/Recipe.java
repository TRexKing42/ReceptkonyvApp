package com.banfikristof.receptkonyv;

import android.text.TextUtils;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Recipe implements Serializable {

    @Exclude
    public String key;

    private String id, uid;
    private String name, description, preparation;
    private List<Map<String,String>> ingredients;
    private List<String> tags;
    private boolean onlineStored;

    public Recipe() {

    }

    public Recipe(String id){
        this.id = id;
        this.onlineStored = true;
    }

    public Recipe(String name, String preparation, List<Map<String,String>> ingredients) {
        this.name = name;
        this.description = " ";
        this.preparation = preparation;
        this.ingredients = ingredients;
    }

    public Recipe(String name, String description, String preparation, List<Map<String,String>> ingredients) {
        this.name = name;
        this.description = description;
        this.preparation = preparation;
        this.ingredients = ingredients;
    }

    public Recipe(String id, String name, String description, String preparation, List<Map<String,String>> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.preparation = preparation;
        this.ingredients = ingredients;
    }

    public Recipe(String id, String name, String description, String preparation, List<Map<String,String>> ingredients, String uid, List<String> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.preparation = preparation;
        this.ingredients = ingredients;
        this.uid = uid;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Map<String,String>> getIngredients() {
        return ingredients;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String ingredientsToString() {
        String s = "";
        for (Map<String,String> i : ingredients) {
            s += i.get("amount") + " ";
            s += i.get("unit") + " ";
            s += i.get("name");
            s += "\n";
        }
        return s;
    }

    public String tagsToString() {
        String s = TextUtils.join(", ",tags);
        return s;
    }

    public void setIngredients(List<Map<String,String>> ingredients) {
        this.ingredients = ingredients;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isOnlineStored() {
        return onlineStored;
    }

    public void setOnlineStored(boolean onlineStored) {
        this.onlineStored = onlineStored;
    }

    public void addIngredient(Map<String,String> ingredient) {
        this.ingredients.add(ingredient);
    }

    public boolean removeIngredient(Map<String,String> ingredient) {
        if(!this.ingredients.contains(ingredient)) {
            return false;
        } else {
            int i = this.ingredients.indexOf(ingredient);
            this.ingredients.remove(i);
            return true;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
