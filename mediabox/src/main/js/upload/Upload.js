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
	event.preventDefault();
	this.setState({
	    selected: index
	});

constructor(props){
    super(props);
    this.state = {
        selected: 0
    };
}

  displayName: 'Tabs';

  _renderContent(){
    return(
      <div className="tab__content">
      {this.props.children[this.state.selected]}
      </div>
    );
  }

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

     <div id = "iterateButtonsDiv">
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

    //gets data from state
    const formData = this.state;
    console.log(formData);

    //DO AJAX jQUERY SUBMIT
    //Send Form data
    $.ajax({
        type:   "POST",
        url:    "SERVER_URL_GOES_HERE",
        data:   formData,
        success:function(){
            console.log('successful submit')
        }.bind(this)
    });

}

render(){
  return(

      <div id="formDiv">
      <form id="uploadVideo" method="post" onSubmit={this.handleSubmit} commandName="videoForm">
      <fieldset>
        <Tabs selected={0}>

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
>>>>>>> Upgraded form to store data across tabs in the REACT state. This data is then used in an ajax request to submit the form asynchronously.
>>>>>>> Upgraded form to store data across tabs in the REACT state. This data is then used in an ajax request to submit the form asynchronously.
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
