import {useState, useEffect} from 'react';
import {useLocation} from 'react-router-dom';
import {requestTokens} from './services/callbackservice';

function CallbackPage() {

    const location = useLocation();
    const [params] = useState(() => {
        let queryArray = location.search.substring(1).split("&")
        const newParams = {}
        queryArray.forEach((term) => {
            const pair = term.split("=");
            newParams[pair[0]] = pair[1].replace(/[+]/g, " ");
        })
        return newParams;
    });

    useEffect(() => {
        requestTokens(params);
    }, [])

    return null;

}

export default CallbackPage;