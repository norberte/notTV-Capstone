const React = require("react");
const ReactDOM = require("react-dom");

class Video extends React.Component{
	render(){
		const source = GetURLParameter('video')
		return (
			<VideoPlayer source={source}/>			
		);
	}
}

class VideoPlayer extends React.Component{
	
	render(){
		return (
			<video id="vid" width="960" height="540" controls="true">
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
