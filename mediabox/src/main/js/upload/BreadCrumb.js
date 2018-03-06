const React = require("react");

export default class BreadCrumb extends React.Component {
    render() {
        return (
            <div className="bc">
              {
                  this.props.items.map((item, idx) => {
                      const child = "bc-child" + (idx <= this.props.curr ? " bc-active" : "");
                      return (
                          <a key={idx} className={child}>
                            {item}
                          </a>
                      );
                  })
              }
            </div>
        );
    }
}
