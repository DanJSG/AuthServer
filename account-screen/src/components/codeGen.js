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

const generateCodeChallenge = () => {
    const codeVerifier = generateSecureString(32);
    return {
        code_verifier: codeVerifier,
        code_challenge: SHA256(codeVerifier).toString()
    };
}

const generateState = () => {
    return generateSecureString(12);
}

export {generateCodeChallenge, generateState}