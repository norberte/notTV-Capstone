const React = require("react");

class SearchTagDropdown extends React.Component {
    render() {
        return (
            <div className="dropdown search-tag-dropdown-container">
              <button className="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false" type="button" className="search-tag-dropdown-button">
                <span className="caret"></span>
              </button>
              <ul role="menu" className="dropdown-menu">
                <li role="presentation"><a href="#">Title </a></li>
                <li role="presentation"><a href="#">Uploader </a></li>
                <li role="presentation"><a href="#">Length </a></li>
              </ul>
            </div>
        );
    }
}

class SearchBar extends React.Component {
    render() {
        return <input type="text" placeholder="Search" className="search-bar"/>;
    }
}

class ResultOrder extends React.Component {
    render() {
        return (
            <div className="result-order-container">
              <span className="result-order-text">Order: </span>
              <div className="dropdown result-order-button">
                <button className="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown" aria-expanded="false" type="button">
                  Popular <span className="caret"></span>
                </button>
                <ul role="menu" className="dropdown-menu">
                  <li role="presentation"><a href="#">Popular </a></li>
                  <li role="presentation"><a href="#">Newest </a></li>
                  <li role="presentation"><a href="#">View Count</a></li>
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
                <SearchTagDropdown />
              </div>
              <div className="col-md-8">
                <SearchBar />
              </div>
              <div className="col-md-3">
                <div className="row">
                  <div className="col-md-5">
                    <ResultOrder />
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
