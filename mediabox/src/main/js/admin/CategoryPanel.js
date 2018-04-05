const React = require("react");

const new_category_type = 1;
const new_category_value = 2;
const edit_category_value = 3;
const edit_category_type = 4;
const delete_category_value = 5;
const delete_category_type = 6;

function CategoryValueRow(props) {
    return (
        <tr className={"category-row panel-collapse collapse in"}>
            <td className="panel-body">
                <input type="text" onChange={props.handleEdit} value={props.value.name}/>
            </td>
            <td className="category-col">
                <input id={props.value.id} className="deleteButton btn btn-danger" type="button" value="Delete" onClick={props.handleDelete}/>
            </td>
        </tr>
    );
}
function CategoryTypeRow(props) {
    console.log(props.category);
    const id = props.category.name + "-accordian";
    const valueClass = props.category.name.replace(/\s/g,"-").toLowerCase() + "-collapse";
    return (
      <React.Fragment>       
      <tbody className="panel panel-default">
        <tr className="panel-heading accordion-toggle category-row" data-toggle="collapse" data-target={"." + valueClass}>
            <td className="category-col">
                <div  className="panel-title">
                    <i className="glyphicon glyphicon-menu-up"/>
                    <input type="text" onChange={props.handleCategoryTypeEdit} value={props.category.name}/>
                </div>
            </td>
            <td className="category-col">
                <input className="deleteButton btn btn-danger" type="button" value="Delete Category" onClick={props.handleDeleteCategoryType}/>
            </td>
        </tr>
      </tbody>
      <tbody className={"panel-collapse collapse " + valueClass}>
        {
        props.category.values.map((val, idx) => {
            return <CategoryValueRow
                    value={val}
                    valueClass={valueClass}
                    key={idx}
                    handleEdit={props.handleEdit.bind(props.handleEdit, val)}
                    handleDelete={props.handleDelete.bind(props.handleDelete, val)}/>
            })
        }
        <tr className={"category-row panel-collapse collapse in"}>
          <td className="panel-body">
            <input type="button" className="btn btn-success" value="New Tag" onClick={props.newCategoryValue}/>
          </td>
        </tr>
      </tbody>
      </React.Fragment>
    );
}

const NULL_ROW = <CategoryTypeRow category={{name: "", values: []}} handleEdit={()=>null} handleDelete={()=>null}/>;

class CategoryType extends React.Component {
    constructor(props) {
    super(props);

    this.state = {
        categoryTypes: [],
        updates: [],
        restore: []
    };

    $.get({
        url: config.serverUrl + "/info/categories",
        dataType: "json",
        success: (data) => {
        console.log(data);
        this.setState({
            categoryTypes: data,
            restore: JSON.parse(JSON.stringify(data)) // clone
        });
        }
    });
    
    this.unsavedWarning = this.unsavedWarning.bind(this);
    this.handleCategoryTypeEdit = this.handleCategoryTypeEdit.bind(this);
    this.handleEdit = this.handleEdit.bind(this);
    this.handleDeleteCategoryType = this.handleDeleteCategoryType.bind(this);
    this.handleDelete = this.handleDelete.bind(this);
    this.newCategoryType = this.newCategoryType.bind(this);
    this.newCategoryValue = this.newCategoryValue.bind(this);
    this.save = this.save.bind(this);
    this.cancelChanges = this.cancelChanges.bind(this);
    }
    
    componentDidMount() {
        window.addEventListener("beforeunload", this.unsavedWarning);
    }
    
    unsavedWarning(e){
        if (this.state.updates.length <= 0) {
            return undefined;
        }

        var confirmationMessage = 'You have unsaved changes. '
                                + 'Are you sure you want to leave?.';

        (e || window.event).returnValue = confirmationMessage; //Gecko + IE
        return confirmationMessage; //Gecko + Webkit, Safari, Chrome etc.
    }
    
