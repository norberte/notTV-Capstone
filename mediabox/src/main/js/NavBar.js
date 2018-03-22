const React = require("react");
const ReactDOM = require("react-dom");

export default class NavBar extends React.Component {
    render(){
        return(
            <div id="navbar">
              <nav>
                <a href="/browse"><img className="notTVLogo" src="/img/notTV_logo_white.png" alt="notTV Logo"/></a>
                <ul className="nav-ul">
                  <li><a href="/browse">Browse</a></li>
                  <li><a href="/upload">Upload</a></li>
                  <li><a href="/account">Account</a></li>
		</ul>
	      </nav>
	    </div>
	);
    }
}

ReactDOM.render(<NavBar />, document.getElementById('rootNav'));
