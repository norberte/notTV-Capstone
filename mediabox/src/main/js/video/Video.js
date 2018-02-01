const React = require("react");
const ReactDOM = require("react-dom");

class Video extends React.Component{
	
	render(){
		const source = GetURLParameter('video');
		return (
			<VideoPlayer source={'/video/'+source}/>	
		);
	}
}

class VideoPlayer extends React.Component{
	
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
