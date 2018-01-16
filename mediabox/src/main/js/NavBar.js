const React = require("react");
const ReactDOM = require("react-dom");

export default class NavBar extends React.Component {
render(){
  return(
  <div>
      <nav className="navbar navbar-default navigation-clean">
          <div className="container">
            <div className="navbar-header">
              <a className="navbar-brand navbar-link" href="http://localhost:8080/browse"><img className="notTVLogo" src="/img/notTV_logo_white.png" alt="notTV Logo"/></a>
            </div>
              <div className="collapse navbar-collapse" id="navcol-1">
                  <ul className="nav navbar-nav navbar-right">
                      <li className="active" role="presentation"><a href="http://localhost:8080/browse">Watch </a></li>
                      <li role="presentation"><a href="http://localhost:8080/upload">Submit Video</a></li>
                      <li className="dropdown"><a className="dropdown-toggle" data-toggle="dropdown" aria-expanded="false" href="#">Account </a></li>
                  </ul>
              </div>
          </div>
      </nav>
  </div>
  );
}
}

ReactDOM.render(<NavBar />, document.getElementById('rootNav'));
