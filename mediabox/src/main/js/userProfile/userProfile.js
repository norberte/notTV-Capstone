import NavBar from '../NavBar.js';
import CarouselLayout from '../browse/CarouselLayout.js';
import AuthorHeader from './authorHeader.js';
import PlaylistCarousel from './playlists.js';

const React = require("react");
const ReactDOM = require("react-dom");

let myUserID = 1; // userID for testUser logged into the account

class Profile extends React.Component {
    constructor(props) {
	super(props);
	this.state = {
		userid:  [-1],
		username: "testUser",
	    videos: [],
		playlists: []
	};

	// Get recent videos that belong to the user	
	this.update_videos = this.update_videos(this.state.userid);
	// Get playlists owned by the user
	this.update_playlists = this.update_playlists(this.state.userid);
    }

    // ajax call for getting the videos
    update_videos(userID) {
	$.get({
	    url: config.serverUrl + "/info/recentVideos/",
	    data: {
			userid: userID
		    },
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
    
    // ajax call for getting the playlists
    update_playlists(userID) {
    	$.get({
    	    url: config.serverUrl + "/info/playlists/",
    	    data: {
    			userid: userID
    		    },
    	    dataType: "json",
    	    success: (data) => {
    		this.setState({
    		    playlists: data
    		});
    	    },
    	    error: (response) => {
    		console.log(response);
    	    }
    	});
        }
    
    render() {
	return (
		<div className="container">
	        <div className="row">
	            <div className="col-md-10 col-md-offset-1">
	            	<AuthorHeader description= "NotTV Test Account" username = {this.state.username} />
	            	<br/>
	            	<br/>
	            	<div className="row browse-body">
	      		  		<CarouselLayout title="Recently Uploaded Videos" videos={this.state.videos}/>
	      		  	</div>
	            	<br/>
	            	<br/>
	            	<div className="row browse-body">
      		  			<PlaylistCarousel title="Playlists" playlists={this.state.playlists}/>
      		  		</div>	  
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


