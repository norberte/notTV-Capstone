package springbackend.view;

public class CategoryType {
    public String name;
    public CategoryValue[] values;

    public CategoryType(String name, CategoryValue[] values) {
	this.name = name;
	this.values = values;
    }
}
