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
	return (
	    <div className="panel panel-default">
	      <div className="panel-heading" role="tab">
		<h4 className="panel-title">
		  <span className="caret"/>
		  <a role="button" data-toggle="collapse" data-parent="#accordion-1" aria-expanded="true" href="#accordion-1 .item-1" style="margin:4px;">{this.props.name} </a>
		</h4>
	      </div>
	      <div className="panel-collapse collapse in item-1" role="tabpanel">
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


class CategoryFilter extends React.Component {
    render() {
	return (
	    <div className="panel-group" role="tablist" aria-multiselectable="true" id="accordion-1" style="margin:0px;">
	      {
		  this.props.categories.map((cat, idx)=>{
		      return <CategoryType name={cat.name} entries={cat.entries}/>;
		  })
	      }
	    </div>
	);
    }
}
