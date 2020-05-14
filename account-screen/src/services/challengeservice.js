import {SHA256} from 'crypto-js';

const generateSecureString = (length) => {
    const randArray = new Uint8Array(length);
    const chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    let secureString = "";
    window.crypto.getRandomValues(randArray);
    for(const i in randArray) {
        secureString += chars[randArray[i] % chars.length]
    }
    return secureString;
}

const b64urlEncode = (value) => {
    return btoa(value).replace(/\//g, "_").replace(/\+/g, "-").replace(/=/g, "");
}

export const generateCodeChallenge = () => {
    const length = Math.floor(Math.random() * 86) + 43;
    const codeVerifier = generateSecureString(length);
    return {
        code_verifier: codeVerifier,
        code_challenge: b64urlEncode(SHA256(codeVerifier).toString()),
        code_challenge_method: 'S256'
    };
}

export const generateState = () => {
    return generateSecureString(12);
}
