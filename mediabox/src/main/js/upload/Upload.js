import NavBar from '../NavBar.js';
import CriteriaList from './CriteriaList.js';
import BreadCrumb from './BreadCrumb.js';
const React = require("react");
const ReactDOM = require("react-dom");

class CategoryPane extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            categories: [],
            currValues: []
        };

        // get the categories.
        $.get({
            url: config.serverUrl + "/info/categories",
            dataType: "json",
            success: (data) => {
                this.setState({
                    categories: data,
                    currValues: data.length > 0 ? data[0].values : []
                });
            },
            failure: (data) => {
                console.log(data);
                this.setState({
                    categories: ["Empty"],
                    currValues: ["Can't connect to the server"]
                });
            }
        });

        this.selectChange = this.selectChange.bind(this);
        this.selected = this.selected.bind(this);
    }

    selectChange(option) {
        this.setState({
            currValues: this.state.categories[option.target.selectedIndex].values
        });
    }

    selected(event) {
        const selected = this.props.data.tags;
        const id = parseInt(event.target.id);
        if(selected.has(id))
            selected.delete(id);
        else
            selected.add(id);
        this.props.setTags(selected);
    }
    
    render() {
        return (
            <div className="upload-pane">
              <h1>Video Categories/Tags</h1>
              <select className="selectpicker" onChange={this.selectChange}>
                {
                    this.state.categories.map((cat, idx) => {
                        return <option idx={idx} key={idx}>{cat.name}</option>;
                    })
                }
              </select>
              <div className="list-group">
                {
                    this.state.currValues.map((val, idx) => {
                        return <a className={(this.props.data.tags.has(val.id) ? "cat-selected " : "") + "list-group-item"}
                                      key={idx} id={val.id} onClick={this.selected}>{val.name}</a>;
                    })
                }
              </div>
            </div>
        );
    }
}

// Basic Info:
class FileInput extends React.Component {
    render() {
        const name = toCamel(this.props.title);
        const input_id = name + "-file";
        return(
            <div>
              <h3>{this.props.title}</h3>
              <div className="file-input">
                <label className="file-button" htmlFor={input_id}>Choose</label>
                <label className="file-label">{this.props.file === null ? "No file selected." : this.props.file.name}</label>
	        <input type="file" id={input_id} className="hidden" name={name} accept={this.props.accept} 
		       ref={(input) => { this.inputRef = input; }} onChange={()=>this.props.set(this.inputRef.files)}/>
	      </div>
            </div>
        );
    }
}

class TextInput extends React.Component {
    render() {
        return (
            <div>
              <h3>{this.props.title}</h3>
              <input type="text" name={toCamel(this.props.title)} value={this.props.value} onChange={this.props.handleChange}/>
            </div>
        );
    }
}

/* 
 Select Video and Thumbnail Files.
 */
class EssentialPane extends React.Component {
    render() {
	return (
	    <div className="upload-pane">
              <h1>Basic Info</h1>
              <TextInput title="Title" value={this.props.data.title} handleChange={this.props.handleChange("title")}/>
              <h3>Description</h3>
              <textarea rows="4" cols="50" name="description" value={this.props.data.description} onChange={this.props.handleChange("description")}>
                Enter the description of the video here.
              </textarea>
              <FileInput title="Video File" accept="video/*" set={this.props.fileChange("videoFile")} file={this.props.data.videoFile}/>
              <FileInput title="Thumbnail File" accept="img/*" set={this.props.fileChange("thumbnailFile")} file={this.props.data.thumbnailFile}/>
	    </div>
	);
    }
}

class ImgFilePane extends React.Component {
    render() {
	return (
	      <div className="tab">
		<h1>{this.props.label}</h1>
		<p>
		  <input type="file" name="videoThumbnail" accept="image/*"
			 ref={(input) => { this.videoThumbnail = input; }} onChange={()=>this.props.onChange(this.videoThumbnail.files)}/>
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
		thumbnailurl: '/img/default-placeholder-300x300.png', // this is the default video thumbnail... if user uploads an actual thumbnail, the backend will overwrite the default
		tags: '',
		userid: '-1' // TODO: get mediabox user id.
	    }
	};

