import React from 'react';

function DeveloperTab(props) {

    const registerApp = (e) => {
        e.preventDefault();
    }

    return (
        <div className="ml-3 w-100">
            <h1>Developer Settings</h1>
            <p className="text-danger"><i><b>None of this has yet been implemented in the backend and so is not currently functional.</b></i></p>
            <hr />
            <div>
                <h3 className="mb-3">Register Application</h3>
                <p><i>Register an application to use Authentity as an OAuth2 authorization provider.</i></p>
                <form onSubmit={registerApp}>
                    <div className="form-group">
                        <input type="text" placeholder="Application Name" className="form-control input-hover w-25" />
                    </div>
                    <div className="form-group">
                        <input type="text" placeholder="Redirect URI" className="form-control input-hover w-25" />
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
    );

}

export default DeveloperTab;
