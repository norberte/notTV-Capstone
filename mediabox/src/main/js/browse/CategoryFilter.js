const React = require("react");

class CategoryEntry extends React.Component {
    render() {
	return (
	    <div className="panel-body">
	      <span>{this.props.name}</span>
	      <i className="glyphicon glyphicon-ok pull-right hidden"/>
	    </div>
	);
    }
}

class CategoryType extends React.Component {
    render() {
	let entryClass = "panel-collapse collapse item-" + this.props.num;
	let href = "#accordion-1 .item-" + this.props.num;
	return (
	    <div className="panel panel-default">
	      <div className="panel-heading" role="tab">
		<h4 className="panel-title">
		  <span className="caret"></span>
		  <a role="button" data-toggle="collapse" data-parent="#accordion-1" aria-expanded="false" href={href} className="category-type-header">{this.props.name} </a>
		</h4>
	      </div>
	      <div className={entryClass} role="tabpanel">
		{
		    this.props.entries.map((name, idx)=> {
			return <CategoryEntry key={idx} name={name}/>;
		    })
		}
	      </div>
	    </div>
	);
    }
}


export default class CategoryFilter extends React.Component {
    render() {
	return (
	    <div className="panel-group category-filter" role="tablist" aria-multiselectable="true" id="accordion-1">
	      {
		  this.props.categories.map((cat, idx)=>{
		      return <CategoryType key={idx} num={idx + 1} name={cat.name} entries={cat.entries}/>;
		  })
	      }
	    </div>
	);
    }
}
