package spring.view;

import java.util.List;

public class CategoryType {
    public int id;
    public String name;
    public List<CategoryValue> values;

    public CategoryType(int id, String name, List<CategoryValue> values) {
        this.id = id;
        this.name = name;
        this.values = values;
    }
}
