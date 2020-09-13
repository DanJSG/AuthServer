import React, { useState } from 'react';
import { checkForm, sendRegistrationRequest } from './services/signupservice';
import { sendLoginRequest } from '../LoginPage/services/loginservice';
import { generateState, generateCodeChallenge } from '../../services/challengeservice';
import SignUpForm from './Forms/SignUpForm';

function SignUpPage() {

    const [formError, setFormError] = useState(null);
    const [authCode, setAuthCode] = useState(null);
    const [state] = useState(generateState());
    const [codeChallenge] = useState(generateCodeChallenge());
    const [params] = useState({
        client_id: 'ThpDT2t2EDlO',
        redirect_uri: 'http://local.courier.net:3000/oauth2/auth_callback',
        response_type: 'code'
    });

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
        const response = await sendLoginRequest(email, password, params, codeChallenge, state);
        if (response.error !== null) {
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

    return (
        <div className="container-fluid inherit-height">
            {authCode === null ?
                <SignUpForm handleRegister={handleRegister} formError={formError} />
                :
                redirectToCallback()
            }
        </div>
    );
}

export default SignUpPage;
