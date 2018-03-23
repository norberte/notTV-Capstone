import NavBar from '../NavBar.js';
import CategoryFilter from './CategoryFilter.js';
import TopBar from './TopBar.js';
import CarouselLayout from '../CarouselLayout.js';
const React = require("react");
const ReactDOM = require("react-dom");

export default class VideoThumbnail extends React.Component {
    constructor(props) {
        super(props);

        this.getThumbnail = this.getThumbnail.bind(this);
        this.getThumbnail();
    }

    getThumbnail() {
        // get thumbnail if it isn't loaded already.
        if(!this.props.entry.thumbnail)
            utils.getThumbnail(this.props.entry.id, (file) => {
                this.props.entry.thumbnail = file;
                this.forceUpdate();
            });
    }

    render() {
        // need to call here because the constructor isn't always called for entries.
        // I think React may be reusing existing ones, so it doesn't create new VideoThumbnails?
        // We need to do it in the constructor also, because the component needs
        // to be mounted first to forceUpdate().
        this.getThumbnail();
        const thumbnail = this.props.entry.thumbnail ? this.props.entry.thumbnail : "/img/default-placeholder-300x300.png";
        return (
            <div className="col-md-2 no-padding">
              <div className="thumbnail no-margin">
	        <a href={"/process/download?videoId="+this.props.entry.id}>
                  <div className="video-thumbnail">
	            <img className="video-thumbnail-content" src={thumbnail}/>
                  </div>
	          <div className="caption">
	            <h4 className="no-margin">{this.props.entry.title} </h4>
	          </div>
	        </a>
	        <a href={"/userProfile/" +this.props.entry.author.username}>
		  <div className="caption">
		    <h6 className="no-margin">{this.props.entry.author.username} </h6>
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
            searchText: "",
            filters: [],
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
    update_videos() {
        $.get({
            url: config.serverUrl + "/info/videos",
            data: {
                searchText: this.state.searchText,
                categories: this.state.filters
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
              <div className="filteringHeader">
                  <h2>Filtering</h2>
              </div>
                <CategoryFilter
                   categories={this.state.categories}
                   selected={this.state.filters}
                   update_selected={(selected)=> {
                       this.setState({filters: selected});
                       this.update_videos(); // always update when filters change.
                   }}/>
              </div>
              <div className="col-md-10 results-container">
                <TopBar handleChange={(e)=>this.setState({searchText: e.target.value})}
                  updateVideos={this.update_videos}
                  searchText={this.state.searchText}/>
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
