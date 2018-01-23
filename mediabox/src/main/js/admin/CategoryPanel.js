const React = require("react");

class CategoryRow extends React.Component {
    constructor(props) {
	super(props);
	this.state = {
	    valueClass: this.props.category.name.replace(/\s/g,"-").toLowerCase() + "-collapse"
	};
    }
    
    render() {
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
		      return (
			  <tr key={idx} className={"category-row panel-collapse collapse " + this.state.valueClass}>
			    <td className="panel-body">
			      <input type="text" onChange={this.props.handleEdit} value={val.name}/>
			    </td>
			    <td className="category-col">
			      <input id={val.id} className="deleteButton btn btn-danger" type="button" value="Delete Value" onClick={this.props.handleDelete}/>
			    </td>
			  </tr>
		      );
		  })
	      }
	    </tbody>
	    
	);
    }
}

const NULL_ROW = <CategoryRow category={{name: "", values: []}} handleEdit={()=>null} handleDelete={()=>null}/>;

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
	this.newCategory = this.newCategory.bind(this);
	this.save = this.save.bind(this);
    }

    handleEdit(e) {
	console.log(e.target);
    }

    handleDelete(e) {
	console.log(e.target);
    }

    newCategory() {
	console.log("test");
	this.setState({
	    categoryTypes: this.state.categoryTypes.concat([{
		name: "New Category",
		values: []
	    }])
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
			  return <CategoryRow
					category={cat}
					key={idx}
					handleEdit={this.handleEdit}
					handleDelete={this.handleDelete}
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
		    <input type="button" className="btn btn-success" value="Add Category" onClick={this.newCategory}/>
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