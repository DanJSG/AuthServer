import React, { useState, useEffect } from 'react';
import DeveloperTab from './Tabs/DeveloperTab';
import GeneralTab from './Tabs/GeneralTab';
import Sidebar from './Sidebar/Sidebar';
import { authorize } from './services/auth'

function SettingsPage() {

    const [authorized, setAuthorized] = useState(false);
    const [authChecked, setAuthChecked] = useState(false);
    const [user, setUser] = useState(null);
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

    useEffect(() => {
        async function checkAuth() {
            const user = await authorize();
            if (user === null) {
                setAuthChecked(true);
                return;
            }
            setAuthorized(true);
            setUser(user);
            setAuthChecked(true);
        }
        if (!authChecked)
            checkAuth();
    })

    const selectTab = (tab) => {
        setTabs(prevTabs => {
            prevTabs.forEach(prevTab => {
                if (prevTab === tab)
                    prevTab.active = true;
                else
                    prevTab.active = false;
            });
            return prevTabs;
        })
        setCurrentTab(tab.rendering);
    }

    return (
        <div className="container-fluid h-100">
            {
                authorized ?
                    <div className="row h-100 p-3">
                        <Sidebar tabs={tabs} selectTab={selectTab} title="Settings"></Sidebar>
                        {currentTab}
                    </div>
                    :
                    authChecked ? window.location.href = "http://local.courier.net:3010/oauth2/authorize" : <p>Checking priveleges, please wait...</p>
            }
        </div >
    );
}

export default SettingsPage;