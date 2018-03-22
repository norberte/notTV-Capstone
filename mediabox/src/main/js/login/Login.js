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
        const loginForm = new FormData();
        serverForm.append(JSON.stringify({username: this.state.username, pass: this.state.pass}));
        console.log(loginForm);
        //Begin post
        $.post({
            url: '/process/loginProcess',
            data: loginForm,
            success: (passMatchTrue) => {
                alert("Successfully Logged In!");
            },
            error: (response) => {
                alert("Login Error");
                console.log(response);
            }
        });
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
                        <input type = "text" name = "pass" id = "pass" value = {this.state.pass} onChange = {this.handleChangePass} />
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
