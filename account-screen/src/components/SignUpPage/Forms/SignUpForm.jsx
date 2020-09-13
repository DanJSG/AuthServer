import React from 'react';
import { Link } from 'react-router-dom';

function SignUpForm(props) {

    return (
        <div className="row align-items-center justify-content-center h-100" style={{ backgroundColor: "#f3f5f744" }}>
            <div className="col-3 border shadow-sm rounded px-5 py-3 bg-white">
                <div>
                    <h1 className="mb-4">Sign Up</h1>
                    <form onSubmit={props.handleRegister}>
                        <div className="form-group">
                            <input className="form-control input-hover" type="text" name="username" placeholder="Username"></input>
                        </div>
                        <div className="form-group">
                            <input className="form-control input-hover" type="email" name="email" placeholder="Email address"></input>
                        </div>
                        <div className="form-group">
                            <input className="form-control input-hover" type="password" name="password" placeholder="Password"></input>
                        </div>
                        <div className="form-group">
                            <input className="form-control input-hover" type="password" name="repeatPassword" placeholder="Repeat Password"></input>
                            {props.formError && <label className="form-text">{props.formError}</label>}
                        </div>
                        <div className="form-group">
                            <button id="signInButton" className="btn btn-primary">Sign Up</button>
                        </div>
                        <div className="form-group">
                            <label className="form-text">
                                Already have an account? <Link className="link text-decoration-none" to="/oauth2/authorize">Sign in here.</Link>
                            </label>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default SignUpForm;
