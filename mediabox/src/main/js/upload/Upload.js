//import TopBar from '../NavBar.js';

const React = require("react");
const ReactDOM = require("react-dom");


const Tabs = React.createClass({
  displayName: 'Tabs',

  getDefaultProps(){
    return{
      selected: 0
    };
  },

  getInitialState(){
    return{
      selected: this.props.selected
    };
  },

  _renderContent(){
    return(
      <div className="tab__content">
      {this.props.children[this.state.selected]}
      </div>
    );
  },

  _renderTitles(){
    function labels(child, index){
      let activeClass = (this.state.selected === index ? 'active' : '');
      return(
        <li key={index}>
          <a href="#"
            className={activeClass}
            onClick={this.handleClick.bind(this, index)}>
            {child.props.label}
          </a>
        </li>
      );
    }
    return(
        <ul className="tabs__labels">
          {this.props.children.map(labels.bind(this))}
        </ul>
    );
  },

  handleClick(index, event){
    event.preventDefault();
    this.setState({
      selected: index
    });
  },

  propTypes:{
    selected: React.PropTypes.number,
    children: React.PropTypes.oneOfType([
      React.PropTypes.array,
      React.PropTypes.element
    ]).isRequired
  },

  render(){
    return(
      <div className="tabs">
        {this._renderTitles()}
        {this._renderContent()}
      </div>
    );
  }
});


const Pane = React.createClass({
displayName: 'Pane',
propTypes:{
  label: React.PropTypes.string.isRequired,
  children: React.PropTypes.element.isRequired
},

render(){
  return(
    <div>
      {this.props.children}
    </div>
  );
}
});


const App = React.createClass({
render(){
  return(

    <div id="formDiv">
    <form id="uploadVideo" action="/videoSubmission" method="post" commandName="videoForm">
    <fieldset>
      <Tabs selected={0}>

        <Pane label="Select Video File">
        <div className="tab">
        <h1>Select Video File</h1>
          <p><input type="file" name="videoFile" accept="video/*"/></p>
        </div>
        </Pane>


        <Pane label="Select Video Poster">
        <div className="tab">
        <h1>Video Poster</h1>
          <p><input type = "file" name="videoPoster" accept="image/*"/></p>
        </div>
        </Pane>


        <Pane label="Add Video Details">
        <div className="tab">
        <h1>Video Details</h1>
          <h2>Video Title</h2>
          <input type = "text" name="videoTitle"/>

          <h2>Description</h2>
          <textarea rows="4" cols="50" name="videoDescription">
          Enter the description of the video here.
          </textarea>

          <h2>Tags</h2>
          <textarea rows="4" cols="50" name="videoTags">
          Enter keywords seperated by a comma. For example: Edmonton,Winter,Driving,Fast
          </textarea>

          <h2>Language</h2>
          <select name = "videoLanguage">
            <option value="English">English</option>
            <option value="French">French</option>
            <option value="Spanish">Spanish</option>
            <option value="Other">Other</option>
          </select>

          <h2>City</h2>
          <input type = "text" name="city"/>

          <h2>Country</h2>
          <input type = "text" name="country"/>

          <h2>License</h2>
          Allow adaptations of this work to be shared?<br/>
          <input type="radio" name="videoLicense" value="Y"/>Yes&emsp;
          <input type="radio" name="videoLicense" value="N"/>No&emsp;
          <input type="radio" name="videoLicense" value="SA"/>Yes, as long as others share alike

          <h2>Content Rating</h2>
          <input type="radio" name="videoRating" value="All Audiences"/>All Audiences&emsp;
          <input type="radio" name="videoRating" value="Mature"/>Mature

          <h2>Version</h2>
          <input type = "text" name="videoVersion"/>

        </div>
        </Pane>

        <Pane label="Submit Video">
        <div className="tab">
        <h1>Submit Video</h1>
          <input type="submit" value="Submit"/>
          <input type="hidden" name="author" value="testUser"/>
        </div>
        </Pane>


      </Tabs>
      </fieldset>
    </form>
    </div>


  );
}
});

const Criteria = React.createClass({
render(){
  return(
    <div id="subCrit">
      <h1>Video Submission Criteria</h1>
      <ol>
        <li>
          All Videos submitted must be original content, remixed content is permitted if it does not infringe any copyrights.
        </li>
        <li>
          Traditional music videos and live performance videos will be accepted.
        </li>
        <li>
          Videos may be submitted by any member of the team that created them.
        </li>
        <li>
          Content contributors must be able to demonstrate the ownership and right to distribute the video on the internet to a worldwide audience.
        </li>
        <li>
          There is no minimum or maxiumum length for the videos.
        </li>
        <li>
          videos must be in high definition (minimum 720p, preferred 1080p).
        </li>
        <li>
          Multiple submissions are allowed.
        </li>
        <li>
          For each video you submit, you grant notTV a non-exclusive, worldwide electronic distribution license to stream the video on www.not.tv or any of the websites owned by notTV, anywhere in the world, electronically, in perpituity.
        </li>
        <li>
          All money after expenses, generated by the operations of notTV, is distributed to the member-owners of notTV.
        </li>
        <li>
          You must be a notTV member for your content to be eligible for commercial revenue generation.
        </li>
      </ol>
    </div>
  );
}
});

const NavBar = React.createClass({
render(){
  return(
      <div>
          <nav className="navbar navbar-default navigation-clean" style="margin:0px;background-color:rgb(0,0,0);background-image:url(&quot;null&quot;);border-color:#424342;border-radius:0px;">
              <div className="container">
                <div className="navbar-header">
                  <a className="navbar-brand navbar-link" href="#"><img className="notTVLogo" src="assets/img/notTV_logo_white.png" alt="notTV Logo"/></a>
                </div>
                  <div className="collapse navbar-collapse" id="navcol-1">
                      <form className="navbar-form navbar-left" target="_self" style="width:362px;margin:8px 0px 0 40px;padding:0;">
                          <div className="form-group" style="width:359px;">
                              <input className="form-control search-field" type="search" name="search" placeholder="Search" id="search-field" style="width:414px;"/>
                          </div>
                      </form>
                      <ul className="nav navbar-nav navbar-right">
                          <li className="active" role="presentation"><a href="#" style="background-image:url(&quot;null&quot;);background-color:rgb(0,0,0);">Watch </a></li>
                          <li role="presentation"><a href="#">Search </a></li>
                          <li role="presentation"><a href="#">Submit Video</a></li>
                          <li className="dropdown"><a className="dropdown-toggle" data-toggle="dropdown" aria-expanded="false" href="#">Account </a>
                              <ul className="dropdown-menu" role="menu">
                                  <li role="presentation"><a href="#">First Item</a></li>
                                  <li role="presentation"><a href="#">Second Item</a></li>
                                  <li role="presentation"><a href="#">Third Item</a></li>
                              </ul>
                          </li>
                      </ul>
                  </div>
              </div>
          </nav>
      </div>
  );
}
});

ReactDOM.render(<NavBar />, document.getElementById('rootNav'));
ReactDOM.render(<App />, document.getElementById("rootLeft"));
ReactDOM.render(<Criteria />, document.getElementById('rootRight'));
