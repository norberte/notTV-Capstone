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
    this.iterate = this.iterate.bind(this);
    }

    displayName: 'Tabs';

    handleClick(index, event){
	    event.preventDefault();
	    this.setState({
	           selected: index
	    });
    }

    iterate(i, event){
        event.preventDefault();
        this.setState({
            selected: this.state.selected + i
        });
    }

  render(){
    //The below two cont's are the condition for when it is tru to have a button disabled
    //Currently, does not work.
    const disableNext = this.state.selected == this.props.children.length;
    const disablePrev = this.state.selected == 0;
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

     <div id = "iterateButtons">
         <button className="iterationButtons" type="button" onClick = {(e)=>this.iterate(1, e)} disabled={this.disableNext}>Next</button>
         <button className="iterateButtons" type="button" onClick = {(e)=>this.iterate(-1, e)} disabled={this.disablePrev}>Previous</button>
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
	      <div className="tab">
		<h1>{this.props.label}</h1>
		<p>
		  <input type="file" name="videoFile" accept="video/*"
			 ref={(input) => { this.videoFile = input; }} onChange={()=>this.props.onChange(this.videoFile.files)}/>
		</p>
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
		license: '',
		thumbnailurl: '/img/default-placeholder-300x300.png', // ignore for now.
		tags: '',
		userid: '-1' // TODO: mediabox user id.
	    }
	};

	this.handleChange = this.handleChange.bind(this);
	this.fileChange = this.fileChange.bind(this);
	this.handleSubmit = this.handleSubmit.bind(this);
    }

    //handles a change in an input from the form and gives that new change to the state.
    handleChange(e){
	const state = this.state;
	state.formData[e.target.name] = e.target.value;
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
	    success: (torrentFile) => {
		// TODO: loop through formData
		const formData = {
			title: this.state.formData.title,
			description: this.state.formData.description,
			version: this.state.formData.version,
			filetype: this.state.videoFile.type,
			license: this.state.formData.license,
			downloadurl: torrentFile,
			thumbnailurl: this.state.formData.thumbnailurl,
			tags: this.state.formData.tags,
			userid: this.state.formData.userid
		};

		// insert video
		$.ajax({
		    type: "POST",
		    url: config.serverUrl + "/upload/videoSubmission",
		    contentType: 'application/json',
		    processData: false,
		    data: JSON.stringify(formData),
		    success: (response) => {
			console.log(response);
			alert("Successfully uploaded!");
		    },
		    error: (response) => {
			console.log(response);
		    }
		});
	    },
	    error: (response) => {
		console.log(response);
	    }
	});
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
			<input type = "text" name="title" value={this.state.formData.title}  onChange={this.handleChange}/>

			<h2>Description</h2>
			<textarea rows="4" cols="50" name="description" value={this.state.formData.description}  onChange={this.handleChange}>
			  Enter the description of the video here.
			</textarea>

			<h2>Tags</h2>
			<textarea rows="4" cols="50" name="tags" value={this.state.formData.tags}  onChange={this.handleChange}>
			  Enter keywords seperated by a comma. For example: Edmonton,Winter,Driving,Fast
			</textarea>
		      </div>
		    </Pane>

		    <Pane label="More Video Details">
		      <div className="tab">
			<h2>License</h2>
			Allow adaptations of this work to be shared?<br/>
			<input type="radio" name="license" value="Y" checked={this.state.formData.license === 'Y'}  onChange={this.handleChange}/>Yes&emsp;
			<input type="radio" name="license" value="N" checked={this.state.formData.license === 'N'}  onChange={this.handleChange}/>No&emsp;
			<input type="radio" name="license" value="SA" checked={this.state.formData.license === 'SA'}  onChange={this.handleChange}/>Yes, as long as others share alike

			<h2>Version</h2>
			<input type = "number" name="version" value={this.state.formData.version}  onChange={this.handleChange}/>
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
