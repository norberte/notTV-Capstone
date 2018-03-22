/*
This file should have state data on the currently logged in user
*/
const React = require("react");

export default class CurrentUser extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 0,
            username: "Not Logged In"
            //thumbnail: ""
        };
        this.setId = this.setId.bind(this);
        this.setUsername = this.setUsername.bind(this);
        this.getId = this.getId.bind(this);
        this.getUsername = this.getUsername.bind(this);
    }

    setId(id){
        this.state.id = id;
    }

    setUsername(username){
        this.state.username = username;
    }

    getId(){
        return this.state.id;
    }

    getUsername(){
        return this.state.username;
    }

}
