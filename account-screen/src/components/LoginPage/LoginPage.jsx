import React, {useState} from 'react';
import {useLocation} from 'react-router-dom';
import {checkLoginForm, sendLoginRequest} from './services/loginservice';
import LoginForm from './Forms/LoginForm';
import {getQueryStringAsJson} from '../../services/querystringmanipulator';

// local.courier.net:3000/oauth2/authorize?audience=courier&scope=name+email&response_type=code&client_id=ThpDT2t2EDlO&redirect_uri=http://local.courier.net:3000/oauth2/auth_callback

function LoginPage() {

    const location = useLocation();
    const [authCode, setAuthCode] = useState(null);
    const [formError, setFormError] = useState(null);
    const [params] = useState(getQueryStringAsJson(location));

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
        const response = await sendLoginRequest(email, password, params);
        if(response.error !== null) {
            setFormError(response.error);
            return;
        }
        setAuthCode(response.code);
    }

    const redirectToCallback = () => {
        window.location.href = `${params.redirect_uri}` +
                                `?code=${authCode}` + 
                                `&client_id=${params.client_id}` + 
                                `&redirect_uri=${params.redirect_uri}`;
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
