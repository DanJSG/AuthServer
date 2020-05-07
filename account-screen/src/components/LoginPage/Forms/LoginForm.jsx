import React from 'react';
import {Link} from 'react-router-dom';

function LoginForm(props) {
    return (
        <div className="row align-items-center justify-content-center inherit-height">
            <div className="col-3">
                <div>
                    <h1 className="mb-4">Sign In</h1>
                    <form onSubmit={props.handleLogin}>
                        <div className="form-group">
                            <input className="form-control input-hover" type="email" name="email" placeholder="Email address"></input>
                        </div>
                        <div className="form-group">
                            <input className="form-control input-hover" type="password" name="password" placeholder="Password"></input>
                            {props.formError && <label className="form-text">{props.formError}</label>}
                        </div>
                        <div className="form-group">
                            <button id="signInButton" className="btn btn-primary">Sign In</button>
                        </div>
                        <div className="form-group">
                        <label className="form-text">
                            Not got an account yet? <Link className="link text-decoration-none" to="/oauth2/register">Sign up here.</Link>
                        </label>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default LoginForm;
