import CategoryFilter from './CategoryFilter.js';
import TopBar from './TopBar.js';
import CarouselLayout from './CarouselLayout.js';
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
		<TopBar/>
		<div className="row browse-body">
		  <div className="col-md-12">
		    <CarouselLayout title="Subscribed" videos={this.props.videos}/>
		    <CarouselLayout title="In Library" videos={this.props.videos}/>
		    <CarouselLayout title="Popular" videos={this.props.videos}/>
		    <CarouselLayout title="Newest" videos={this.props.videos}/>
		  </div>
		</div>
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

let VIDEOS = [];
for(let i=0;i<8;i++)
    VIDEOS.push({
	title: "Title" + i,
	src: "/img/default-placeholder-300x300.png "
    });

ReactDOM.render(
    <Browse categories={CATEGORIES} videos={VIDEOS}/>,
    document.getElementById('root')
);
