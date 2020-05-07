import React from 'react';

function LogoutPage() {

    const client_id="ThpDT2t2EDlO";

    const revokeRefreshToken = () => {
        const token = localStorage.getItem("ref.tok");
        const url = `http://local.courier.net:8080/api/v1/revoke` +
                    `?client_id=${client_id}` + 
                    `&token=${token}`;
        fetch(url, {
            method: "POST",
            credentials: "include"
        })
        .then((response) => {
            if(response.status !== 200) {
                return;
            }
            localStorage.removeItem("ref.tok");
            localStorage.removeItem("acc.tok");
        })
    }

    return(
        <div>
            <button className="btn btn-secondary" onClick={revokeRefreshToken}>Revoke!</button>
        </div>
    )

}

export default LogoutPage;