import NavBar from '../NavBar.js';
import CategoryFilter from './CategoryFilter.js';
import TopBar from './TopBar.js';
import CarouselLayout from '../CarouselLayout.js';
const React = require("react");
const ReactDOM = require("react-dom");

class VideoThumbnail extends React.Component {
    render() {
        return (
            <div className="col-md-2 no-padding">
              <a href={this.props.entry.url}>
                <div className="thumbnail no-margin">
                  <img className="video-thumbnail" src={this.props.entry.thumbnail}/>
                  <div className="caption">
                    <h3 className="no-margin">{this.props.entry.title} </h3>
                  </div>
                </div>
              </a>
            </div>
        );
    }
}

class Browse extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            categories: [],
            videos: []
        };
        
        // get the categories.
        $.get({
            url: config.serverUrl + "/info/categories",
            dataType: "json",
            success: (data) => {
                this.setState({
                    categories: data
                });
            }
        });

        // TODO: Get videos for each category.
        // get videos
        this.update_videos([]);
        
        this.update_videos = this.update_videos.bind(this);
    }

    /**
     * Gets a list of videos filtered by 'filters'
     * filters = [ cat1_id, cat2_id, ...]
     */
    update_videos(filters) {
        $.get({
            url: config.serverUrl + "/info/videos",
            data: {
                categories: filters
            },
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
    
    render() {
        return (
            <div className="row display-flex categories-row">
              <div className="col-md-2 categories-column">
                <CategoryFilter
                   categories={this.state.categories}
                   update_handler={this.update_videos}/>
              </div>
              <div className="col-md-10 results-container">
                <TopBar/>
                <div className="row browse-body">
                  <div className="col-md-12">
                    <CarouselLayout thumbnailClass={VideoThumbnail} title="Subscribed" entries={this.state.videos}/>
                  </div>
                </div>
              </div>
            </div>
        );
    }
}

ReactDOM.render(
    <Browse />,
    document.getElementById('root')
);