    handleCategoryTypeEdit(categoryType, event){
        console.log(event.target);
        let newCategoryTypes = this.state.categoryTypes.slice();
        let index = newCategoryTypes.indexOf(categoryType);
        newCategoryTypes[index].name = event.target.value;
        
        let newUpdates = this.state.updates.slice();
        index = newUpdates.findIndex(function(u){return (u.categoryTypeId == categoryType.id && u.categoryValueId == undefined);});
        // if a previous update on this element exists in the update list, just modify the value
        if(index > -1){
            if(newUpdates[index].action == edit_category_type || newUpdates[index].action == new_category_type){
                newUpdates[index].value =  event.target.value;
            }
            else{
                console.error("Error: Invalid update operation."); //This should never EVER happen
            }
        }
        else{   //else append a new update to the list
            newUpdates = newUpdates.concat([{
                action: edit_category_type,
                categoryTypeId: categoryType.id,
                value: event.target.value
            }]);
        }
        this.setState({
            categoryTypes: newCategoryTypes,
            updates: newUpdates
        });
    }
    // updates state when a CategoryValue name is changed
    handleEdit(categoryType, categoryValue, event) {
        console.log(event.target);
        let newCategoryTypes = this.state.categoryTypes.slice();
        let index1 = newCategoryTypes.indexOf(categoryType);
        let index2 = newCategoryTypes[index1].values.indexOf(categoryValue);
        newCategoryTypes[index1].values[index2].name = event.target.value;
        
        let newUpdates = this.state.updates.slice();
        let index = newUpdates.findIndex(function(u){return (u.categoryTypeId == categoryType.id && u.categoryValueId == categoryValue.id);});
        // if a previous update on this element exists in the update list, just modify the value
        if(index > -1){
            if(newUpdates[index].action == edit_category_value || newUpdates[index].action == new_category_value){
                newUpdates[index].value =  event.target.value;
            }
            else{
                console.error("Error: Invalid update operation.")
            }
        }
        else{   //else append a new update to the list
            newUpdates = newUpdates.concat([{
                action: edit_category_value,
                categoryTypeId: categoryType.id,
                categoryValueId: categoryValue.id,
                value: event.target.value
            }]);
        }
        this.setState({
            categoryTypes: newCategoryTypes,
            updates: newUpdates
        });
    }
    
    // removes a CategoryType from the tree when the delete button is pressed and adds the action to the updates list
    handleDeleteCategoryType(categoryType, event){
        console.log("Delete Category: " + event.target.parentNode.parentNode);
        let newCategoryTypes = this.state.categoryTypes.slice();
        let index = newCategoryTypes.indexOf(categoryType);
        console.log(newCategoryTypes.splice(index, 1));
        
        let newUpdates = this.state.updates.slice();
        index = newUpdates.findIndex(function(u){return (u.categoryTypeId == categoryType.id && u.categoryValueId == undefined);});
        
        if(index > -1){
            if(newUpdates[index].action == new_category_type){
                newUpdates.splice(index, 1);    //if the element isn't in the database yet, just remove the insert instruction
            }
            else if(newUpdates[index].action == edit_category_type){
                newUpdates.splice(index, 1);    //else delete the update instruction and append a deletion
                newUpdates = newUpdates.concat([{
                    action: delete_category_type,
                    categoryTypeId: categoryType.id
                }]);
            }
            else{
                console.error("Error: Invalid delete operation."); 
            }
        }
        else{   //else append a new delete to the list
            newUpdates = newUpdates.concat([{
                action: delete_category_type,
                categoryTypeId: categoryType.id
            }]);
        }
        this.setState({
            categoryTypes: newCategoryTypes,
            updates: newUpdates
        });
    }
    
