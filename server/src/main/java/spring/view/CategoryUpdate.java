package spring.view;

public class CategoryUpdate {
    public int action;
    public int categoryTypeId;
    public int categoryValueId;
    public String value;
    
    public CategoryUpdate(){
    }
    public CategoryUpdate(int action, int typeId, int valId, String value){
        this.action = action;
        this.categoryTypeId = typeId;
        this.categoryValueId = valId;
    }
    public void setAction(int action){
        this.action = action;
    }
    public void setCategoryTypeId(int id){
        this.categoryTypeId = id;
    }
    public void setCategoryValueId(int id){
        this.categoryValueId = id;
    }
    public void SetValue(String value){
        this.value = value;
    }
}
