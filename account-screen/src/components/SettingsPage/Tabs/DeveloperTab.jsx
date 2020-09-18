import React from 'react';
import { registerApp } from '../services/appregistration';

function DeveloperTab(props) {

    const registerAppSubmitted = (e) => {
        e.preventDefault();
        const name = e.target.elements.appName.value;
        const redirectUri = e.target.elements.appRedirectUri.value;
        registerApp(name, redirectUri, localStorage.getItem("acc.tok"));
    }

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
                        props.applications === undefined || props.applications === null ?
                            <p>You have not currently registered any applications.</p>
                            :
                            <ul className="list-group">
                                {props.applications.map(app => <div key={app.name} className="list-group-item border-0">{app.name}</div>)}
                            </ul>
                    }
                </div>
                <hr />
            </div>
        </div>
    );

}

export default DeveloperTab;