    // removes a CategoryValue from the tree when the delete button is pressed and adds the action to the updates list
    handleDelete(categoryType, categoryValue, event) {
        console.log("Delete: " + event.target.parentNode.parentNode);
        let newCategoryTypes = this.state.categoryTypes.slice();
        let index1 = newCategoryTypes.indexOf(categoryType);
        let index2 = newCategoryTypes[index1].values.indexOf(categoryValue);
        console.log(newCategoryTypes[index1].values.splice(index2, 1));
        
        let newUpdates = this.state.updates.slice();
        let index = newUpdates.findIndex(function(u){return (u.categoryTypeId == categoryType.id && u.categoryValueId == categoryValue.id);});
        
        if(index > -1){
            if(newUpdates[index].action == new_category_type){
                newUpdates.splice(index, 1);    //if the element isn't in the database yet, just remove the insert instruction
            }
            else if(newUpdates[index].action == edit_category_type){
                newUpdates.splice(index, 1);    //else delete the update instruction and append a deletion
                newUpdates = newUpdates.concat([{
                    action: delete_category_value,
                    categoryTypeId: categoryType.id,
                    categoryValueId: categoryValue.id
                }]);
            }
            else{
                console.error("Error: Invalid delete operation."); 
            }
        }
        else{   //else append a new delete to the list
            newUpdates = newUpdates.concat([{
                action: delete_category_value,
                categoryTypeId: categoryType.id,
                categoryValueId: categoryValue.id
            }]);
        }
        this.setState({
            categoryTypes: newCategoryTypes,
            updates: newUpdates
        });
    }
    // Appends a new, empty CategoryType to the list when 'New Category' button is clicked
    newCategoryType() {
        $.get({
            url: config.serverUrl + "/info/category-type-id",
            dataType: "text",
            success: (id) => {
                console.log("New Category: id = "+ id);
                this.setState({
                    categoryTypes: this.state.categoryTypes.concat([{
                        id: id,
                        name: "New Category",
                        values: []
                    }]),
                    updates: this.state.updates.concat([{
                        action: new_category_type,
                        categoryTypeId: id,
                        value: "New Category"
                    }])
                });
            }
        });
    }
    // Adds a new CategoryValue when one of the 'New" buttons are pressed
    newCategoryValue(categoryType) {
        $.get({
            url: config.serverUrl + "/info/category-value-id",
            dataType: "text",
            success: (id) => {
                console.log("New Category Value: id = "+ id);
                
                let newCategoryTypes = this.state.categoryTypes.slice();
                let index = newCategoryTypes.indexOf(categoryType);
                newCategoryTypes[index].values.push({
                    id: id,
                    name: "New"
                });
                    
                this.setState({
                    categoryTypes: newCategoryTypes,
                    updates: this.state.updates.concat([{
                        action: new_category_value,
                        categoryTypeId: categoryType.id,
                        categoryValueId: id,
                        value: "New"
                    }])
                });
            }
        });
    }
    save() {
        console.log("save");
        console.log("updating: "+ JSON.stringify(this.state.updates));
        $.post({
            url: config.serverUrl + "/update/categories",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({updateList: this.state.updates}),
            processData: false,
            success: (data) => {
                console.log(data);
                this.setState({
                    updates: [],    //clear update list
                    restore: this.categoryTypes
                });
                alert("Changes Saved");
            },
            error: () => {} //not sure yet how to handle errors 
        });
    }
    cancelChanges(){
        console.log("reverting to previous save");
        this.setState({
            categoryTypes: this.state.restore,
            updates: []
        });
    }
    
    render() {
        return (
            <table className="panel-group" id="category-accordian">
              <thead>
            <tr>
              <th>CategoryType</th>
            </tr>
              </thead>
              {
              this.state.categoryTypes.length > 0 ?
                  this.state.categoryTypes.map((cat, idx) => {
                  return <CategoryTypeRow
                        category={cat}
                        key={idx}
                        handleCategoryTypeEdit={this.handleCategoryTypeEdit.bind(this, cat)}
                        handleEdit={this.handleEdit.bind(this, cat)}
                        handleDelete={this.handleDelete.bind(this, cat)}
                        handleDeleteCategoryType={this.handleDeleteCategoryType.bind(this, cat)}
                        newCategoryValue={this.newCategoryValue.bind(this, cat)}
                        />;
                  })
              : NULL_ROW
              }
              <tbody>
                <tr>
                  <td>
                    <input type="button" className="btn btn-success" value="Add Category" onClick={this.newCategoryType}/>
                  </td>
                </tr>
                <tr>
                <td>
                  <br/>
                </td>
              </tr>
              <tr>
                <td>
                  <input type="button" className="btn btn-info" value="Save" onClick={this.save}/>
                  <input type="button" className="btn btn-warning" value="Cancel Changes" onClick={this.cancelChanges}/>
                </td>
              </tr>
              </tbody>
            </table>
        );
    }
}

export default class CategoryPanel extends React.Component {
    render() {
    return (
        <div>
          <CategoryType/>
        </div>
    );
    }
}
