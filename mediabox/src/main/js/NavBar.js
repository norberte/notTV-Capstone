const React = require("react");
const ReactDOM = require("react-dom");

export default class NavBar extends React.Component {
    render(){
	return(
	    <div id="navbar">
	      <nav>
		<a href="http://localhost:8080/browse"><img className="notTVLogo" src="/img/notTV_logo_white.png" alt="notTV Logo"/></a>
		<ul>
                  <li><a href="http://localhost:8080/browse">Watch </a></li>
                  <li><a href="http://localhost:8080/upload">Submit Video</a></li>
                  <li><a href="#">Account </a></li>
		</ul>
	      </nav>
	    </div>
	);
    }
}

ReactDOM.render(<NavBar />, document.getElementById('rootNav'));
