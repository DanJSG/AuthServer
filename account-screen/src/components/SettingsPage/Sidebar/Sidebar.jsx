import React from 'react';
import Selector from './Selector'

function Sidebar(props) {
    return (
        <React.Fragment>
            <div className="col-2 border-right">
                <h1>{props.title}</h1>
                <hr />
                <ul className="list-group">
                    {props.tabs.map(tab => <Selector active={tab.active} tab={tab} key={tab.name} className="list-group-item border-0 selectable rounded w-100" selectTab={props.selectTab} />)}
                </ul>
            </div>
        </React.Fragment>
    );
}

export default Sidebar;
