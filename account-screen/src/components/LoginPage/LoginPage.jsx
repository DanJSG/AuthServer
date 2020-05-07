import React, {useState} from 'react';
import {Link, useLocation} from 'react-router-dom'
import {generateCodeChallenge, generateState} from '../codeGen'
import {checkLoginForm, sendLoginRequest} from './services/loginservice';

// local.courier.net:3000/oauth2/authorize?audience=courier&scope=name+email&response_type=code&client_id=ThpDT2t2EDlO&redirect_uri=http://local.courier.net:3000/oauth2/auth_callback

function LoginPage() {

    const location = useLocation();
    const [authCode, setAuthCode] = useState(null);
    const [formError, setFormError] = useState(null);
    const [state] = useState(generateState());
    const [codeChallenge] = useState(generateCodeChallenge());
    const [params] = useState(() => {
        let queryArray = location.search.substring(1).split("&");
        const newParams = {};
        queryArray.forEach((term) => {
            const pair = term.split("=");
            if(pair.length === 2) {
                newParams[pair[0]] = pair[1].replace(/[+]/g, " ");
            }
        })
        return newParams;
    });

    const handleLogin = async (e) => {
        e.preventDefault();
        setFormError(null);
        const email = e.target.elements.email.value.trim();
        const password = e.target.elements.password.value;
        const error = checkLoginForm(email, password);
        if(error !== null) {
            setFormError(error);
            return;
        }
        const response = await sendLoginRequest(email, password, params, codeChallenge, state);
        if(response.error !== null) {
            setFormError(response.error);
            return;
        }
        setAuthCode(response.code);
    }

    const redirectToCallback = () => {
        window.location.href = `${params.redirect_uri}?code=${authCode}` + 
                                `&state=${state}` + 
                                `&client_id=${params.client_id}` + 
                                `&redirect_uri=${params.redirect_uri}` + 
                                `&code_verifier=${codeChallenge.code_verifier}`;
    }

    return(
        <div className="container-fluid inherit-height">
            {authCode === null ? 
                <div className="row align-items-center justify-content-center inherit-height">
                    <div className="col-3">
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
                                    Not got an account yet? <Link className="link text-decoration-none" to="/oauth2/register">Sign up here.</Link>
                                </label>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                :
                redirectToCallback()
            }
        </div>
    );
}

export default LoginPage;