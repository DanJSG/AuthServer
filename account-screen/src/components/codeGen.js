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

const generateCodeChallenge = () => {
    const length = Math.floor(Math.random() * 86) + 43;
    console.log("Char length: " + length);
    const codeVerifier = generateSecureString(length);
    console.log("Code verifier: " + codeVerifier);
    console.log("Hashed: " + SHA256(codeVerifier).toString());
    console.log("Base64: " + btoa(SHA256(codeVerifier).toString()));
    console.log("Base64url: " + b64urlEncode(SHA256(codeVerifier).toString()));
    return {
        code_verifier: codeVerifier,
        code_challenge: b64urlEncode(SHA256(codeVerifier).toString())
    };
}

const generateState = () => {
    return generateSecureString(12);
}

export {generateCodeChallenge, generateState}