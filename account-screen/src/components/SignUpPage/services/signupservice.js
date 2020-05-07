export const sendRegistrationRequest = async (email, username, password) => {
    const url = `http://local.courier.net:8080/api/v1/register`;
    const details = JSON.stringify({
        email: email,
        password: password,
        username: username
    });
    return await fetch(url, {
        method: "POST",
        body: details,
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then((response) => {
        if(response.status !== 200) {
            return "Failed to create account.";
        }
        return null;
    })
    .catch((error) => {
        return "An error occurred when contacting the server.";
    });
}

export const checkForm = (email, username, password, repeatPassword) => {
    if(email === undefined || email === null || email === "") {
        return "Please enter an email address.";
    }
    if(username === undefined || username === null || username === "") {
        return "Please enter a username.";
    }
    if(password === undefined || password === null || password === "") {
        return "Please enter a password.";
    }
    if(password.length < 8) {
        return "Your password must be at least 8 characters long.";
    }
    if(password !== repeatPassword) {
        return "The two passwords do not match.";
    }
    return null;
}
