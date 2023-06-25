package budget;

public enum Category {
    FOOD(1),
    CLOTHES(2),
    ENTERTAINMENT(3),
    OTHER(4);

    private final int value;

    private Category(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static int getValueByKey(String key) {
        Category[] categories = Category.values();
        for (Category category : categories) {
            if (category.name().equalsIgnoreCase(key)) {
                return category.getValue();
            }
        }

        throw new IllegalArgumentException("Invalid key: " + key);
    }

    public static Category getKeyByValue(int value) {
        for (Category category : Category.values()) {
            if (category.getValue() == value) {
                return category;
            }
        }

        throw new IllegalArgumentException("Invalid key: " + value);
    }
}
