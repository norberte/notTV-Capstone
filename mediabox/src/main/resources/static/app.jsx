		class ListItem extends React.Component {
          //I was thinking I would get an id for a video and launch the web player and give the web player the id to play the video
          //var videoId = Do something to have an ID for a video. Probably Database ID.
            render() {
                return <li className="App-video"><a href="webPlayer.html?link=video1.mp4">{this.props.value}</a></li>
            }
        }

        class FilterList extends React.Component {
            constructor(props) {
                super(props);
                this.state = {
                    filteredList: this.props.list
                };

                this.textChange = this.textChange.bind(this);
            }

            textChange(e) {
                this.setState({
                    filteredList: this.props.list.filter((item) => item.startsWith(e.target.value))
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
                    to download a video file, place the video file somewhere, and then launch theadweb webPlayer
                    with parameter ?link=video*/}
                    <label>
                      Enter a Magnet Link Here:
                    	  <input type = "text" name="magnetLink" />
                    </label>
                      <br/>
                      <input id="submitButton" type = "submit" value = "submit" />
                    </form>

                        <ul>
                        {
                            this.state.filteredList.map((item, key) => {
                                return (
                                    <ListItem key={key} value={item}/>
                                );
                            })
                        }
                        </ul>
                    </div>

                  </div>

                );
            }
        }

        const List = [
            //"Test Video!", "a", "24y29hu", "dfshskd"
        ];

        ReactDOM.render(
            <FilterList list={List}/>,
            document.getElementById('content')
        );
