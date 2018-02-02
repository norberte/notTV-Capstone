import NavBar from '../NavBar.js';
import CarouselLayout from '../browse/CarouselLayout.js';
import PlaylistCarousel from './playlists.js';
import AuthorHeader from './authorHeader.js'
const React = require("react");
const ReactDOM = require("react-dom");

// logged in user's id and username
// these 2 values should be stored in the cookies of the site
let default_loggedIn_userID = -1;
let default_loggedIn_username = "dummy"; // real username from DB that we consider logged in

// userName and id of the user who's profile is being checked out
let username_fromURLParameter = "";

export default class Profile extends React.Component {
    constructor(props) {
	super(props);
	var urlPath = window.location.pathname;
	username_fromURLParameter = urlPath.replace('/userProfile/', ''); 
	// extracts username provided in the URL path and removes "/userProfile/" 
	// ... TO DO: FIND A BETTER WAY to do this
	
	this.state = {
		userid:  [],
		username: [username_fromURLParameter],
	    videos: [],
		playlists: [],
		loggedInUser_IsSubscribed: false
	};
	
	// check validity of the username provided in the url
	this.checkUsernameValidity = this.checkUsernameValidity(this.state.username);
    }
    
    checkForSubscribed(loggedInUser, userFromProfile){
    	$.get({
    	    url: config.serverUrl + "/info/checkSubscribed",
    	    data: {
    			userID1: loggedInUser,
    			userID2: userFromProfile[0] // this is actually an array of 1 element
    		    },
    	    dataType: "json",
    	    success: (data) => {
    	    	console.log("Successfully checked if user is subscribed.");
    	    	this.setState({
    	    		loggedInUser_IsSubscribed: data
    	    	});
    	    },
    	    error: (response) => {
    	    	console.log("Failed to check if logged in user is subscribed");
    	    } 
    	});
    }
    
    //ajax call to check username validity
    checkUsernameValidity(usernameFromURL){
    	$.get({
    	    url: config.serverUrl + "/info/getUserID",
    	    data: {
    			username: usernameFromURL
    		    },
    	    dataType: "json",
    	    success: (data) => {
    	    	if(jQuery.isEmptyObject(data)){
    	    		this.setState({
    	    			userid: [-10],
    	    			username: ["No such username in notTV's system"]
    	    		});
    	    	} else {
    	    		this.setState({
            			userid: data
            		});
    	    		
    	    		// Check if logged in user is subscribed to user'profile being checked out
    	    		this.checkForSubscribed = this.checkForSubscribed(default_loggedIn_userID, this.state.userid);
    	    		
    	    		// Get recent videos that belong to the user
        	    	this.update_videos = this.update_videos(this.state.userid);
        	    	
        	    	// Get playlists owned by the user
        	    	this.update_playlists = this.update_playlists(this.state.userid);
    	    	}
    	    },
    	    error: (response) => {
    		console.log(response);
    		this.setState({
    			userid: [-10],
    			username: ["No such username in notTV's system"]
    		});
    	    }
    	});
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
	            	<AuthorHeader description= "NotTV Test Account" username = {this.state.username} userID = {this.state.userid} subscribed = {this.state.loggedInUser_IsSubscribed} loggedIn_userID = {default_loggedIn_userID} />
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
