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
                <div className="thumbnail no-margin">
	                <a href={this.props.entry.url}>
	                  <img className="video-thumbnail" src={this.props.entry.thumbnail}/>
	                  <div className="caption">
	                    <h3 className="no-margin">{this.props.entry.title} </h3>
	                  </div>
	               </a>
	               <a href={this.props.entry.authorUrl}>
		               <div className="caption">
		           			<h3 className="no-margin">{this.props.entry.author} </h3>
		           		</div>	
	               </a>
	           </div>
            </div>
        );
    }
}


class Browse extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            categories: [],
            videos: [],
            thumbnails: []
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
        this.dowloadThumbnails = this.dowloadThumbnails.bind(this);
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
    
    // provide a list of thumbnail files to be downloaded, then store returned files inside the state
    dowloadThumbnails(fileName){
    	$.get({
            url: '/process/downloadThumbnail',
            data: {
            	thumbnailName: fileName
            },
            dataType: "json",
            success: (data) => {
            	console.log("Successfully downloaded thumbnails. Response = " + data);
            },
            error: (response) => {
            	console.log("Could not download thumbnails");
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
                {this.dowloadThumbnails('index.png')}
                {this.dowloadThumbnails('default-placeholder-300x300.png')}
              <div className="col-md-10 results-container">
                <TopBar/>
                <div className="row browse-body">
                  <div className="col-md-12">
                    <CarouselLayout thumbnailClass={VideoThumbnail} title="Subscribed" entries={this.state.videos} thumbnail = {this.state.thumbnails}  />
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
