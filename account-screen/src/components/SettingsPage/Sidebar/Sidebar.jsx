import React from 'react';
import Selector from './Selector'

function Sidebar(props) {
    return (
        <React.Fragment>
            <h1>{props.title}</h1>
            <hr />
            <ul className="list-group">
                {props.tabs.map(tab => <Selector active={tab.active} tab={tab} className="list-group-item border-0 selectable rounded w-100" selectTab={props.selectTab} />)}
            </ul>
        </React.Fragment>
    );
}

export default Sidebar;