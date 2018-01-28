import NavBar from '../NavBar.js';

const React = require("react");
const ReactDOM = require("react-dom");

class Account extends React.Component {
    render() {
    return (
        <div>
            <h1>Test</h1>
            <h2>Test</h2>
            <h3>Test</h3>
            <h4>Test</h4>
            <h5>Test</h5>
            <p>Test</p>
        </div>
    );
    }
}

ReactDOM.render(<Account />, document.getElementById('root'));
