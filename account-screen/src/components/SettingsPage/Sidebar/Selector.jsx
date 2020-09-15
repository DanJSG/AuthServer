import React from 'react';

function Selector(props) {

    const classNames = props.className + (props.active ? " active" : "");

    const tabClicked = (e) => {
        props.selectTab(props.tab);
    }

    return <li className={classNames} key={props.tab.name} onClick={tabClicked}>{props.tab.icon} {props.tab.name}</li>
}

export default Selector;
