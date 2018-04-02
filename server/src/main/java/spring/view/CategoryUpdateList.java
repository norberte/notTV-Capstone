package spring.view;

import java.util.List;

public class CategoryUpdateList {
    public List<CategoryUpdate> updateList;
    
    public CategoryUpdateList(){
    }
    public CategoryUpdateList(List<CategoryUpdate> updateList){
        this.updateList = updateList;
    }
    public List<CategoryUpdate> getUpdateList(){
        return updateList;
    }
    public void setUpdateList(List<CategoryUpdate> updateList){
        this.updateList = updateList;
    }
}
