package com.banfikristof.receptkonyv;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recipe implements Serializable {

    @Exclude
    public String key;

    @Exclude
    public Bitmap mainImg;

    private String id, uid;
    private String name, description;
    private List<Map<String, String>> ingredients;
    private List<String> tags;
    private List<String> preparation;
    private Map<String,String> pictures;
    private boolean hasMainImg;
    private boolean favourite;

    public Recipe() {

    }

    public Recipe(String id) {
        this.id = id;
        this.hasMainImg = true;
    }

    public Recipe(String name, List<String> preparation, List<Map<String,String>> ingredients) {
        this.name = name;
        this.description = " ";
        this.preparation = preparation;
        this.ingredients = ingredients;
    }

    public Recipe(String name, String description, List<String> preparation, List<Map<String,String>> ingredients) {
        this.name = name;
        this.description = description;
        this.preparation = preparation;
        this.ingredients = ingredients;
    }

    public Recipe(String id, String name, String description, List<String> preparation, List<Map<String,String>> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.preparation = preparation;
        this.ingredients = ingredients;
    }

    public Recipe(String id, String name, String description, List<String> preparation, List<Map<String,String>> ingredients, String uid, List<String> tags) {
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

    public List<String> getPreparation() {
        return preparation;
    }

    public void setPreparation(List<String> preparation) {
        this.preparation = preparation;
    }

    public String preparationAsString() {
        if (preparation.size() == 1){
            return preparation.get(0);
        }
        String string = "";
        for (int i = 0; i < preparation.size(); i++) {
            string += (i+1)+". "+preparation.get(i)+"\n";
        }
        return string;
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

    public Map<String,String> getPictures() {
        if (pictures == null) {
            pictures = new HashMap<>(); //Ha nincs akkor mostmár van
        }
        return pictures;
    }

    public void setPictures(Map<String,String> pictures) {
        this.pictures = pictures;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isHasMainImg() {
        return hasMainImg;
    }

    public void setHasMainImg(boolean hasMainImg) {
        this.hasMainImg = hasMainImg;
    }

    /*public void addIngredient(Map<String,String> ingredient) {
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
    }*/

    @Override
    public String toString() {
        return name;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
