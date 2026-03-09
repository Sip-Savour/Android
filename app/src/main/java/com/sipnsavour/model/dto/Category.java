package com.sipnsavour.model.dto;

public class Category {
    private String name;
    private boolean isExpanded;

    public Category(String name, boolean isExpanded) {
        this.name = name;
        this.isExpanded = isExpanded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
