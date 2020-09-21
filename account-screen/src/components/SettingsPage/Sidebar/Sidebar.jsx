import React from 'react';
import Selector from './Selector'

function Sidebar(props) {
    return (
        <React.Fragment>
            <div className="col-2 border-right">
                <h1>{props.title}</h1>
                <hr />
                <ul className="list-group">
                    {props.tabs.map((tab, index) => <Selector active={tab.active} tab={tab} key={index} className="list-group-item border-0 selectable rounded w-100" selectTab={() => props.selectTab(index)} />)}
                </ul>
            </div>
        </React.Fragment>
    );
}

export default Sidebar;
