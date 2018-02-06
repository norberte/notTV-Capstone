const React = require("react");

class ArrowButton extends React.Component {
    render() {
        return (
            <div className="col-md-1 arrow-button">
              <button className="btn btn-default arrow-button-icon" type="button" onClick={this.props.handler}>
                <i className={"glyphicon glyphicon-chevron-" + this.props.dir}/>
              </button>
            </div>
        );
    }
}

export default class CarouselLayout extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            start: 0
        };

        this.next = this.next.bind(this);
        this.prev = this.prev.bind(this);
    }

    next() {
        // check if there is a video to the right not shown.
        if(this.state.start + 6 < this.props.videos.length)
            this.setState({
                start: this.state.start + 1
            });
    }

    prev() {
        // check if there is a video to the left not shown.
        if(this.state.start > 0)
            this.setState({
                start: this.state.start - 1
            });
    }
    
    render() {
        let rows = this.props.videos.slice(
            this.state.start,
            this.state.start + 6
        );
        return (
            <div className="row carousel-layout">
              <div className="col-md-12">
                <div className="row">
                  <div className="col-md-11 col-md-offset-1">
                    <span>{this.props.title} </span>
                  </div>
                </div>
                <div className="row display-flex">
                  <ArrowButton dir="left" handler={this.prev}/>
                  <div className="col-md-10">
                    <div className="row row-eq-height">
                      { rows.map((video, idx) => <this.props.thumbnailClass key={idx} video={video}/>) }
                    </div>
                  </div>
                  <ArrowButton dir="right" handler={this.next}/>
                </div>
              </div>
            </div>
        );
    }
}
