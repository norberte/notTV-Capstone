class Browse extends React.Component {
    render() {
	return (
	    <div className="row display-flex" style="height:100%;margin:0px;">
	      <div className="col-md-2" style="height:100vh;background-color:#4b4646;padding:0px;">
		// <CategoryFilter categories={this.props.categories}/>
	      </div>
	      <div className="col-md-10" style="height:100vh;">
	      </div>
	    </div>
	);
    }
}

let CATEGORIES = [
    {
	name: "Misc",
	entries: [
	    "In Library",
	    "Short Videos",
	    "Long Videos"
	]
    },
    {
	name: "City",
	entries: []
    }
];
