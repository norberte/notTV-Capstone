class selectVideo extends React.Component {
    render() {
        return (
          <div className="tab">Select Video File
            <p><input type="file" name="videoFile" accept="video/*"/></p>
          </div>
        );
    }
}

class videoPoster extends React.Component {
    render() {
        return (
          <div className="tab">Video Poster
            <p><input type = "file" name="videoPoster" accept="image/*"/></p>
          </div>
        );
    }
}

class videoDetails extends React.Component {
    render() {
        return (
          <div className="tab">Video Details
            <p>Video Title<input type = "text" name="videoTitle"/></p>
            <p>Genre/Category
            <select name = "videoCategory">
              <option value="Action">Action</option>
              <option value="Adventure">Adventure</option>
              <option value="Comedy">Comedy</option>
              <option value="Some Junk">Some Junk</option>
            </select>
            </p>
          </div>
        );
    }
}

class tabForm extends React.Component {

    render() {
        return (
          <form id="uploadVideo" action="">

          <Tabs selected={0}>
            <Pane label="Tab 1">
              <div>Tab 1 contents</div>
            </Pane>
            <Pane label="Tab 2">
              <div>Tab 2 contents</div>
            </Pane>
            <Pane label="Tab 3">
              <div>Tab 3 contents</div>
            </Pane>
          </Tabs>

          </form>
        );
    }
}

var FORM_PARTS = [
<selectVideo/>,
<videoPoster/>,
<videoDetails/>
];

ReactDOM.render(
    <tabForm />,
    document.getElementById('root')
);
