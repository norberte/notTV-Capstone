
const React = require("react");
const ReactDOM = require("react-dom");



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
            videoId: props.videoId,
            videoName: props.videoName,
            video_data: ""
        };

        //get video metadata from server
        $.get({
            url: config.serverUrl + "/info/video-data",
            data: {videoId: this.state.videoId},
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
              <VideoPlayer source={'/video/'+ this.state.videoName}/>    
              <div>
                <h1>{this.state.title}</h1>
                <div>
                  <a href=/userprofile/{this.state.username} ><h2 className="media-heading">{this.state.username}</h2></a>
                  <SubscribeButton 
                     subState = {this.state.subscribed ? "Unsubscribe" : "Subscribe"} 
                     onClick = {() => this.subscribe()}
                    />
                </div>
                <p></p>
                <p>{this.state.description}</p>
                <ReportDialog videoId={this.state.videoId}/>
              </div>
            </div>
        );
    }
}

ReactDOM.render(
    <Video videoId={videoId} videoName={videoName}/>,
    document.getElementById('root')
);
