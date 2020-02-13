package com.banfikristof.receptkonyv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    public String displayName;
    public Map<String, List<String>> images;

    public User() {
        // Ez az osztály az adatbázis miatt van
        images = new HashMap<>();
    }

    public User(String displayName) {
        images = new HashMap<>();
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, List<String>> getImages() {
        return images;
    }

    public void setImages(Map<String, List<String>> images) {
        this.images = images;
    }

    public void addImgList(List<String> imagesList, String key){
        images.put(key, imagesList);
    }
}
