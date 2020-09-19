import React, { useState, useEffect } from 'react';
import DeveloperTab from './Tabs/DeveloperTab';
import GeneralTab from './Tabs/GeneralTab';
import Sidebar from './Sidebar/Sidebar';
import { authorize } from './services/auth'
import AppRegistrationModal from './Modals/AppRegistrationModal';
import { getApps } from './services/appregistration';

function SettingsPage() {

    const toggleShowEditAppModal = () => {
        setEditAppModalVisible(prevVisibility => !prevVisibility);
    }

    const [applications, setApplications] = useState(null);
    const [authorized, setAuthorized] = useState(false);
    const [authChecked, setAuthChecked] = useState(false);
    const [editAppModalVisible, setEditAppModalVisible] = useState(false);
    const [currentTab, setCurrentTab] = useState(0);
    const [tabs, setTabs] = useState([
        {
            name: "General",
            icon: <i className="fa fa-cog"></i>,
            active: true
        },
        {
            name: "Developer",
            icon: <i className="fa fa-code"></i>,
            active: false
        },
    ])

    useEffect(() => {
        async function checkAuth() {
            const fetchedUser = await authorize();
            if (fetchedUser === null) {
                setAuthChecked(true);
                return;
            }
            setAuthorized(true);
            setAuthChecked(true);
        }
        async function fetchApps() {
            const apps = await getApps(localStorage.getItem("acc.tok"));
            setApplications(apps);
        }
        if (!authChecked)
            checkAuth();
        if (applications == null && authChecked)
            fetchApps();
    })

    const selectTab = (tabIndex) => {
        let i = 0;
        setTabs(prevTabs => {
            for (i; i < prevTabs.length; i++)
                prevTabs[i].active = i === tabIndex ? true : false;
            return prevTabs;
        })
        setCurrentTab(tabIndex);
    }

    const renderTab = (index) => {
        switch (index) {
            case 0:
                return <GeneralTab></GeneralTab>
            case 1:
                return <DeveloperTab applications={applications} edit={toggleShowEditAppModal}></DeveloperTab>
        }
    }

    return (
        <div className="container-fluid h-100">
            {
                authorized ?
                    <div className="row h-100 p-3">
                        <Sidebar tabs={tabs} selectTab={selectTab} title="Settings"></Sidebar>
                        {renderTab(currentTab)}
                    </div>
                    :
                    authChecked ? window.location.href = "http://local.courier.net:3010/oauth2/authorize" : <p>Checking priveleges, please wait...</p>
            }
            <AppRegistrationModal title={"Edit Application"} close={toggleShowEditAppModal} visible={editAppModalVisible}></AppRegistrationModal>
        </div >
    );
}

export default SettingsPage;
