import React from 'react';

function GeneralTab(props) {

    const changePassword = (e) => {
        e.preventDefault();
    }

    const changeUsername = (e) => {
        e.preventDefault();
    }

    return (
        <div className="ml-3 w-100">
            <h1>Account Settings</h1>
            <p className="text-danger"><i><b>None of this has yet been implemented in the backend and so is not currently functional.</b></i></p>
            <hr />
            <div>
                <h3 className="mb-3">Change Username</h3>
                <form onSubmit={changeUsername}>
                    <div className="form-group">
                        <input className="form-control input-hover w-25" type="text" name="username" placeholder="New username"></input>
                    </div>
                    <div className="form-group">
                        <button type="submit" className="btn btn-primary">Change</button>
                    </div>
                </form>
            </div>
            <hr />
            <div>
                <h3 className="mb-3">Change Password</h3>
                <form onSubmit={changePassword}>
                    <div className="form-group">
                        <input type="password" name="oldPassword" placeholder="Old password" className="form-control input-hover w-25" />
                    </div>
                    <div className="form-group">
                        <input type="password" name="newPassword" placeholder="New password" className="form-control input-hover w-25" />
                    </div>
                    <div className="form-group">
                        <input type="password" name="repeatPassword" placeholder="Repeat password" className="form-control input-hover w-25" />
                    </div>
                    <div className="form-group">
                        <button type="submit" className="btn btn-primary">Change</button>
                    </div>
                </form>
            </div>
            <hr />
        </div>
    )
}

export default GeneralTab;
