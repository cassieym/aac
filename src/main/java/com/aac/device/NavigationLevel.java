package com.aac.device;

public enum NavigationLevel {
    CATEGORY_GROUP("category_group"),
    CATEGORY("category"),
    CARD("card");


    private final String value;

    NavigationLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static NavigationLevel forValue(String value) {

        if (value == null || value.isBlank()) return NavigationLevel.CATEGORY_GROUP;

        for (NavigationLevel type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return NavigationLevel.CATEGORY_GROUP;
    }
}
