import React from 'react';

function AppRegistrationModal(props) {

    const saveClicked = (e) => {
        e.preventDefault();
    }

    const cancelClicked = (e) => {
        e.preventDefault();
        props.toggleModal();
    }

    return (
        props.show ?
            <div style={{
                position: "absolute",
                width: "80vw",
                height: "80vh",
                marginLeft: "-40vw",
                marginTop: "-40vh",
                left: "50%",
                top: "50%",
            }}>
                <div className="modal-dialog modal-dialog-centered">
                    <div className="modal-content">
                        <div className="modal-header">
                            <div className="d-flex w-100 justify-content-between">
                                <h4 className="modal-title">Edit Application</h4>
                                <button onClick={props.toggleModal} style={{ fontSize: "20px" }} className="btn my-0 py-0"><i className="fa fa-times"></i></button>
                            </div>
                        </div>
                        <div className="modal-body">
                            <form>
                                <div className="form-group">
                                    <input className="form-control selectable w-75" placeholder="Name"></input>
                                </div>
                                <div className="form-group">
                                    <input className="form-control selectable w-75" placeholder="New redirect URI"></input>
                                </div>
                                <div className="form-group">
                                    <button className="btn btn-primary mx-1" onClick={saveClicked}>Save</button>
                                    <button className="btn btn-secondary mx-1" onClick={cancelClicked}>Cancel</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            :
            null
    )
}

export default AppRegistrationModal;