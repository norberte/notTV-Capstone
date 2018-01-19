package spring.model;

//CategoryValue model object for Spring back-end
public class CategoryValue {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryValue(int id, String name) {
    this.id = id;
    this.name = name;
    }

    @Override
    public String toString() {
        return "CategoryValue [id=" + id + ", name=" + name + "]";
    }
}
