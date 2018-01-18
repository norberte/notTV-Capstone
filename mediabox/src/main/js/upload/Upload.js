//import ajaxSubmit from '../ajaxSubmit.js';
import NavBar from '../NavBar.js';
import './CriteriaList.js';
const React = require("react");
const ReactDOM = require("react-dom");

class Label extends React.Component {
    render() {
	const activeClass = this.props.active ? 'active' : '';
	return(
	    <li>
	      <a href="#"
		 className={activeClass}
		 onClick={this.props.handleClick}>
		{this.props.label}
	      </a>
	    </li>
	);
    }
}

class Tabs extends React.Component {
    constructor(props){
	super(props);
	this.state = {
	    selected: 0
	};

	this.handlClick = this.handleClick.bind(this);
    }
    
    displayName: 'Tabs';

    handleClick(index, event){
	event.preventDefault();
	this.setState({
	    selected: index
	});
    }

    render(){
	return(
	    <div className="tabs">
	      <ul className="tabs__labels">
		{
		    this.props.children.map((child, idx) => {
			console.log(child);
			return (
			    <Label
			       key={idx}
			       label={child.props.label}
			       active={this.state.selected === idx}
			       handleClick={(e)=>this.handleClick(idx, e)}/>
			);
		    })
		}
            </ul>
		<div className="tab__content">
		{this.props.children[this.state.selected]}
	    </div>
		</div>
	);
    }

}

class Pane extends React.Component {
    displayName: 'Pane';

    render(){
        return(
            <div>
              {this.props.children}
            </div>
	);
    }
}

class App extends React.Component {
    constructor(props){
	super(props);
	this.state = {
	    videoFile: '',
	    videoPoster: '',
	    videoTitle: '',
	    videoDescription: '',
	    videoTags: '',
	    videoLanguage: '',
	    city: '',
	    country: '',
	    videoLicense: '',
	    videoRating: '',
	    videoVersion: '',
	    author: 'testUser'
	};

	this.handleChange = this.handleChange.bind(this);
	this.handleSubmit = this.handleSubmit.bind(this);
    }

    //handles a change in an input from the form and gives that new change to the state.
    handleChange(e){
	const state = this.state;
	state[e.target.name] = e.target.value;
	this.setState(state);
    }

    //handles getting state data and giving it to the ajax submit.
    handleSubmit(e){
	e.preventDefault();

	// get state data
	const formData = this.state;
	//DO AJAX jQUERY SUBMIT
	//Send Form data
	$.post({
            url:    config.serverUrl,
            data:   {
		formData: formData
	    },
            success:(response) => {
		console.log('successful submit');
            },
	    complete:(response) => console.log(response)
	});

    }

    render(){
	return(
	    <div id="formDiv">
	      <form id="uploadVideo" method="post" onSubmit={this.handleSubmit} commandname="videoForm">
		<fieldset>
		  <Tabs>

		    <Pane label="Select Video File">
		      <div className="tab">
			<h1>Select Video File</h1>
			<p><input type="file" name="videoFile" accept="video/*" value={this.state.videoFile} onChange={this.handleChange}/></p>
		      </div>
		    </Pane>


		    <Pane label="Select Video Poster">
		      <div className="tab">
			<h1>Video Poster</h1>
			<p><input type = "file" name="videoPoster" accept="image/*" value={this.state.videoPoster}  onChange={this.handleChange}/></p>
		      </div>
		    </Pane>


		    <Pane label="Add Video Details">
		      <div className="tab">
			<h1>Video Details</h1>
			<h2>Video Title</h2>
			<input type = "text" name="videoTitle" value={this.state.videoTitle}  onChange={this.handleChange}/>

			<h2>Description</h2>
			<textarea rows="4" cols="50" name="videoDescription" value={this.state.videoDescription}  onChange={this.handleChange}>
			  Enter the description of the video here.
			</textarea>

			<h2>Tags</h2>
			<textarea rows="4" cols="50" name="videoTags" value={this.state.videoTags}  onChange={this.handleChange}>
			  Enter keywords seperated by a comma. For example: Edmonton,Winter,Driving,Fast
			</textarea>

			<h2>Language</h2>
			<select name = "videoLanguage" value={this.state.videoLanguage}  onChange={this.handleChange}>
			  <option value="English">English</option>
			  <option value="French">French</option>
			  <option value="Spanish">Spanish</option>
			  <option value="Other">Other</option>
			</select>
		      </div>
		    </Pane>

		    <Pane label="More Video Details">
		      <div className="tab">
			<h2>City</h2>
			<input type = "text" name="city" value={this.state.city}  onChange={this.handleChange}/>

			<h2>Country</h2>
			<input type = "text" name="country" value={this.state.country}  onChange={this.handleChange}/>

			<h2>License</h2>
			Allow adaptations of this work to be shared?<br/>
			<input type="radio" name="videoLicense" value="Y" checked={this.state.videoLicense === 'Y'}  onChange={this.handleChange}/>Yes&emsp;
			<input type="radio" name="videoLicense" value="N" checked={this.state.videoLicense === 'N'}  onChange={this.handleChange}/>No&emsp;
			<input type="radio" name="videoLicense" value="SA" checked={this.state.videoLicense === 'SA'}  onChange={this.handleChange}/>Yes, as long as others share alike

			<h2>Content Rating</h2>
			<input type="radio" name="videoRating" value="All Audiences" checked={this.state.videoRating === 'All Audiences'}  onChange={this.handleChange}/>All Audiences&emsp;
			<input type="radio" name="videoRating" value="Mature" checked={this.state.videoRating === 'Mature'}  onChange={this.handleChange}/>Mature

			<h2>Version</h2>
			<input type = "number" name="videoVersion" value={this.state.videoVersion}  onChange={this.handleChange}/>
		      </div>
		    </Pane>
		    
		    <Pane label="Submit Video">
		      <div className="tab">
			<h1>Submit Video</h1>
			<input type="hidden" name="author" value={this.state.author}  onChange={this.handleChange}/>
			<input type="submit" value="Submit"/>
		      </div>
		    </Pane>
		  </Tabs>
		</fieldset>
	      </form>
	    </div>
	);
    }
}

ReactDOM.render(<App />, document.getElementById("rootLeft"));
