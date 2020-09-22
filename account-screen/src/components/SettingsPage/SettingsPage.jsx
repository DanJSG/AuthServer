import React, { useState, useEffect } from 'react';
import DeveloperTab from './Tabs/DeveloperTab';
import GeneralTab from './Tabs/GeneralTab';
import Sidebar from './Sidebar/Sidebar';
import { authorize, refreshAccessToken } from './services/auth'
import AppRegistrationModal from './Modals/AppRegistrationModal';
import ConfirmationModal from './Modals/ConfirmationModal';
import { deleteApp, getApps } from './services/appregistration';

function SettingsPage() {

    const [applications, setApplications] = useState(null);
    const [authorized, setAuthorized] = useState(false);
    const [authChecked, setAuthChecked] = useState(false);
    const [editAppModalVisible, setEditAppModalVisible] = useState(false);
    const [confirmationModalVisible, setconfirmationModalVisible] = useState(false);
    const [currentTab, setCurrentTab] = useState(0);
    const [currentAppIndex, setCurrentAppIndex] = useState(null);
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

    const showEditAppModal = (index) => {
        setEditAppModalVisible(true);
        setCurrentAppIndex(index);
    }

    const hideEditAppModal = () => {
        setEditAppModalVisible(false);
        setCurrentAppIndex(null);
    }

    const showConfirmationModal = (index) => {
        setconfirmationModalVisible(true);
        setCurrentAppIndex(index);
    }
    const hideConfirmationModal = () => {
        setconfirmationModalVisible(false);
        setCurrentAppIndex(null);
    };

    const confirmAppDeletion = async () => {
        const { clientId, name, redirectUri } = applications[currentAppIndex];
        let deleted = await deleteApp(clientId, name, redirectUri, localStorage.getItem("acc.tok"));
        if (!deleted) {
            await refreshAccessToken();
            await deleteApp(clientId, name, redirectUri, localStorage.getItem("acc.tok"));
        }
        let apps = await getApps(localStorage.getItem("acc.tok"));
        setApplications(apps);
        hideConfirmationModal();
    }

    useEffect(() => {
        async function checkAuth() {
            let fetchedUser = await authorize();
            if (fetchedUser === null) {
                await refreshAccessToken();
                fetchedUser = await authorize();
                if (fetchedUser === null) {
                    setAuthChecked(true);
                    return;
                }
            }
            setAuthorized(true);
            setAuthChecked(true);
        }
        async function fetchApps() {
            let apps = await getApps(localStorage.getItem("acc.tok"));
            if (apps === null) {
                await refreshAccessToken();
                apps = await getApps(localStorage.getItem("acc.tok"));
            }
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
                return <DeveloperTab applications={applications} updateApplications={setApplications} edit={showEditAppModal} delete={showConfirmationModal}></DeveloperTab>
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
            <AppRegistrationModal setApplications={setApplications} applications={applications} currentAppIndex={currentAppIndex} title={"Edit Application"} close={hideEditAppModal} visible={editAppModalVisible}></AppRegistrationModal>
            <ConfirmationModal title={"Are you sure you want to remove this application?"} close={hideConfirmationModal} ok={confirmAppDeletion} visible={confirmationModalVisible}></ConfirmationModal>
        </div >
    );
}

export default SettingsPage;
