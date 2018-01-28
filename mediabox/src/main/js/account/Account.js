import NavBar from '../NavBar.js';

const React = require("react");
const ReactDOM = require("react-dom");

class Account extends React.Component {
    constructor(props){
    super(props);
    this.state = {
        formData: {
        currentUsername: '', //Alternatively, we could use the id of the user rather than the username
        newUsername: '',
        newEmail: '',
        autoDownload: '',
        newPass: '',
        confirmNewPass: ''
        }
    }
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    };

    //handles a change in an input from the form and gives that new change to the state.
    handleChange(e){
        const state = this.state;
        state.formData[e.target.name] = e.target.value;
        this.setState(state);
    }

    //handles getting state data and giving it to the ajax submit.
    handleSubmit(e){
	       e.preventDefault();

           const formData = {
               currentUsername: this.state.currentUsername,
   			   newUsername: this.state.newUsername,
               newEmail: this.state.newEmail
   		};

           // Use Ajax to send new account details
           $.ajax({
               type: "POST",
               url: config.serverUrl + "/account/accountSubmit",
               contentType: 'application/json',
               processData: false,
               data: JSON.stringify(formData),
               success: (response) => {
               console.log(response);
               alert("Successfully uploaded!");
               },
               error: (response) => {
               console.log(response);
               }
           });
    }


    render() {
    return (
        <div id = 'accountInfo'>
            <h1>Account Info</h1>

            <div id = 'accountDetails'>

                <figure>
                    <img src = "img/default-placeholder-300x300.png" alt = "Profile Picture"/>
                    <figcaption>Username_Placeholder</figcaption>
                </figure>

                <form id="accountInfoForm" method="post" onSubmit={this.handleSubmit} commandname="accountForm">
                    <input type = "text" name="newUsername" value={this.state.username}  onChange={this.handleChange} placeholder="Enter a New Username"/><br />
                    <input type = "text" name="newEmail" value={this.state.email}  onChange={this.handleChange} placeholder="Enter a New Email" disabled/><br />
                    <input type = "password" name="newPass" value={this.state.newPass}  onChange={this.handleChange} placeholder="Enter a New Password" disabled/><br />
                    <input type = "password" name="confirmNewPass" value={this.state.confirmNewPAss}  onChange={this.handleChange} placeholder="Confirm New Password" disabled/><br />
                    <h5>Settings and Options</h5>
                    <input type = "checkbox" name = "autoDownload" value="T" checked={this.state.formData.autoDownload === 'T'} onChange={this.handleChange} /> Auto Download Latest Videos
                    <input id="saveButton" type="submit" value="Save"/>
                </form>
            </div>

            <div id = "subscritpions">
            <h3>Subscriptions</h3>
            // TODO Make subscritpions float correctly below figure on the left
            </div>

        </div>
    );
    }
}

ReactDOM.render(<Account />, document.getElementById('root'));
