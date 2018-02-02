const React = require("react");

class Button extends React.Component{
	render(){
		return(
			<button className="btn btn-default" type="button" onClick={this.props.onClickFunction}> {this.props.buttonName} </button>
		);
	}
}

export default class AuthorHeader extends React.Component {
	subscribe(loggedInUser, userFromProfile){
    	$.get({
    	    url: config.serverUrl + "/info/subscribe",
    	    data: {
    			userID1: loggedInUser,
    			userID2: userFromProfile[0] // this is actually an array of 1 element
    		    },
    	    dataType: "json",
    	    success: (data) => {
    	    	if(data == true){
    	    		this.setProps({
    	    			subscribed: true
        	    	});
    	    		console.log("Successfully subscribed.");
    	    	} else {
    	    		console.log("Could not subscribe for some reason.");
    	    	}
    	    },
    	    error: (response) => {
    		console.log("Unsuccessful subscribe!");
    	    }
    	});
    }
	
	
	unsubscribe(loggedInUser, userFromProfile){
    	$.get({
    	    url: config.serverUrl + "/info/unsubscribe",
    	    data: {
    			userID1: loggedInUser,
    			userID2: userFromProfile[0] // this is actually an array of 1 element
    		    },
    	    dataType: "json",
    	    success: (data) => {
    	    	if(data == true){
    	    		this.setProps({
    	    			subscribed: false
        	    	});
    	    		console.log("Successfully unsubscribed.");
    	    	} else {
    	    		console.log("Could not unsubscribe for some reason.");
    	    	}
    	    },
    	    error: (response) => {
    		console.log("Unsuccessful unsubscribe.");
    	    }
    	});
    }  
	
	/*
	subscribedButton(){
		return (<button className="btn btn-default" type="button" onClick={this.subscribe(this.props.loggedIn_userID, this.props.userID)}>Subscribe </button>);
	}
	unsubscribedButton(){
		return (<button className="btn btn-default" type="button" onClick={this.unsubscribe(this.props.loggedIn_userID, this.props.userID)}>Unsubscribe</button>);
	}
	*/
	
	render() {
		return (
				<div className="media">
					<div className="media-left">
						<img src="../img/user.png"></img>
					</div>
					<div className="media-body">
						<h4 className="media-heading">{this.props.username}</h4>
						<p>{this.props.description}</p>
						<div>{
							(this.props.subscribed == true) ? <Button buttonName="Unsubscribe" onClickFunction = {this.unsubscribe(this.props.loggedIn_userID, this.props.userID)} /> : 
								<Button buttonName="Subscribe" onClickFunction={this.subscribe(this.props.loggedIn_userID, this.props.userID)} />
						}</div> 
					</div>
				</div>
		);
	}
}
