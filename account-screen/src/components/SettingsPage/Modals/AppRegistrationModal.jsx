import React from 'react';

function AppRegistrationModal(props) {

    const saveClicked = (e) => {
        e.preventDefault();
        const name = e.target.elements.name.value === undefined || e.target.elements.name.value == null ? "" : e.target.elements.name.value.trim();
        const uri = e.target.elements.redirectUri.value === undefined || e.target.elements.redirectUri.value == null ? "" : e.target.elements.redirectUri.value.trim();
        e.target.elements.name.value = null;
        e.target.elements.redirectUri.value = null;
        console.log(name);
        console.log(uri);
        // send request
        setTimeout(() => props.close(), 300);
    }

    const cancelClicked = (e) => {
        e.preventDefault();
        props.close();
    }

    return (
        props.visible ?
            <React.Fragment>
                <div style={{
                    position: "absolute",
                    width: "80vw",
                    height: "80vh",
                    marginLeft: "-40vw",
                    marginTop: "-40vh",
                    left: "50%",
                    top: "33%",
                    zIndex: 4
                }}>
                    <div className="modal-dialog modal-dialog-centered p-4 w-100 h-100">
                        <div className="modal-content">
                            <div className="modal-header">
                                <div className="d-flex w-100 justify-content-between">
                                    <h4 className="modal-title">{props.title}</h4>
                                    <button onClick={props.close} style={{ fontSize: "20px" }} className="btn my-0 py-0"><i className="fa fa-times"></i></button>
                                </div>
                            </div>
                            <div className="modal-body">
                                <form onSubmit={saveClicked}>
                                    <div className="form-group">
                                        <input name="name" className="form-control selectable w-75" placeholder="Name"></input>
                                    </div>
                                    <div className="form-group">
                                        <input name="redirectUri" className="form-control selectable w-75" placeholder="New redirect URI"></input>
                                    </div>
                                    <div className="form-group">
                                        <button className="btn btn-primary mx-1">Save</button>
                                        <button className="btn btn-secondary mx-1" onClick={cancelClicked}>Cancel</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <div style={{
                    position: "fixed",
                    width: "100vw",
                    height: "100%",
                    padding: 0,
                    margin: 0,
                    top: 0,
                    bottom: 0,
                    left: 0,
                    right: 0,
                    backgroundColor: "rgb(0,0,0,0.75)",
                    zIndex: 3
                }}></div>
            </React.Fragment>
            :
            null
    )
}

export default AppRegistrationModal;