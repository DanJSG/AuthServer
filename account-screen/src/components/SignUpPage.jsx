import React, {useState} from 'react';
import {Link} from 'react-router-dom';

function SignUpPage() {

    const [formError, setFormError] = useState(null);

    const sendRegistrationRequest = (email, username, password) => {
        const url = `http://local.courier.net:8080/api/v1/register`;
        const details = JSON.stringify({
            email: email,
            password: password,
            username: username
        });
        fetch(url, {
            method: "POST",
            body: details,
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then((response) => {
            if(response.status !== 200) {
                setFormError("Failed to create account.");
                return;
            }
            console.log(response);
            return;
        })
        .catch((error) => {
            console.log(`Fetch error: ${error}`);
            setFormError("An error occurred when contacting the server.");
        })
    }

    const handleRegister = (e) => {
        e.preventDefault();
        setFormError(null);
        const email = e.target.elements.email.value.trim();
        const username = e.target.elements.username.value.trim();
        const password = e.target.elements.password.value.trim();
        const repeatPassword = e.target.elements.repeatPassword.value.trim();
        if(checkForm(email, username, password, repeatPassword)) {
            sendRegistrationRequest(email, username, password);
        }
    }

    const checkForm = (email, username, password, repeatPassword) => {
        if(email === undefined || email === null || email === "") {
            setFormError("Please enter an email address.");
            return false;
        }
        if(username === undefined || username === null || username === "") {
            setFormError("Please enter a username.");
            return false;
        }
        if(password === undefined || password === null || password === "") {
            setFormError("Please enter a password.");
            return false;
        }
        if(password !== repeatPassword) {
            setFormError("The two passwords do not match.");
            return false;
        }
        return true;
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