        this.increment = this.increment.bind(this);
	this.handleChange = this.handleChange.bind(this);
	this.fileChange = this.fileChange.bind(this);
	this.imgFileChange = this.imgFileChange.bind(this);
	this.handleSubmit = this.handleSubmit.bind(this);
        this.setTags = this.setTags.bind(this);
    }

    setTags(newTags) {
        const fd = this.state.formData;
        fd['tags'] = newTags;
        this.setState({
            formData: fd
        });
    }
    
    increment(amount) {
        const newVal = this.state.curr + amount;

        if(newVal >=0 && newVal < this.props.items.length)
            this.setState({
                curr: newVal
            });
    }

    //handles a change in an input from the form and gives that new change to the state.
    handleChange(field){
        return (e) => {
	    const state = this.state;
	    state.formData[field] = e.target.value;
	    this.setState(state);
        };
    }

    fileChange(field) {
        return (files) => {
	    if(files.length > 0) { // Only one file for now.
                const data = this.state.formData;
	        data[field] = files[0];
                this.setState({
                    formData: data
                });
            }
        };
    }
    
    imgFileChange(files) {
    	if(files.length > 0) // Only one file for now.
    	    this.state.videoThumbnail = files[0];
    }
    
    //handles getting state data and giving it to the ajax submit.
    handleSubmit(e){
    	e.preventDefault();
    	
    	// initialize a "global" thumbnailURL with default value that each post request can access
    	var thumbnailURL = this.state.formData.thumbnailurl;
    	
    	const localForm = new FormData();
    	localForm.append('video', this.state.videoFile);
    	
    	// Start upload process on local mediabox server.
    	$.post({
    		url: '/process/upload',
    		data: localForm,
    		processData: false,  // tell jQuery not to process the data (because of the file)
    		contentType: false,  // tell jQuery not to set contentType
    		success: (torrentFile) => {
    			// make FormData object to store thumbnail image to be uploaded
    	    	const newForm = new FormData();
    	    	newForm.append('image', this.state.videoThumbnail);

    	    	// post request to upload thumbnail to server
    	        $.post({
    	    		url: config.serverUrl + '/upload/thumbnailSubmission',
    	    		data: newForm,
    	    		processData: false,  // not to process the data (because of the file)
    	    		contentType: false,  // not to set contentType
    	    		success: (thumbnailURL_returned) => {
    	    			// change thumnailURl, if it was uploaded to server, else, leave it as default value
    	    			if(thumbnailURL_returned !== ""){
    	    				thumbnailURL = thumbnailURL_returned.substring(thumbnailURL_returned.indexOf("/img/"));
    	    				console.log("Thumbnail Submission returned an actual THUMBNAIL URL: " + thumbnailURL_returned);
    	    			} else {
    	    				console.log("Thumbnail Submission returned empty String!");
    	    				// keep thumbnailURL to the default picture
    	    			}
    	    			
    	    			// ajax request to send video metadata to server
    	    			// TODO: loop through formData <-- someone else's comment .. not sure why this is needed
    	    			const formData = {
    	    					title: this.state.formData.title,
    	    					description: this.state.formData.description,
    	    					version: this.state.formData.version,
    	    					filetype: this.state.videoFile.type,
    	    					license: this.state.formData.license,
    	    					downloadurl: torrentFile,
    	    					thumbnailurl: thumbnailURL,
    	    					tags: this.state.formData.tags,
    	    					userid: this.state.formData.userid
    	    			};

    	    			// insert video-metadata
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
    	    			console.log("Thumbnail Submission DID NOT WORK!");
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
		      <ImgFilePane label="Select Video Thumbnail" onChange={this.imgFileChange}/>

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

/*
 A containter that iterates through a list of React Components.
*/
class UploadContainer extends React.Component {
    render() {
        const disableNext = this.props.curr === this.props.items.length - 1 ? " disabled" : "";
        const disablePrev = this.props.curr === 0 ? " disabled" : "";
        return (
            <div className="row">
              <div className="col-md-12 upload-container">
                <div className="row">
                  {this.props.items[this.props.curr]}
                </div>

                <div className="row">
                  <ul className="pager">
                    <li className={"previous" + disablePrev}>
                      <a onClick={()=>this.props.increment(-1)}>
                        <span aria-hidden="true">&larr;</span> Go Back
                      </a>
                    </li>
                    <li className={"next" + disableNext}>
                      <a onClick={()=>this.props.increment(1)}>
                        Continue <span aria-hidden="true">&rarr;</span>
                      </a>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
        );
    }
}

class Test extends React.Component {
    render() {
        return (
            <div>
              {this.props.content}
            </div>
        );
    }
}

class UploadForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            curr: 0
        };

        this.increment = this.increment.bind(this);
    }

    increment(amount) {
        const newVal = this.state.curr + amount;

        if(newVal >=0 && newVal < this.props.items.length)
            this.setState({
                curr: newVal
            });
    }
    render() {
        const CompClass = this.props.items[this.state.curr];
        return (
            <div className="col-md-12">
              <div className="row bc-parent">
                <BreadCrumb curr={this.state.curr} items={this.props.titles} />
              </div>
              <div className="row">
                <div className="col-md-7">
                  <div className="row">
                    <div className="col-md-12 upload-container">
                      <div className="row">
                        {  // Pass in all methods and data to the current component.
                            <CompClass
                                   data={this.state.formData}
                                   handleChange={this.handleChange}
                                   fileChange={this.fileChange}
                                   setTags={this.setTags}/>
                        }
                      </div>
                    </div>
                  </div>
                </div>
                <div className="col-md-5">
                  <CriteriaList/>
                </div>
              </div>
              <div className="row">
                <ul className="pager cat-nav">
                  <PrevButton increment={this.increment} curr={this.state.curr}/>
                  <NextButton increment={this.increment} submit={this.handleSubmit} curr={this.state.curr} length={this.props.items.length}/>
                </ul>
              </div>
            </div>
        );
    }
}

const ITEMS = [EssentialPane, CategoryPane];
const TITLES = ["Basic Info", "Video Tags"];
ReactDOM.render(<UploadForm items={ITEMS} titles={TITLES}/>, document.getElementById("root"));
