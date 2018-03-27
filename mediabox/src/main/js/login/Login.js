import NavBar from '../NavBar.js';
import CurrentUser from '../CurrentUser.js'

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
        this.handleChangePass = this.handleChangePass.bind(this);
        this.handleChangeUsername = this.handleChangeUsername.bind(this);
    }

    handleChangePass(event){
        this.setState({pass: event.target.value})
    }

    handleChangeUsername(event){
        this.setState({username: event.target.value})
    }

    //Handles submission of login form with a Post
    //This should go to ProcessController.java and be handled by the fucntion loginProcess
    handleSubmit(event) {
        event.preventDefault();
        //Begin post
        $.post({
            url: config.secureServerUrl + '/upload/authenticateLogin',
            data: {
              username: this.state.username,
              pass: this.state.pass
            },
            success: (response) => {
              if (response != null) {
                setGlobalUserState(response);
                alert("Successfully Logged In!");
                //Redirect to Browse?
              } else {
                alert("Authentication Failure");
                console.log("Authentiction Failure");
              }
            },
            error: (response) => {
                alert("Authentiction Request Error");
                console.log(response);
            }
        });
    }

    //Handles setting the global state to the newly logged in user.
    setGlobalUserState(id){
        CurrentUser.state.id = id;
        CurrentUser.state.username = this.state.username;
    }

    render() {
        return (
            <div>
                <h1>Login to notTv</h1>
                <form onSubmit={this.handleSubmit}>
                    <label>
                        Username:
                        <input type = "text" name = "username" id = "username" value = {this.state.username} onChange = {this.handleChangeUsername} />
                    </label>
                    <br />
                    <label>
                        Password:
                        <input type = "password" name = "pass" id = "pass" value = {this.state.pass} onChange = {this.handleChangePass} />
                    </label>
                    <br />
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
