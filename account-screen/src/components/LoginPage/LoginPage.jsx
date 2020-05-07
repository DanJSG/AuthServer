import React, {useState} from 'react';
import {Link, useLocation} from 'react-router-dom'
import {generateCodeChallenge, generateState} from '../../services/codeprovider'
import {checkLoginForm, sendLoginRequest} from './services/loginservice';
import LoginForm from './LoginForm/LoginForm';

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
                <LoginForm handleLogin={handleLogin} formError={formError} />
                :
                redirectToCallback()
            }
        </div>
    );
}

export default LoginPage;
