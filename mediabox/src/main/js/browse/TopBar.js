const React = require("react");

class SearchTagDropdown extends React.Component {
    render() {
        return (
            <div className="dropdown search-tag-dropdown-container">
              <button className="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false" type="button" className="search-tag-dropdown-button">
                <span className="caret"></span>
              </button>
              <ul role="menu" className="dropdown-menu">
                <li role="presentation"><a onClick={(e) => {this.props.changeSearchTarget('title')}}>Title </a></li>
                <li role="presentation"><a onClick={(e) => {this.props.changeSearchTarget('uploader')}}>Uploader </a></li>
              </ul>
            </div>
        );
    }
}

class SearchBar extends React.Component {
    constructor(props) {
        super(props);
        this.handleKeyPress = this.handleKeyPress.bind(this);
    }

    handleKeyPress(e) {
        if (e.key === 'Enter') { // apply 
            this.props.updateVideos();
        }
    }
    
    render() {
        return (
            <input type="text" placeholder="Search" className="search-bar"
                   value={this.props.value} onChange={this.props.handleChange} onKeyPress={this.handleKeyPress}/>
        );
    }
}

class ResultOrder extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            order: "Newest"
        }
    }
    render() {
        return (
            <div className="result-order-container">
              <span className="result-order-text">Order: </span>
              <div className="dropdown result-order-button">
                <button className="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown" aria-expanded="false" type="button">
                 {this.state.order} <span className="caret"></span>
                </button>
                <ul role="menu" className="dropdown-menu">
                  <li role="presentation"><a onClick={(e) => {this.setState({order: "Newest"}); this.props.changeOrder('time asc')}}>Newest </a></li>
                  <li role="presentation"><a onClick={(e) => {this.setState({order: "Oldest"});this.props.changeOrder('time desc')}}>Oldest </a></li>
                  <li role="presentation"><a href="#"><s>Popular </s></a></li>
                  <li role="presentation"><a href="#"><s>View Count</s></a></li>
                </ul>
              </div>
            </div>
        );
    }
}

class Icon extends React.Component {
    render() {
        return (
            <button className="btn btn-default btn-sm" type="button">
              <i className={"glyphicon glyphicon-" + this.props.glyph}/>
            </button>
        );
    }
}

class LayoutSelector extends React.Component {
    render() {
        return (
            <div role="group" className="btn-group layout-selector-group">
              <Icon glyph="align-justify"/>
              <Icon glyph="list"/>
              <Icon glyph="th-large"/>
              <Icon glyph="film"/>
            </div>
        );
    }
}

// Main container that defines how the above components are layed out.
export default class TopBar extends React.Component {
    render() {
        return (
            <div className="row">
              <div className="col-md-1 search-tag-dropdown-parent">
                <SearchTagDropdown changeSearchTarget={this.props.changeSearchTarget}/>
              </div>
              <div className="col-md-8">
                <SearchBar handleChange={this.props.handleChange}
                  updateVideos={this.props.updateVideos}
                  values={this.props.searchText}/>
              </div>
              <div className="col-md-3">
                <div className="row">
                  <div className="col-md-5">
                    <ResultOrder changeOrder={this.props.changeOrder}/>
                  </div>
                  <div className="col-md-7">
                    <LayoutSelector />
                  </div>
                </div>
              </div>
            </div>
        );
    }
}
