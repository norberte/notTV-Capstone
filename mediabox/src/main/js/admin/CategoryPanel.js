const React = require("react");

class CategoryValueRow extends React.Component {
    constructor(props) {
        super(props);
        this.handleChange = this.handleChange.bind(this);
    }
    handleChange(event) {
        //this.props.handleEdit(event);
        this.setState({name: event.target.value});
    }
    
    render(){
        return (
            <tr className={"category-row panel-collapse collapse " + (this.props.expanded?"in ":"") + this.props.valueClass}>
                <td className="panel-body">
                    <input type="text" onChange={this.handleChange} value={this.props.name}/>
                </td>
                <td className="category-col">
                    <input id={this.props.id} className="deleteButton btn btn-danger" type="button" value="Delete Value" onClick={this.props.handleDelete}/>
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
    handleEdit(CategoryValue, event){
        this.props.handleEdit(CategoryValue, event);
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
                <input className="deleteButton btn btn-danger" type="button" value="Delete Category"/>
            </td>
        </tr>
        {
        this.props.category.values.map((val, idx) => {
            return <CategoryValueRow 
                    name={val.name} 
                    id={val.id}
                    expanded={val.expanded}
                    valueClass={this.state.valueClass} 
                    key={idx}
                    handleEdit={this.handleEdit.bind(this, val)}
                    handleDelete={this.props.handleDelete}/>
            })
        }
        <tr className={"category-row panel-collapse collapse " + this.state.valueClass}>
          <td>
            <br/>
            <input type="button" className="btn btn-success" value="Add Category" onClick={this.props.newCategory}/>
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
	    categoryTypes: []
	};

	$.get({
	    url: config.serverUrl + "/info/categories",
	    dataType: "json",
        success: (data) => {
        console.log(data);
	    this.setState({
	        categoryTypes: data
	    });
        }
    });

    this.handleDelete = this.handleDelete.bind(this);
    this.handleEdit = this.handleEdit.bind(this);
    this.newCategoryType = this.newCategoryType.bind(this);
    this.newCategory = this.newCategory.bind(this);
    this.save = this.save.bind(this);
    }

    handleEdit(categoryType, categoryVal, event) {
        console.log(e.target);
        let newCategoryTypes = this.state.categoryTypes.slice();
        let index1 = newCategoryTypes.indexOf(categoryType);
        let index2 = newCategoryTypes[index1].values.indexOf(categoryVal);
        newCategoryTypes[index1].values[index2].name = event.target.value; //holy shit if this works
            
        this.setState({
            categoryTypes: newCategoryTypes
        });
    }

    handleDelete(e) {
        console.log(e.target);
        $.post({
            url: config.serverUrl + "/update/categories",
            data: {},
            success: (data) => {
                console.log(data);
        	    this.setState({
        	        categoryTypes: data
        	    });
            }
        });
    }

    newCategoryType() {
    	console.log("test");
    	this.setState({
    	    categoryTypes: this.state.categoryTypes.concat([{
    		name: "New Category",
    		values: []
    	    }])
    	});
    }
    newCategory(categoryType) {
        console.log("test");
        let newCategoryTypes = this.state.categoryTypes.slice();
        let index = newCategoryTypes.indexOf(categoryType);
        newCategoryTypes[index].values.push({
            id: 1, //TODO: figure out how to assign correct id
            name: "New",
            expanded: true
        });
            
        this.setState({
            categoryTypes: newCategoryTypes
        });
    }
    save() {
        console.log("save");
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
    					handleDelete={this.handleDelete}
    			        newCategory={this.newCategory.bind(this, cat)}
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
