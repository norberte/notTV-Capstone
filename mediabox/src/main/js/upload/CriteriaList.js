const React = require("react");

class Criteria extends React.Component {
    render() {
	return <li>{this.props.text}</li>;
    }
}

export default class CriteriaList extends React.Component {
    constructor(props) {
	super(props);

	// TODO: get this from the db
	this.state = {
	    criteria: []
	};

        $.get({
            url: config.serverUrl + "/criteria",
            dataType: "json",
            succuss: (data) => {
                this.setState({
                    criteria: data
                });
            }
        });
    }
    
    render(){
	return(
	    <div id="subCrit">
	      <h1>Video Submission Criteria</h1>
	      <ol>
		{this.state.criteria.map((c, idx)=><Criteria key={idx} text={c}/>)}
	      </ol>
	    </div>
	);
    }
}
