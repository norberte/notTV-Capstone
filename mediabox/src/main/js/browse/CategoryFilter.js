const React = require("react");

class CategoryEntry extends React.Component {
    render() {
        const active = this.props.selected ? "category-active" : "";
        return (
            <div className={"panel-body category-entry " + active} onClick={()=>this.props.onClick(this)}>
              <span className="category-id hidden">{this.props.entry.id}</span>
              <span>{this.props.entry.name}</span>
              <i className="glyphicon glyphicon-ok pull-right hidden"/>
            </div>
        );
    }
}

class CategoryType extends React.Component {
    render() {
        let entryClass = "panel-collapse collapse item-" + this.props.num;
        let href = "#accordion-1 .item-" + this.props.num;
        return (
            <div className="panel panel-default">
              <div className="panel-heading" role="tab">
                <h4 className="panel-title">
                  <span className="caret"></span>
                  <a role="button" data-toggle="collapse" data-parent="#accordion-1" aria-expanded="false" href={href} className="category-type-header">{this.props.name} </a>
                </h4>
              </div>
              <div className={entryClass} role="tabpanel">
                {
                    this.props.entries.map((entry, idx)=> {
                        return <CategoryEntry
                                      key={idx}
                                      entry={entry}
                                      onClick={this.props.handleCategorySelect}
                                      selected={this.props.selected.includes(entry.id)}/>;
                    })
                }
              </div>
            </div>
        );
    }
}


export default class CategoryFilter extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            selected: []
        };
        
        this.handleCategorySelect = this.handleCategorySelect.bind(this);
    }
    
    handleCategorySelect(categoryValue) {
        const id = categoryValue.props.entry.id;
        // if already selected
        const idx = this.state.selected.indexOf(id);
        const selected = this.state.selected;
        if(idx >=0 ) {
            selected.splice(idx, 1); // remove
        } else {
            selected.push(id);
        }
        this.setState({
            selected: selected
        });
        this.props.update_handler(selected);
    }
    
    render() {
        return (
            <div className="panel-group category-filter" role="tablist" aria-multiselectable="true" id="accordion-1">
              {
                  this.props.categories.map((cat, idx)=>{
                      return <CategoryType
                                    key={idx}
                                    num={idx + 1}
                                    name={cat.name}
                                    entries={cat.values}
                                    selected={this.state.selected}
                                    handleCategorySelect={this.handleCategorySelect}/>;
                  })
              }
            </div>
        );
    }
}
