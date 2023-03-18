import React from 'react';

export class Settings extends React.Component {
  constructor(props) {
    super();
    this.state = {};
    this.props = props;
  }

  componentDidMount() {

  }

  render() {
    return (
      <>
        <div className="modal" tabIndex="-1" role="dialog" id="settings-modal">
          <div className="modal-dialog" role="document">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Settings</h5>
                <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                  <span aria-hidden="true">&times;</span>
                </button>
              </div>
              <div className="modal-body">
                <form>
                  <div className="form-group">
                    <label htmlFor="formGroupExampleInput">Clear Cache</label>
                    <button type="button"  className="form-control" id="btnClearCache" ></button>
                  </div>
                </form>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="button" className="btn btn-primary">Save changes</button>
              </div>
            </div>
          </div>
        </div>
      </>
    );
  }
}
