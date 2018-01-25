import NavBar from '../NavBar.js';
import CarouselLayout from '../browse/CarouselLayout.js';
import AuthorHeader from './authorHeader.js';

const React = require("react");
const ReactDOM = require("react-dom");

let myUserID = 1; // userID for testUser logged into the account

class Profile extends React.Component {
    constructor(props) {
	super(props);
	this.state = {
		userid:  -1,
		username: "testUser",
	    videos: [],
		playlists: []
	};

	// TODO: Get recent videos that belong to the user
	// get recent videos
	this.update_videos([]);
	
	this.update_videos = this.update_videos.bind(this);
    }

    update_videos() {
	$.get({
	    url: config.serverUrl + "/info/recentVideos",
	    data: JSON.stringify(this.state.userid),
	    dataType: "json",
	    success: (data) => {
		this.setState({
		    videos: data
		});
	    },
	    error: (response) => {
		console.log(response);
	    }
	});
    }
    
    // TODO: Get playlists owned by the user
    
	// get playlists
    
    render() {
	return (
		<div className="container">
	        <div className="row">
	            <div className="col-md-10 col-md-offset-1">
	            	<AuthorHeader/>
	            	
	            	<br/>
	            	<br/>
	            	<CarouselLayout title="Recently Uploaded Videos" videos={this.state.videos}/>
	            	
	            	<br/>
	            	<br/>
	            	
	            </div>
	        </div>
	    </div>
	);
    }
}

ReactDOM.render(
	    <Profile />, document.getElementById('root')
	);

// playlist React Component
// <playlists videos={this.state.playlists}/>
// <CarouselLayout title="Playlists" videos={this.state.playlists}/>


