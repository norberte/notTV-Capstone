

class ListItem extends React.Component {
    //I was thinking I would get an id for a video and launch the web player and give the web player the id to play the video
    //var videoId = Do something to have an ID for a video. Probably Database ID.
    render() {
	let link = "/download?torrentName=" + this.props.value.name;
        return <li className="App-video"><a href={link}>{this.props.value.name}</a></li>;
    }
}

class FilterList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
	    fullList: this.props.list,
            filteredList: this.props.list
        };

        this.textChange = this.textChange.bind(this);
	$.ajax({
	    url: "http://nottv.levimiller.ca/list-torrents",
	    dataType: "json",
	    success: (r) => {
		if($.isArray(r)) {
		    console.log(r);
		    let list = r.map(i => ({
			name: i.split('/').pop(),
			link: i
		    }));
		    
		    this.setState({
			fullList: list,
			filteredList: list.filter((item) => item.name.startsWith(this.refs.filterInput.value))
		    });
		} else {
		    console.log("Invalid Json: ");
		    console.log(r);
		}
	    }
	});
    }

    textChange(e) {
        this.setState({
            filteredList: this.state.fullList.filter((item) => item.startsWith(e.target.value))
        });
    }

    render() {
        return (
            <div className="App">

              <header className="App-header">
                <img className="App-logo" src="notTV_logo_white.png" alt="notTV"/>
                <h1>Torrent Prototype</h1>
              </header>

              <div>
                <p> This is a test. Milestone 1 Peer-to-peer filesharing system using torrent protocol implemented in Java.</p>

                <form action = "/download" method = "post">
                  {/*My hope is that this will go to the back end, use the magnet link and button
                      to download a video file, place the video file somewhere, and then launch the web webPlayer
                  with parameter ?link=video*/}
                  <label>
                    Enter a .torrent File Here:
                    <input type = "text" ref="filterInput" onChange={this.textChange} name="torrentFile" />
                  </label>
                  <br/>
                  <input id="submitButton" type = "submit" value = "submit" />
                </form>
		<form id="selectTorrent" action="/download" method="post">
                  <ul>
                    {
                        this.state.filteredList.map((item, key) => {
                            return (
                                <ListItem key={key} value={item}/>
                            );
                        })
                    }
            </ul>
                <input id="torrentInput" type="hidden" name="torrentFile"/>
                </form>
                </div>

                </div>

        );
    }
}

//const List = ["Test Video!", "a", "24y29hu", "dfshskd" ];

ReactDOM.render(
    <FilterList list={List}/>,
    document.getElementById('content')
);
