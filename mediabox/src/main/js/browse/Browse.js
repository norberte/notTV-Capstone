import CategoryFilter from './CategoryFilter.js';
const React = require("react");
const ReactDOM = require("react-dom");


class Browse extends React.Component {
    render() {
	return (
	    <div className="row display-flex categories-row">
	      <div className="col-md-2 categories-column">
		<CategoryFilter categories={this.props.categories}/>
	      </div>
	      <div className="col-md-10 results-container">
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
	entries: [
	    "Edmonton",
	    "Kelowna"
	]
    }
];

ReactDOM.render(
    <Browse categories={CATEGORIES}/>,
    document.getElementById('root')
);
