import NavBar from '../NavBar.js';
//There is crossover into the browse folder with the use of CarouselLayout.
//Perhaps, if we are to use it in this page, it should be moved up a folder.
import CarouselLayout from '../CarouselLayout.js';

const React = require("react");
const ReactDOM = require("react-dom");

class Account extends React.Component {
    constructor(props){
    super(props);
    this.state = {
        videos: [],
        subscriptions: [],
        formData: {
        currentUsername: '', //Alternatively, we could use the id of the user rather than the username
        newUsername: '',
        newEmail: '',
        autoDownload: '',
        newPass: '',
        confirmNewPass: ''
        }
    }
    // get subscriptions
    //this.update_subscriptions();

    // get videos
	//this.update_videos();

	//this.update_videos = this.update_videos.bind(this);
    //this.update_subscriptions = this.update_subscriptions.bind(this);

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    };

    //Need to fix code to get data from back end for subscritpions and videos
    //Also, will need to send data to back end to update videos and subscriptions

    /*
    //Get a list of subscriptions
    update_subscriptions() {
    $.get({
        url: config.serverUrl + "/info/subscriptions",  //Needs adjustment and changes. Config is undefined.
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
    */

    /*
    //Get a list of videos this user has saved.
    update_videos() {
	$.get({
	    url: config.serverUrl + "/info/videos", //Needs adjustment and changes. Config is undefined.
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
    */

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
               currentUsername: this.state.currentUsername,
   			   newUsername: this.state.newUsername,
               newEmail: this.state.newEmail
   		};

           // Use Ajax to send new account details
           $.ajax({
               type: "POST",
               url: config.serverUrl + "/account/accountSubmit",
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
                    <figcaption>Username_Placeholder</figcaption>
                </figure>

                <form id="accountInfoForm" method="post" onSubmit={this.handleSubmit} commandname="accountForm">
                    <input type = "text" name="newUsername" value={this.state.formData.username}  onChange={this.handleChange} placeholder="Enter a New Username" disabled/><br />
                    <input type = "text" name="newEmail" value={this.state.formData.email}  onChange={this.handleChange} placeholder="Enter a New Email" /><br />
                    <input type = "password" name="newPass" value={this.state.formData.newPass}  onChange={this.handleChange} placeholder="Enter a New Password" /><br />
                    <input type = "password" name="confirmNewPass" value={this.state.formData.confirmNewPass}  onChange={this.handleChange} placeholder="Confirm New Password" />

                    <input type="submit" value="Submit"/>
                </form>
                {/*
                For some reason, I need to have this below form here to get the above form to have
                a clickable submit button. This is very strange.
                */}
                <form id = "hide">
                    <input type="submit" value="Submit"/>
                </form>
            </div>

            <div className = "lowerDiv">
            <div className = "row browse-body">
                <CarouselLayout title="Subscribed" videos={this.state.subscriptions}/>
            </div>
            <div className = "row browse-body">
                <CarouselLayout title="Saved Videos" videos={this.state.videos}/>
            </div>
            </div>

        </div>
    );
    }
}

ReactDOM.render(<Account />, document.getElementById('root'));
