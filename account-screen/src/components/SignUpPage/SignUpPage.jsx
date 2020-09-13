import React, { useState } from 'react';
import { useLocation } from 'react-router-dom'
import { checkForm, sendRegistrationRequest } from './services/signupservice';
import { sendLoginRequest } from '../LoginPage/services/loginservice';
import SignUpForm from './Forms/SignUpForm';
import { getQueryStringAsJson } from '../../services/querystringmanipulator';

function SignUpPage() {

    const location = useLocation();
    const [formError, setFormError] = useState(null);
    const [authCode, setAuthCode] = useState(null);
    const [params] = useState(getQueryStringAsJson(location));

    const handleRegister = async (e) => {
        e.preventDefault();
        setFormError(null);
        const email = e.target.elements.email.value.trim();
        const username = e.target.elements.username.value.trim();
        const password = e.target.elements.password.value.trim();
        const repeatPassword = e.target.elements.repeatPassword.value.trim();
        let error = checkForm(email, username, password, repeatPassword);
        if (error !== null) {
            setFormError(error);
            return;
        }
        error = await sendRegistrationRequest(email, username, password);
        if (error) {
            setFormError(error);
            return;
        }
        const response = await sendLoginRequest(email, password, params, params.code_challenge, params.state);
        if (response.error !== null) {
            setFormError(response.error);
            return;
        }
        setAuthCode(response.code);
    }

    const redirectToCallback = () => {
        window.location.href = `${params.redirect_uri}?code=${authCode}` +
            `&client_id=${params.client_id}` +
            `&redirect_uri=${params.redirect_uri}`;
    }

    return (
        <div className="container-fluid inherit-height">
            {authCode === null ?
                <SignUpForm handleRegister={handleRegister} formError={formError} urlParams={params} />
                :
                redirectToCallback()
            }
        </div>
    );
}

export default SignUpPage;
