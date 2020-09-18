import React, { useEffect, useState } from 'react';
import { getApps, registerApp } from '../services/appregistration';

function DeveloperTab(props) {

    const [applications, setApplications] = useState(null);

    const registerAppSubmitted = (e) => {
        e.preventDefault();
        const name = e.target.elements.appName.value;
        const redirectUri = e.target.elements.appRedirectUri.value;
        registerApp(name, redirectUri, localStorage.getItem("acc.tok"));
        e.target.elements.appName.value = null;
        e.target.elements.appRedirectUri.value = null;
    }

    useEffect(() => {
        async function fetchApps() {
            const apps = await getApps(localStorage.getItem("acc.tok"));
            setApplications(apps);
        }
        fetchApps();
    })

    return (
        <div className="col-10 w-100">
            <div className="ml-3 w-100">
                <h1>Developer Settings</h1>
                <p className="text-danger"><i><b>None of this has yet been implemented in the backend and so is not currently functional.</b></i></p>
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
                        applications === undefined || applications === null ?
                            <p>You have not currently registered any applications.</p>
                            :
                            <ul className="list-group">
                                {applications.map(app =>
                                    <div key={app.name} className="list-group-item border-0 selectable rounded">
                                        <div className="d-flex justify-content-between">
                                            <h5>{app.name}</h5>
                                            <button className="btn btn-secondary px-4">Edit</button>
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
