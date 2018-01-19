package spring.model;

import spring.view.CategoryValue;

// CategoryType model object for Spring back-end
public class CategoryType {
    private String name;
    private CategoryValue[] values;

    public CategoryType(String name, CategoryValue[] values) {
        this.name = name;
        this.values = values;
    }
}
