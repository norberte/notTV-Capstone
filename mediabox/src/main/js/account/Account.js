import NavBar from '../NavBar.js';
//import ajaxSubmit from '../ajaxSubmit.js';
//There is crossover into the browse folder with the use of CarouselLayout.
//Perhaps, if we are to use it in this page, it should be moved up a folder.
import CarouselLayout from '../CarouselLayout.js';

const React = require("react");
const ReactDOM = require("react-dom");

let default_loggedIn_userID = -1;
let default_loggedIn_username = 'dummy';

class UserThumbnail extends React.Component {
    render() {
        return (
            <div className="col-md-2 no-padding">
              <a href={this.props.entry.userProfileURL}>
                <div className="thumbnail no-margin">
                  <img className="video-thumbnail" src={this.props.entry.thumbnailURL} />
                  <div className="caption">
                    <h3 className="no-margin">{this.props.entry.username} </h3>
                  </div>
                </div>
              </a>
            </div>
        );
    }
}

class VideoThumbnail extends React.Component {
    render() {
        return (
            <div className="col-md-2 no-padding">
              <a href={this.props.entry.url}>
                <div className="thumbnail no-margin">
                  <img className="video-thumbnail" src={this.props.entry.thumbnail}/>
                  <div className="caption">
                    <h3 className="no-margin">{this.props.entry.title} </h3>
                  </div>
                </div>
              </a>
            </div>
        );
    }
}

export default class Account extends React.Component {
    constructor(props){
    super(props);
    this.state = {
    	userLogin: default_loggedIn_userID,
        videos: [],
        subscriptions: [],
        formData:{
        	userId: default_loggedIn_userID,
        	currentUsername: default_loggedIn_username,
        	newEmail: '',
        	autoDownload: '',
        	newPass: '',
        	confirmNewPass: ''
        }
    }
   
	//this.update_videos = this.update_videos.bind(this);
    this.update_subscriptions = this.update_subscriptions.bind(this);
    this.update_videos = this.update_videos.bind(this);
    
    // get subscriptions
    this.update_subscriptions();

    // get videos
	this.update_videos();

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    };

    //Get a list of subscriptions
    update_subscriptions() {
    $.get({
        url: config.serverUrl + "/info/subscriptions",
        data: {
        	userID: this.state.userLogin
        },
        dataType: "json",
        success: (data) => {
        this.setState({
        	subscriptions: data
        });
        },
        error: (response) => {
        console.log(response);
        }
    });
    }

    
    //Get a list of videos this user has saved.
    update_videos() {
	$.get({
	    url: config.serverUrl + "/info/libraryVideos",
	    data: {
        	userID: this.state.userLogin
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
    
    //handles a change in an input in the settings form and gives that new change to the state.
    handleChange(e){
        const state = this.state;
        state.formData[e.target.name] = e.target.value;
        this.setState(state);
    }

    //handles getting state data for the settings form and giving it to the ajax submit.
    handleSubmit(e){
	       e.preventDefault();

           const formData = {
               currentUsername: this.state.formData['currentUsername'],
               newPass: this.state.formData['newPass'],
               newEmail: this.state.formData['newEmail'],
               confirmNewPass: this.state.formData['confirmNewPass'],
               autoDownload: this.state.formData['autoDownload']
   		};

           // Use Ajax to send new account details
           $.ajax({
               type: "POST",
               url: config.serverUrl + "/upload/accountSubmit",
               contentType: 'application/json',
               processData: false,
               data: JSON.stringify(formData),
               success: (response) => {
               console.log(response);
               alert("Successfully uploaded!");
               },
               error: (response) => {
               console.log(response);
               }
           });
    }


    render() {
    return (
        <div id = 'accountInfo'>
            <h1>Account Info</h1>

            <div id = 'accountDetails'>

                <figure>
                    <img src = "img/default-placeholder-300x300.png" alt = "Profile Picture"/>
                    <figcaption>{default_loggedIn_username}</figcaption>
                </figure>

                <form id="accountInfoForm" method="post" onSubmit={this.handleSubmit} commandname="accountForm">
                    <input type = "text" name="newUsername" value={this.state.formData.username}  onChange={this.handleChange} placeholder="Enter a New Username" disabled/><br />
                    <input type = "text" name="newEmail" value={this.state.formData.email}  onChange={this.handleChange} placeholder="Enter a New Email" /><br />
                    <input type = "password" name="newPass" value={this.state.formData.newPass}  onChange={this.handleChange} placeholder="Enter a New Password" /><br />
                    <input type = "password" name="confirmNewPass" value={this.state.formData.confirmNewPass}  onChange={this.handleChange} placeholder="Confirm New Password" />

                    <input type="submit" value="Submit"/>
                </form>
                <form id = "hide">
                    <input type="submit" value="Submit"/>
                </form>
            </div>

            <div className = "lowerDiv">
            	<div className = "row browse-body">
                	<CarouselLayout thumbnailClass={UserThumbnail} title="Subscribed" entries={this.state.subscriptions}/>
                </div>
	            <div className = "row browse-body">
	                <CarouselLayout thumbnailClass={VideoThumbnail} title="Saved Videos" entries={this.state.videos}/>
	            </div>
            </div>
        </div>
    );
    }
}

ReactDOM.render(<Account />, 
		document.getElementById('root') 
	);
