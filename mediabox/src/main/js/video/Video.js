
import VideoPlayer from './VideoPlayer.js';
const React = require("react");
const ReactDOM = require("react-dom");

className Video extends React.Component{
    constructor(props) {
    	super(props);
    	this.state = {
    	    video_data: null
    	};
    	
    }
	//get video metadata from server
	$.get({
	    url: config.serverUrl + "/info/video-data",
	    dataType: "json",
	    success: (data) => {
		this.setState({
		    video_data: data
		});
	    }
	});

	
	subscribe(){
		//send ajax to server for subscribe request
		$.post({
			url: config.serverUrl + "/update/subscribe",
			data:{
				authorid:this.state.video_data.userid
				unsub: false	// TO DO: set this based on current subscription state
				},
			success: () => {} //on success, update subscribe button or something
		});
	
	}	
		
	render(){
		const source = GetURLParameter('video');
		return (
			<div className="container">

				<VideoPlayer source={'/video/'+source}/>	
		        <div>
		            <h1>Video Title</h1>
		            <div><a href=#{/*user page ?=video_data.userid*/} ><h2 className="media-heading">{video_data.username}</h2></a>
		                <button className="btn btn-default subscribe-button" onClick={()=>subscribe()}>Subscribe</button>
		            </div>
		            <p> </p>
		            <p>{this.state.video_data.description}</p>
		            <ReportDialog/>
		        </div>
		    </div>
		);
	}
}

className VideoPlayer extends React.Component{	
	render(){
		return (
			<video id="video-player" width="960" height="540" controls="true">
		      <source src={this.props.source} type="video/mp4" />
		      <source src={this.props.source} type="video/webm" />
		      Your browser does not support the video tag.
		    </video> 	
		);
	}
}

className ReportDialog extends React.Component{
	constructor(props) {
		super(props);
		this.state = {
		    visible: false,
		};
	}
	submitReport(){
		//submit report to server (by ajax)
		$.post({
			url: config.serverUrl + "/update/report",
			data:{
				videoid: this.state.video_data.video_id,
				report_text: $("#report-text").value
			},
			success: () => {this.setState({visible: false})}
		});
	}
	render(){
		
		return (
			<div>
                <p><a className="report-button" onClick={() => this.setState({visible: !this.state.visible})}>Report</a></p>
                {
                	if(this.state.visible)
                		return(
	                <textarea id="report-text"></textarea>
	                <p>
	                    <button className="btn btn-default" onClick={() => this.submitReport()}>Submit Report</button>
	                    <button className="btn btn-default" onClick={() => this.setState({visible: false})}>Cancel</button>
	                </p>
	                );
                }
            </div>
        );
	}
}


ReactDOM.render(
	    <Video />,
	    document.getElementById('root')
	);


//reads URL parameters - from http://www.jquerybyexample.net/2012/06/get-url-parameters-using-jquery.html
function GetURLParameter(sParam) {
	var sPageURL = window.location.search.substring(1);
  var sURLVariables = sPageURL.split('&');
  for (var i = 0; i < sURLVariables.length; i++) {
      var sParameterName = sURLVariables[i].split('=');
      if (sParameterName[0] == sParam) {
      	return sParameterName[1];
      }
  }
}
