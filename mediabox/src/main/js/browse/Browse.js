import NavBar from '../NavBar.js';
import CategoryFilter from './CategoryFilter.js';
import TopBar from './TopBar.js';
import CarouselLayout from './CarouselLayout.js';
const React = require("react");
const ReactDOM = require("react-dom");

class Browse extends React.Component {
    constructor(props) {
	super(props);
	this.state = {
	    categories: [],
	    videos: []
	};
	console.log(location.origin);
	// get the categories.
	$.get({
	    url: "http://nottv.levimiller.ca/info/categories",
	    dataType: "json",
	    success: (data) => {
		this.setState({
		    categories: data
		});
	    }
	});

	// TODO: Get videos for each category.
	// get videos
	$.ajax({
	    url: "http://nottv.levimiller.ca/info/videos",
	    data: {},
	    dataType: "json",
	    success: (data) => {
		console.log(data);
		this.setState({
		    videos: data
		});
	    }
	});
    }
    
    render() {
	return (
	    <div className="row display-flex categories-row">
	      <div className="col-md-2 categories-column">
		<CategoryFilter categories={this.state.categories}/>
	      </div>
	      <div className="col-md-10 results-container">
		<TopBar/>
		<div className="row browse-body">
		  <div className="col-md-12">
		    <CarouselLayout title="Subscribed" videos={this.state.videos}/>
		  </div>
		</div>
	      </div>
	    </div>
	);
    }
}

ReactDOM.render(
    <Browse />,
    document.getElementById('root')
);
