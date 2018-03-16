import NavBar from '../NavBar.js';

const React = require("react");
const ReactDOM = require("react-dom");

class Login extends React.Component {
    constructor(props){
        super(props)
        this.state = {
            username: '',
            pass: ''
        };
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event){
        tgis.setState({pass: event.target.value})
    }

    handleSubmit(event) {
        if (this.state.pass != '') {
            alert('Username: '+username+' was submitted with a password'); //For initial testing
        } else {
            alert('Username: '+username+' was submitted without a password'); //For initial testing
        }
        event.preventDefault();
    }

    render() {
        return (
            <div>
                <h1>Login to notTv/h1>
                <form onSubmit={this.handleSubmit}>
                    <label>
                        Username:
                        <input type = "text" value = {this.state.username} onChange = {this.handleChange} />
                    </label>
                    <label>
                        Password:
                        <input type = "text" value = {this.state.pass} onChange = {this.handleChange} />
                    </label>
                    <input type = "submit" value = "Submit" />
                </form>
            </div>
        );
    }
}

ReactDOM.render(
    <Login />,
    document.getElementById('root')
);
