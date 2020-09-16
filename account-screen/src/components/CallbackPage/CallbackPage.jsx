import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { requestTokens } from './services/callbackservice';
import { getQueryStringAsJson } from '../../services/querystringmanipulator';

function CallbackPage() {

    const location = useLocation();
    const [params] = useState(getQueryStringAsJson(location));

    useEffect(() => {
        requestTokens(params, sessionStorage.getItem("state"), sessionStorage.getItem("code_verifier"));
        sessionStorage.removeItem("state");
        sessionStorage.removeItem("code_verifier");
    }, [])

    return <p>Loading, please wait...</p>;

}

export default CallbackPage;