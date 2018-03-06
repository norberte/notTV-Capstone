const React = require("react");
const ReactDOM = require("react-dom");
import CategoryPanel from './CategoryPanel.js';

class AdminPage extends React.Component {
    render() {
	return (
	    <div className="panel-group">
	      {this.props.panels.map((panel, idx) => <PanelWrapper panel={panel} key={idx}/>)}
	    </div>
	);
    }
}

class PanelWrapper extends React.Component {
    render() {
	return (
	    <div className="panel panel-default">
	      <div className="panel panel-heading">
		{this.props.panel.heading}
	      </div>
	      <div className="panel-body">
		{this.props.panel.body}
	      </div>
	    </div>
	);
    }
}

const PANELS = [
    {
	heading: "Edit Categories:",
	body: <CategoryPanel/>
    }
];

ReactDOM.render(
    <AdminPage panels={PANELS}/>,
    document.getElementById('root')
);
