export const authorize = async () => {
    const url = "http://local.courier.net:8090/api/v1/settings/auth";
    return fetch(url, {
        method: "POST",
        credentials: "include",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("acc.tok"),
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (response.status !== 200) {
                console.log(response);
                throw Error("Failed to authorize your account.");
            }
            return response.json();
        })
        .catch(error => {
            console.error(error);
            return null;
        })
}
