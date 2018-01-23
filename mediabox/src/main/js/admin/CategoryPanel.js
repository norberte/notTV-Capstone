const React = require("react");

class CategoryRow extends React.Component {
    render() {
	console.log(this.props.category);
	const id = this.props.category.name + "-accordian";
	return (
	    <tbody>
	      <tr data-toggle="collapse" data-target={"#" + id} className="clickable">
		<td><div contentEditable="true"  onChange={this.props.handleEdit}>{this.props.category.name}</div></td>
		<td><input type="button" value="X" onClick={this.props.handleDelete}/></td>
	      </tr>
	      {
		  this.props.category.values.map((val, idx) => <tr id={id} key={idx} className="collapse"><td>{val.name}</td></tr>)
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
    }

    handleEdit(e) {
	console.log(e);
    }

    handleDelete(e) {
	console.log(e);
    }
    
    render() {
	return (
	    <table>
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
