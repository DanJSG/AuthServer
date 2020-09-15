import React, { useState } from 'react';
import DeveloperTab from './Tabs/DeveloperTab';
import GeneralTab from './Tabs/GeneralTab';
import Selector from './Sidebar/Selector';
import Sidebar from './Sidebar/Sidebar';

function SettingsPage() {

    const [currentTab, setCurrentTab] = useState(<GeneralTab></GeneralTab>);
    const [tabs, setTabs] = useState([
        {
            name: "General",
            icon: <i className="fa fa-cog"></i>,
            rendering: <GeneralTab></GeneralTab>,
            active: true
        },
        {
            name: "Developer",
            icon: <i className="fa fa-code"></i>,
            rendering: <DeveloperTab></DeveloperTab>,
            active: false
        },
    ])

    const selectTab = (tab) => {
        setTabs(prevTabs => {
            prevTabs.forEach(prevTab => {
                if (prevTab === tab) {
                    prevTab.active = true;
                } else {
                    prevTab.active = false;
                }
            });
            return prevTabs;
        })
        setCurrentTab(tab.rendering);
    }

    return (
        <div className="container-fluid h-100">
            <div className="row h-100 p-3">
                <div className="col-2 border-right">
                    <Sidebar tabs={tabs} selectTab={selectTab} title="Settings"></Sidebar>
                </div>
                <div className="col-10 w-100">
                    {currentTab}
                </div>
            </div>
        </div >
    );
}

export default SettingsPage;