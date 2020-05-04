import React, {useState} from 'react';
import {Link} from 'react-router-dom';

function SignUpPage() {

    const [formError, setFormError] = useState(null);

    const handleRegister = (e) => {
        e.preventDefault();
    }

    return(
        <div className="container-fluid inherit-height">
            <div className="row align-items-center justify-content-center inherit-height">
                <div className="col-3">
                    <div>
                        <h1 className="mb-4">Sign Up</h1>
                        <form onSubmit={handleRegister}>
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
                                {formError && <label className="form-text">{formError}</label>}
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
        </div>
    );

}

export default SignUpPage;