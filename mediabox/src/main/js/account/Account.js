import NavBar from '../NavBar.js';
import CurrentUser from '../CurrentUser.js'
//import ajaxSubmit from '../ajaxSubmit.js';
//There is crossover into the browse folder with the use of CarouselLayout.
//Perhaps, if we are to use it in this page, it should be moved up a folder.
import CarouselLayout from '../CarouselLayout.js';
import VideoThumbnail from '../browse/Browse.js';

const React = require("react");
const ReactDOM = require("react-dom");

let default_loggedIn_userID = CurrentUser.getId();
let default_loggedIn_username = CurrentUser.getUsername();
let thumbnailURL;
class UserThumbnail extends React.Component {
    constructor(props) {
        super(props);

        //this.getThumbnail = this.getThumbnail.bind(this);
        //this.getThumbnail();
    }

    /*
     * User Profile Picture download is not implemented yet
     *
    getThumbnail() {
        // get thumbnail if it isn't loaded already.
        if(!this.props.entry.thumbnailURL)
            utils.getThumbnail(this.props.entry.id, (file) => {
                this.props.entry.thumbnailURL = file;
                this.forceUpdate();
            });
    }
    */

    render() {
        // need to call here because the constructor isn't always called for entries.
        // I think React may be reusing existing ones, so it doesn't create new VideoThumbnails?
        // We need to do it in the constructor also, because the component needs
        // to be mounted first to forceUpdate().
        //this.getThumbnail();
        const thumbnail = this.props.entry.thumbnailURL ? this.props.entry.thumbnailURL : "/img/default-placeholder-300x300.png";
        thumbnailURL = thumbnail;
        return (
            <div className="col-md-2 no-padding">
            <a href={this.props.entry.userProfileURL}>
                 <div className="thumbnail no-margin">
                     <div className="video-thumbnail">
                         <img className="video-thumbnail-content" src={thumbnail}/>
                     </div>
                     <div className="caption">
                         <h4 className="no-margin">{this.props.entry.username} </h4>
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
        url: config.secureServerUrl + "/info/subscriptions",
        data: {
            loggedInUserID: this.state.userLogin
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


    //Get a list of in-library videos
    update_videos() {
	$.get({
	    url: config.serverUrl + "/info/libraryVideos",
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

           //send new account details
           $.post({
               url: config.secure_serverUrl + "/upload/accountSubmit",
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
            <div id ="myContainer">
            <div className="container">
                <div className="row">
                    <div className="col-md-10 col-md-offset-1">
                    <div id = 'accountInfo'>
                        <h1>Account Info</h1>
                        <div id = 'accountDetails'>
                            <div id = 'NewContainer'>
                                <figure>
                                    <img src = {thumbnailURL} alt = "Profile Picture"/>
                                    <figcaption>{default_loggedIn_username}</figcaption>
                                </figure>
                            </div>

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
                        <div className = "row browse-body">
                            <CarouselLayout thumbnailClass={UserThumbnail} title="Users Subscribed To" entries={this.state.subscriptions}/>
                        </div>
            	        <div className = "row browse-body">
            	            <CarouselLayout thumbnailClass={VideoThumbnail} title="In-Library Videos" entries={this.state.videos}/>
            	        </div>
                    </div>
                    </div>
                </div>
          </div>
     </div>
    );
    }
}

ReactDOM.render(<Account />,
		document.getElementById('root')
	);
