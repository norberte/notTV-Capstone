const React = require("react");

class CategoryValueRow extends React.Component {
    constructor(props) {
        super(props);
    }
    
    render(){
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
                <input className="deleteButton btn btn-danger" type="button" value="Delete Category" onClick={this.props.handleCategoryDelete}/>
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
    this.handleCategoryDelete = this.handleCategoryDelete.bind(this);
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
        newUpdates = this.overwrite(newUpdates, categoryType.name, categoryValue.id).concat([{
            action: "value edit",
            category: categoryType.name,
            id: categoryValue.id,
            value: event.target.value
        }]);

        console.log(newUpdates);
        this.setState({
            categoryTypes: newCategoryTypes,
            updates: newUpdates
        });
    }
    
    // removes a CategoryType from the tree when the delete button is pressed and adds the action to the updates list
    handleCategoryDelete(categoryType, event){
        console.log("Delete Category: " + event.target.parentElement);
        let newCategoryTypes = this.state.categoryTypes.slice();
        let index = newCategoryTypes.indexOf(categoryType);
        newCategoryTypes.splice(index);
        
        let newUpdates = this.state.updates.slice();
        newUpdates = this.overwrite(newUpdates, categoryType.name).concat([{
            action: "category delete",
            category: CategoryType.name
        }]);
        
        console.log(newUpdates);
        this.setState({
            categoryTypes: newCategoryTypes,
            updates: newUpdates
        });
    }
    
    // removes a CategoryValue from the tree when the delete button is pressed and adds the action to the updates list
    handleDelete(categoryType, categoryValue, event) {
        console.log("Delete: " + event.target.parentElement);
        let newCategoryTypes = this.state.categoryTypes.slice();
        let index1 = newCategoryTypes.indexOf(categoryType);
        let index2 = newCategoryTypes[index1].values.indexOf(categoryValue);
        newCategoryTypes[index1].values.splice(index2);
            
        let newUpdates = this.state.updates.slice();
        newUpdates = this.overwrite(newUpdates, categoryType.name, categoryValue.id).concat([{
            action: "value delete",
            category: categoryType.name,
            id: categoryValue.id
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
            categoryTypes: newCategoryTypes
        });
    }
    save() {
        console.log("save");
        
        $.post({
            url: config.serverUrl + "/update/categories",
            data: updates,
            success: (data) => {
                console.log(data);
                /*
                this.setState({
                    categoryTypes: data
                });
                */
            },
            error: () => {} //not sure yet how to handle errors 
        });
    }
    
    //helper method to remove a value update from the update list if a later change overwrites it
    overwrite(updates, categoryType, catId=null){
        let index = updates.findIndex((u)=>{u.category == categoryType && u.id == catId});
        if(index >= 0){
            updates.splice(index);
        }
        return updates;
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
                        handleCategoryDelete={this.handleCategoryDelete.bind(this, cat)}
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
