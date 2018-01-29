const React = require("react");

export default class AuthorHeader extends React.Component {
	render() {
		return (
				<div className="media">
					<div className="media-left">
						<img src="./img/user.png"></img>
					</div>
					<div className="media-body">
						<h4 className="media-heading">{this.props.username}</h4>
						<p>{this.props.description}</p>
						<button className="btn btn-default" type="button">Subscribe</button>
						<button className="btn btn-default" type="button">Unsubscribe</button>
					</div>
				</div>
		);
	}
}
