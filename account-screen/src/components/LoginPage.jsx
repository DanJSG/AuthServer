import React, {useState} from 'react';
import {Link, useLocation} from 'react-router-dom'
// import '../stylesheets/LoginPage.css'

//http://localhost:3000/authorize?audience=courier&scope=name+email&response_type=code&client_id=h43dx4f&state=53243231&redirect_uri=https://local.courier.net/auth_callback&code_challenge=3yct34hroa8fh4n8chfn84hacoxe8wfco8he74ajdory3ow8coa8du8WYNCQO8YWO8qonthc34oon8

function LoginPage() {

    const location = useLocation();
    const [authCode, setAuthCode] = useState(null);
    const [formError, setFormError] = useState(null);
    const [params] = useState(() => {
        let queryArray = location.search.substring(1).split("&")
        const newParams = {}
        queryArray.forEach((term) => {
            const pair = term.split("=");
            newParams[pair[0]] = pair[1].replace(/[+]/g, " ");
        })
        return newParams;
    });

    const checkForm = (email, password) => {
        if(email === undefined || email === null || email === "") {
            setFormError("Please enter an email address.");
            return false;
        }
        if(password === undefined || password === null || password === "") {
            setFormError("Please enter a password.");
            return false;
        }
        return true;
    }

    const sendLoginRequest = (email, password) => {
        const url = `http://local.courier.net:8080/api/auth/authorize?code_challenge=${params.code_challenge}&response_type=${params.response_type}&client_id=${params.client_id}&redirect_uri=${params.redirect_uri}`;
        const credentials = JSON.stringify({credentials: btoa(JSON.stringify({email: email, password: password}))})
        fetch(url, {
            method: "POST",
            body: credentials,
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then((response) => {
            if(response.status !== 200) {
                console.log(`Request failed. Returned status code ${response.status}. Response object logged below.`);
                console.log(response);
                setFormError("The email address and password combination you have entered is incorrect.");
                return;
            }
            return response.json();
        })
        .then((json) => {
            if(!json) {
                return;
            }
            console.log("Auth code is: " + json.code);
            setAuthCode(json.code);
        })
        .catch((error) => {
            console.log(`Fetch error: ${error}`);
        })
    }

    const handleLogin = (e) => {
        e.preventDefault();
        setFormError(null);
        const email = e.target.elements.email.value.trim();
        const password = e.target.elements.password.value;
        if(checkForm(email, password)) {
            sendLoginRequest(email, password);
        }
    }

    const redirectToCallback = () => {
        window.location.href = `${params.redirect_uri}?code=${authCode}&state=${params.state}`;
    }

    return(
        <div className="container-fluid inherit-height">
            <div className="row align-items-center justify-content-center inherit-height">
                <div className="col-3">
                    {authCode === null ? 
                        <div>
                            <h1 className="mb-4">Sign In</h1>
                            <form onSubmit={handleLogin}>
                                <div className="form-group">
                                    <input className="form-control input-hover" type="email" name="email" placeholder="Email address"></input>
                                </div>
                                <div className="form-group">
                                    <input className="form-control input-hover" type="password" name="password" placeholder="Password"></input>
                                    {formError && <label className="form-text">{formError}</label>}
                                </div>
                                <div className="form-group">
                                    <button id="signInButton" className="btn btn-primary">Sign In</button>
                                </div>
                                <div className="form-group">
                                <label className="form-text">
                                    Not got an account yet? <Link className="link text-decoration-none" to="/sign-up">Sign up here.</Link>
                                </label>
                                </div>
                            </form>
                        </div>
                        :
                        redirectToCallback()
                    }
                </div>
            </div>
        </div>
    );
}

export default LoginPage;