package com.banfikristof.receptkonyv;

import java.io.Serializable;
import java.util.List;

public class Recipe implements Serializable {
    private String id;
    private String name, description, preparation;
    private List<String> ingredients;

    public Recipe(String name, String preparation, List<String> ingredients) {
        this.name = name;
        this.description = " ";
        this.preparation = preparation;
        this.ingredients = ingredients;
    }

    public Recipe(String name, String description, String preparation, List<String> ingredients) {
        this.name = name;
        this.description = description;
        this.preparation = preparation;
        this.ingredients = ingredients;
    }

    public Recipe(String id, String name, String description, String preparation, List<String> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.preparation = preparation;
        this.ingredients = ingredients;
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

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getIngredientsString() {
        String s = "";
        for (String i : ingredients) {
            s += i+"\n";
        }
        return s;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(String ingredient) {
        this.ingredients.add(ingredient);
    }

    public boolean removeIngredient(String ingredient) {
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
