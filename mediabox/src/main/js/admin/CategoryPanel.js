const React = require("react");

const new_category_type = 1;
const new_category_value = 2;
const edit_category_value = 3;
const edit_category_type = 4;
const delete_category_value = 5;
const delete_category_type = 6;

class CategoryValueRow extends React.Component {
    constructor(props) {
        super(props);
    }
    
    render(){
        let temp = this.props.value.display;
        // change it to false here
        return (
            <tr className={"category-row panel-collapse collapse " + (this.props.value.display?"in ":"") + this.props.valueClass}>
                <td className="panel-body">
                    <input type="text" onChange={this.props.handleEdit} value={this.props.value.name}/>
                </td>
                <td className="category-col">
                    <input id={this.props.value.id} className="deleteButton btn btn-danger" type="button" value="Delete" onClick={this.props.handleDelete}/>
                </td>
            </tr>
        )
    }
}
class CategoryTypeRow extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            valueClass: this.props.category.name.replace(/\s/g,"-").toLowerCase() + "-collapse"
        };
    }
    render() {
        console.log(this.props.category);
        const id = this.props.category.name + "-accordian";
        return (
                
    <tbody className="panel panel-default">
        <tr className="panel-heading accordion-toggle category-row" data-toggle="collapse" data-target={"." + this.state.valueClass}>
            <td className="category-col">
                <div  className="panel-title">
                    <i className="glyphicon glyphicon-menu-up"/>
                    {" " + this.props.category.name}
                </div>
            </td>
            <td className="category-col">
                <input className="deleteButton btn btn-danger" type="button" value="Delete Category" onClick={this.props.handleDeleteCategoryType}/>
            </td>
        </tr>
        {
        this.props.category.values.map((val, idx) => {
            return <CategoryValueRow
                    value={val}
                    name={val.name} //remove these later
                    id={val.id}
                    display={val.display}
                    valueClass={this.state.valueClass} 
                    key={idx}
                    handleEdit={this.props.handleEdit.bind(this.props.handleEdit, val)}
                    handleDelete={this.props.handleDelete.bind(this.props.handleDelete, val)}/>
            })
        }
        <tr className={"category-row panel-collapse collapse " + this.state.valueClass}>
          <td>
            <br/>
            <input type="button" className="btn btn-success" value="New" onClick={this.props.newCategoryValue}/>
          </td>
        </tr>
    </tbody>
	    
        );
    }
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
	        restore: data
	    });
        }
    });

    this.handleEdit = this.handleEdit.bind(this);
    this.handleDeleteCategoryType = this.handleDeleteCategoryType.bind(this);
    this.handleDelete = this.handleDelete.bind(this);
    this.newCategoryType = this.newCategoryType.bind(this);
    this.newCategoryValue = this.newCategoryValue.bind(this);
    this.save = this.save.bind(this);
    this.overwrite = this.overwrite.bind(this);
    }
    
    // updates state when a CategoryValue name is changed
    handleEdit(categoryType, categoryValue, event) {
        console.log(event.target);
        let newCategoryTypes = this.state.categoryTypes.slice();
        let index1 = newCategoryTypes.indexOf(categoryType);
        let index2 = newCategoryTypes[index1].values.indexOf(categoryValue);
        newCategoryTypes[index1].values[index2].name = event.target.value;
        
        let newUpdates = this.state.updates.slice();
        newUpdates = this.overwrite(newUpdates, categoryType.id, categoryValue.id).concat([{
            action: edit_category_value,
            categoryTypeId: categoryType.id,
            categoryValueId: categoryValue.id,
            value: event.target.value
        }]);

        console.log(newUpdates);
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
        newUpdates = newUpdates.concat([{
            action: delete_category_type,
            categoryTypeId: categoryType.id
        }]);
        
        console.log(newUpdates);
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
        newUpdates = newUpdates.concat([{
            action: delete_category_value,
            categoryTypeId: categoryType.id,
            categoryValueId: categoryValue.id
        }]);

        console.log(newUpdates);
        this.setState({
            categoryTypes: newCategoryTypes,
            updates: newUpdates
        });
    }
    // Appends a new, empty CategoryType to the list when 'New Category' button is clicked
    newCategoryType() {
    	console.log("New Category");
    	this.setState({
    	    categoryTypes: this.state.categoryTypes.concat([{
        		name: "New Category",
        		values: []
    	    }]),
    	    updates: this.state.updates.concat([{
                action: new_category_type,
                value: "New Category"
    	    }])
    	});
    }
    // Adds a new CategoryValue when one of the 'New" buttons are pressed
    newCategoryValue(categoryType) {
        console.log("New Category Value");
        let newCategoryTypes = this.state.categoryTypes.slice();
        let index = newCategoryTypes.indexOf(categoryType);
        newCategoryTypes[index].values.push({
            id: 1, //TODO: figure out how to assign correct id
            name: "New",
            display: true
        });
            
        this.setState({
            categoryTypes: newCategoryTypes,
            updates: this.state.updates.concat([{
                action: new_category_value,
                categoryTypeId: categoryType.id,
                value: "New"
            }])
        });
    }
    save() {
        console.log("save");
        console.log(JSON.stringify(this.state.updates));
        $.post({
            url: config.serverUrl + "/update/categories",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({updateList: this.state.updates}),
            processData: false,
            success: (data) => {
                console.log(data);
                this.setState({
                    updates: [],    //clear update list
                //  categoryTypes: data //reset state to make sure it's consistent with database?
                });
            },
            error: () => {} //not sure yet how to handle errors 
        });
    }
    
    //helper method to remove a value update from the update list if a later change overwrites it
    overwrite(updateList, catTypeId, catValId=null){
        let index = updateList.findIndex(function(u){return (u.categoryTypeId == catTypeId && u.categoryValueId == catValId);});
        if(index >= 0){
            updateList.splice(index, 1);
        }
        return updateList;
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
        		    <br/>
        		  </td>
        		</tr>
        		<tr>
        		  <td>
        		    <input type="button" className="btn btn-success" value="Add Category" onClick={this.newCategoryType}/>
        		    <input type="button" className="btn btn-info" value="Save" onClick={this.save}/>
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
