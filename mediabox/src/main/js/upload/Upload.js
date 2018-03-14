import NavBar from '../NavBar.js';
import CriteriaList from './CriteriaList.js';
import BreadCrumb from './BreadCrumb.js';
const React = require("react");
const ReactDOM = require("react-dom");

function toCamel(str) {
    const name = str.replace(" ", "");
    return name.charAt(0).toLowerCase() + name.slice(1);
}

// gets the categories from the server and handles the user selecting them.
// args: data - dictionary with tags Set
// setTags - method to call to update the tags.
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


// Buttons for navigating the UploadForm. These handle the logic of disabling themselves.
// args: curr - current position, length: total number of positions
// handleSubmit - what to do upon submission, increment - what to call to change position.
class NextButton extends React.Component {
    render() {
        if(this.props.curr === this.props.length - 1) {
            return (
                <li className="next submit">
                  <a onClick={this.props.submit}>
                    Submit
                  </a>
                </li>
            );
        }
        return (
            <li className="next">
              <a onClick={()=>this.props.increment(1)}>
                Continue <span aria-hidden="true">&rarr;</span>
              </a>
            </li>
        );
    }
}

// args: increment - change position, curr - current position.
class PrevButton extends React.Component {
    render() {
        const disable = this.props.curr === 0 ? " disabled" : "";
        return (
            <li className={"previous" + disable}>
              <a onClick={()=>this.props.increment(-1)}>
                <span aria-hidden="true">&larr;</span> Go Back
              </a>
            </li>
        );
    }
}

class UploadForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            curr: 0,
            formData: {
                videoFile: null,
	        thumbnailFile: null,
		title: '',
		description: '',
		version: '',
		license: '',
		tags: new Set(),
		userid: -1 // TODO: mediabox user id.
            }
        };

        this.increment = this.increment.bind(this);
	this.handleChange = this.handleChange.bind(this);
	this.fileChange = this.fileChange.bind(this);
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

    //handles getting state data and giving it to the ajax submit.
    handleSubmit(e){
	e.preventDefault();

        // TODO: loop through formData
        // https://stackoverflow.com/questions/21329426/spring-mvc-multipart-request-with-json
        const serverForm = new FormData();
        serverForm.append('videoForm', new Blob([JSON.stringify({
            title: this.state.formData.title,
            description: this.state.formData.description,
            version: this.state.formData.version,
            license: this.state.formData.license,
            tags: Array.from(this.state.formData.tags),
            userid: this.state.formData.userid
        })], {
            type: "application/json"
        }));
        serverForm.append('thumbnail', this.state.formData.thumbnailFile);
        console.log(serverForm);
	// insert video
	$.ajax({
	    type: "POST",
	    url: config.serverUrl + "/upload/add-video",
	    processData: false,
            contentType: false,
            headers: {
                "Content-Type": undefined
            },
	    data: serverForm,
	    success: (response) => {
                // insert 
		console.log(response);
                        
	        const localForm = new FormData();
                localForm.append('id', response);
	        localForm.append('video', this.state.formData.videoFile);
	        // Start upload process on local mediabox server.
	        $.post({
	            url: '/process/upload',
	            data: localForm,
	            processData: false,  // tell jQuery not to process the data (because of the file)
	            contentType: false,  // tell jQuery not to set contentType
	            success: (torrentFile) => {
	                alert("Successfully Uploaded!");
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
