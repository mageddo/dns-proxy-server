import React from 'react';
import $ from "jquery";

export class Settings extends React.Component {
  constructor(props) {
    super();
    this.state = {};
    this.props = props;
  }

  componentDidMount() {

  }

  clearCache(){
    window.$.ajax({
      method: "DELETE",
      url: "/v1/caches",
      error: response => {
        window.$.notify({
          title: 'Ops!',
          message: 'An error ocurred',
        }, {
          type: 'danger'
        });
      },
      success: () => {
        window.$.notify({
          message: `Cache cleared`
        }, {
          type: 'success'
        });
      },
    });
  }

  render() {
    return (
      <>
        <div className="modal" tabIndex="-1" role="dialog" id="settings-modal" style={{zIndex: 9999}}>
          <div className="modal-dialog" role="document">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Settings</h5>
                <button
                  type="button" className="close" data-dismiss="modal" aria-label="Close"
                >
                  <span aria-hidden="true">&times;</span>
                </button>
              </div>
              <div className="modal-body">
                <form>
                  <div className="form-group">
                    <button
                      type="button" className="btn btn-info" id="btnClearCache"
                      onClick={(e) => this.clearCache()}
                    >
                      <i className="fa fa-trash"></i>
                    </button>&nbsp;
                    <label htmlFor="formGroupExampleInput">Clear Cache</label>
                  </div>
                </form>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" data-dismiss="modal">Close</button>
              </div>
            </div>
          </div>
        </div>
      </>
    );
  }
}
