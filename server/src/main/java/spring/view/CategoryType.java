package spring.view;

import java.util.List;

public class CategoryType {
    public String name;
    public List<CategoryValue> values;

    public CategoryType(String name, List<CategoryValue> values) {
	this.name = name;
	this.values = values;
    }
}
