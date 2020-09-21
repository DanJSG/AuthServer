import React, { useEffect, useState } from 'react';
import { getApps, registerApp } from '../services/appregistration';

function DeveloperTab(props) {

    const registerAppSubmitted = async (e) => {
        e.preventDefault();
        const name = e.target.elements.appName.value;
        const redirectUri = e.target.elements.appRedirectUri.value;
        e.target.elements.appName.value = null;
        e.target.elements.appRedirectUri.value = null;
        await registerApp(name, redirectUri, localStorage.getItem("acc.tok"));
        const apps = await getApps(localStorage.getItem("acc.tok"));
        props.updateApplications(apps);
    }

    return (
        <div className="col-10 w-100">
            <div className="ml-3 w-100">
                <h1>Developer Settings</h1>
                <hr />
                <div>
                    <h3 className="mb-3">Register Application</h3>
                    <p><i>Register an application to use Authentity as an OAuth2 authorization provider.</i></p>
                    <form onSubmit={registerAppSubmitted}>
                        <div className="form-group">
                            <input name="appName" type="text" placeholder="Application Name" className="form-control input-hover w-25" />
                        </div>
                        <div className="form-group">
                            <input name="appRedirectUri" type="text" placeholder="Redirect URI" className="form-control input-hover w-25" />
                        </div>
                        <div className="form-group">
                            <button type="submit" className="btn btn-primary">Register</button>
                        </div>
                    </form>
                </div>
                <hr />
                <div>
                    <h3 className="mb-3">Existing Applications</h3>
                    {
                        props.applications === undefined || props.applications === null ?
                            <p>You have not currently registered any props.applications.</p>
                            :
                            <ul className="list-group">
                                {props.applications.map((app, index) =>
                                    <div key={index} className="list-group-item border-0 selectable rounded">
                                        <div className="d-flex justify-content-between">
                                            <h5>{app.name}</h5>
                                            <button onClick={() => props.edit(index)} className="btn btn-secondary my-1" style={{ width: "6%" }}>Edit</button>
                                        </div>
                                        <div className="d-flex justify-content-between">
                                            <p>Client ID: <b>{app.clientId}</b></p>
                                            <button onClick={() => props.delete(index)} className="btn btn-danger my-1" style={{ width: "6%" }}>Delete</button>
                                        </div>
                                        <p>Callback URL: <a href={app.redirectUri}>{app.redirectUri}</a></p>
                                    </div>
                                )}
                            </ul>
                    }
                </div>
                <hr />
            </div>
        </div>
    );

}

export default DeveloperTab;
