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
