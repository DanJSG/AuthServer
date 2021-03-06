import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';
import { checkLoginForm, sendLoginRequest } from './services/loginservice';
import LoginForm from './Forms/LoginForm';
import { getQueryStringAsJson } from '../../services/querystringmanipulator';

// local.courier.net:3000/oauth2/authorize?audience=courier&scope=name+email&response_type=code&client_id=ThpDT2t2EDlO&redirect_uri=http://local.courier.net:3000/oauth2/auth_callback

function LoginPage() {

    const location = useLocation();
    const [authCode, setAuthCode] = useState(null);
    const [formError, setFormError] = useState(null);
    const [params, setParams] = useState(getQueryStringAsJson(location));

    const handleLogin = async (e) => {
        e.preventDefault();
        setFormError(null);
        const email = e.target.elements.email.value.trim();
        const password = e.target.elements.password.value;
        const error = checkLoginForm(email, password);
        if (error !== null) {
            setFormError(error);
            return;
        }
        const response = await sendLoginRequest(email, password, params);
        console.log(response);
        if (response.error !== null) {
            console.log("Error detected...");
            setFormError(response.error);
            return;
        }
        setParams(response.params);
        setAuthCode(response.code);
    }

    const redirectToCallback = () => {
        let url = `${params.redirect_uri}?code=${authCode}&redirect_uri=${params.redirect_uri}`;
        url += params.client_id === undefined ? "" : `&client_id=${params.client_id}`;
        window.location.href = url;
    }

    return (
        <div className="container-fluid inherit-height">
            {authCode === null ?
                <LoginForm handleLogin={handleLogin} formError={formError} urlParams={params} />
                :
                redirectToCallback()
            }
        </div>
    );
}

export default LoginPage;
