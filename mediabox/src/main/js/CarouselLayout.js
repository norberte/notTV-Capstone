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

/*
 * Usage: pass in a list of entries to be displayed, and a thumbnailClass to render each entry.
 * The thumbnailClass will recieve a single props: entry={entry}, where entry is one of the items in the entries prop.
 * @param title - title of Carousel.
 * @param entries - list of data for entries.
 * @param thumbnailClass - class to render each entry.
 */
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
        if(this.state.start + 6 < this.props.entries.length)
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
        let rows = this.props.entries.slice(
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
                      { rows.map((entry, idx) => <this.props.thumbnailClass key={idx} entry={entry}/>) }
                    </div>
                  </div>
                  <ArrowButton dir="right" handler={this.next}/>
                </div>
              </div>
            </div>
        );
    }
}