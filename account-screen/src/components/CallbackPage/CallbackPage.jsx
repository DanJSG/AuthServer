import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { requestTokens } from './services/callbackservice';
import { getQueryStringAsJson } from '../../services/querystringmanipulator';

function CallbackPage() {

    const location = useLocation();
    const [params] = useState(getQueryStringAsJson(location));

    useEffect(() => {
        requestTokens(params);
    }, [])

    return null;

}

export default CallbackPage;