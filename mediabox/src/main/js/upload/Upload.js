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

class FilePane extends React.Component {
    render() {
	return (
	    <div>
	      <div className="tab">
		<h1>{this.props.label}</h1>
		<p>
		  <input type="file" name="videoFile" accept="video/*"
			 ref={(input) => { this.videoFile = input; }} onChange={()=>this.props.onChange(this.videoFile.files)}/>
		</p>
	      </div>
	    </div>
	);
    }
}

class App extends React.Component {
    constructor(props){
	super(props);
	this.state = {
	    videoFile: null,
	    videoThumbnail: null,
	    formData: {
		title: '',
		description: '',
		version: '',
		filetype: '',
		license: '',
		downloadurl: '', // get from mediabox server
		thumbnailurl: '/img/default-placeholder-300x300.png', // ignore for now.
		tags: '',
		userid: '-1' // TODO: mediabod user id.
	    }
	};

	this.handleChange = this.handleChange.bind(this);
	this.fileChange = this.fileChange.bind(this);
	this.handleSubmit = this.handleSubmit.bind(this);
    }

    //handles a change in an input from the form and gives that new change to the state.
    handleChange(e){
	const state = this.state;
	state[e.target.name] = e.target.value;
	this.setState(state);
    }

    fileChange(files) {
	if(files.length > 0) // Only one file for now.
	    this.state.videoFile = files[0];
    }

    //handles getting state data and giving it to the ajax submit.
    handleSubmit(e){
	e.preventDefault();

	const localForm = new FormData();
	localForm.append('video', this.state.videoFile);
	// Start upload process on local mediabox server.
	$.post({
	    url: '/process/upload',
	    data: localForm,
	    processData: false,  // tell jQuery not to process the data (because of the file)
	    contentType: false,  // tell jQuery not to set contentType
	    success: (response) => {
		console.log(response);
	    },
	    error: (response) => {
		console.log(response);
	    }
	});
	
	// // get state data
	// const formData = this.state;
	// //DO AJAX jQUERY SUBMIT
	// //Send Form data
	// $.post({
        //     url:    config.serverUrl,
        //     data:   {
	// 	formData: formData
	//     },
        //     success:(response) => {
	// 	console.log('successful submit');
        //     },
	//     complete:(response) => console.log(response)
	// });
    }

    render(){
	return(
	    <div id="formDiv">
	      <form id="uploadVideo" method="post" onSubmit={this.handleSubmit} commandname="videoForm">
		<fieldset>
		  <Tabs>
		    <FilePane label="Select Video File" onChange={this.fileChange}/>
		    <FilePane label="Select Video Thumbnail" onChange={this.fileChange}/>
		    
		    <Pane label="Add Video Details">
		      <div className="tab">
			<h1>Video Details</h1>
			<h2>Video Title</h2>
			<input type = "text" name="videoTitle" value={this.state.formData.title}  onChange={this.handleChange}/>

			<h2>Description</h2>
			<textarea rows="4" cols="50" name="videoDescription" value={this.state.formData.description}  onChange={this.handleChange}>
			  Enter the description of the video here.
			</textarea>

			<h2>Tags</h2>
			<textarea rows="4" cols="50" name="videoTags" value={this.state.formData.tags}  onChange={this.handleChange}>
			  Enter keywords seperated by a comma. For example: Edmonton,Winter,Driving,Fast
			</textarea>
		      </div>
		    </Pane>

		    <Pane label="More Video Details">
		      <div className="tab">
			<h2>License</h2>
			Allow adaptations of this work to be shared?<br/>
			<input type="radio" name="videoLicense" value="Y" checked={this.state.formData.license === 'Y'}  onChange={this.handleChange}/>Yes&emsp;
			<input type="radio" name="videoLicense" value="N" checked={this.state.formData.license === 'N'}  onChange={this.handleChange}/>No&emsp;
			<input type="radio" name="videoLicense" value="SA" checked={this.state.formData.license === 'SA'}  onChange={this.handleChange}/>Yes, as long as others share alike

			<h2>Version</h2>
			<input type = "number" name="videoVersion" value={this.state.formData.version}  onChange={this.handleChange}/>
		      </div>
		    </Pane>
		    
		    <Pane label="Submit Video">
		      <div className="tab">
			<h1>Submit Video</h1>
			<input type="hidden" name="author" value="-1"  onChange={this.handleChange}/>
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
