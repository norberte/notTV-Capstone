const React = require("react");

// default image: ./img/playlist.png
export default class PlaylistThumbnail extends React.Component {
    render() {
        return (
            <div className="col-md-2 no-padding">
              <a href={this.props.entry.url}>
                <div className="thumbnail no-margin">
                  <img className="video-thumbnail" src={this.props.entry.thumbnail} />
                  <div className="caption">
                    <h3 className="no-margin">{this.props.entry.title} </h3>
                  </div>
                </div>
              </a>
            </div>
        );
    }
}
