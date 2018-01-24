import NavBar from '../NavBar.js';
import CarouselLayout from '../browse/CarouselLayout.js';
const React = require("react");
const ReactDOM = require("react-dom");

class videoCreator extends React.Component {
    constructor(props) {
	super(props);
	this.state = {
	    videos: []
		//playlists: []
	};

	// TODO: Get recent videos that belong to the user
	// get recent videos
	this.update_videos([]);
	
	this.update_videos = this.update_videos.bind(this);
    }

    /**
     * Gets a list of videos filtered by 'filters'
     * filters = [ cat1_id, cat2_id, ...]
     */
    update_videos(filters) {
	$.get({
	    url: config.serverUrl + "/info/videos", // change url to something like /recentVideos/userName
	    data: {
		categories: filters // not sure what this will be
	    },
	    dataType: "json",
	    success: (data) => {
		this.setState({
		    videos: data
		});
	    }
	});
    }
    
    // TODO: Get playlists owned by the user
	// get playlists
    
    render() {
	return (
	    <div className="col-md-10 results-container">
		<div className="row browse-body">
		  <div className="col-md-12">
		    <CarouselLayout title="Recently Uploaded Videos" videos={this.state.videos}/>
		  </div>
		  <div className="col-md-12">
		    <CarouselLayout title="Playlists" videos={this.state.playlists}/>
		  </div>
		</div>
	    </div>
	);
    }
}

ReactDOM.render(
    <videoCreator />,
    document.getElementById('root')
);