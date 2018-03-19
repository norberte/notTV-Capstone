const React = require("react");
const ReactDOM = require("react-dom");
const Hls = require("hls.js");

class VideoPlayer extends React.Component{
    componentDidMount() {
        // Use hls library to access the video stream.
        if(Hls.isSupported()) {
            const hls = new Hls();
            hls.loadSource(this.props.source + "/index");
            hls.attachMedia(this.video);
            hls.on(Hls.Events.CAN_PLAY,() => {
                this.video.play();
            });
        }
        
    }
    render(){
        return (
            <video ref={(input) => { this.video = input; }} id="video-player" width="960" height="540" controls="true" src={null}>
              <source type="video/mp4" src={null}/>
              <source type="video/webm" src={null}/>
              Your browser does not support the video tag.
            </video>     
        );
    }
}

class SubscribeButton extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            subscribed: this.props.subscribed
        };
    }
    
    render(){
        
        let subState = this.props.subscribed ? "Unsubscribe" : "Subscribe";
        
        return (
            <button className="btn btn-default subscribe-button" onClick={()=>this.props.onClick()}>{this.props.subState}</button>
        );
    }
}

class ReportDialog extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            visible: false
        };
    }
    submitReport(){
        //submit report to server (by ajax)
        $.post({
            url: config.serverUrl + "/update/report",
            data:{
                videoId: this.props.videoId,
                reportText: $("#report-text").val()
            },
            success: () => {this.setState({visible: false});}
        });
    }
    render(){
        
        return (
            <div>
              <p><a className="report-button" onClick={() => this.setState({visible: !this.state.visible})}>Report</a></p>
              {
                  this.state.visible &&
                      <div>
                          <textarea id="report-text"></textarea>
                              <p>
                                  <button className="btn btn-default" onClick={() => this.submitReport()}>Submit Report</button>
                                  <button className="btn btn-default" onClick={() => this.setState({visible: false})}>Cancel</button>
                             </p>
                      </div>
               }
            </div>
        );
    }
}

class Video extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            video_data: ""
        };

        //get video metadata from server
        $.get({
            url: config.serverUrl + "/info/video-data",
            data: {videoId: this.props.videoId},
            dataType: "json",
            success: (data) => {
                this.setState({
                    video_data: {data},
                    id: data.id,
                    title: data.title,
                    description: data.description,
                    userid: data.userid,
                    username: data.username,
                    subscribed: data.subscribed    
                });
            }
        });
    }
    subscribe(){
        //send ajax to server for subscribe request
        const url = this.state.subscribed ?
                  config.serverUrl + "/update/unsubscribe"
                  : config.serverUrl + "/update/subscribe";
        $.post({
            url: url,
            data:{
                author: this.state.userid,
                subscriber: 1 // TODO: get id of whoever is using this mediabox.
            },
            success: () => this.setState({subscribed: !this.state.subscribed})
        });
        
    }
    render(){
        return (
            <div className="container">
              <VideoPlayer source={"/process/video-stream/" + this.props.videoId}/>    
              <div>
                <h1>{this.state.title}</h1>
                <div>
                  <a href={'/userProfile/' + this.state.username} ><h2 className="media-heading">{this.state.username}</h2></a>
                  <SubscribeButton 
                     subState = {this.state.subscribed ? "Unsubscribe" : "Subscribe"} 
                     onClick = {() => this.subscribe()}
                    />
                </div>
                <p></p>
                <p>{this.state.description}</p>
                <ReportDialog videoId={this.props.videoId}/>
              </div>
            </div>
        );
    }
}

// videoId from parameters automatically passed in default_page
ReactDOM.render(
    <Video videoId={videoId}/>,
    document.getElementById('root')
);
