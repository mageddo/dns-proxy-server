import React from 'react';
import {Settings} from './Settings.js'

export class NavBar extends React.Component {
  constructor(props) {
    super();
    this.state = {};
    this.props = props;
  }

  componentDidMount() {

  }

  openSettings(){
    window.$('#settings-modal').modal()
  }

  render() {
    return (
      <>
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark navbar-inverse navbar-fixed-top" >
          <a className="navbar-brand" href="#">DNS Proxy Server</a>
          <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
                  aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
          </button>
          <div className="collapse navbar-collapse" id="navbarNav">
            <ul className="navbar-nav mr-auto pull-right">
              <li className="nav-item active">
                <a className="nav-link" href="#">Home <span className="sr-only">(current)</span></a>
              </li>
              <li className="nav-item">
                <a className="nav-link" onClick={(e) => this.openSettings()} href="#">Settings</a>
              </li>
              <li className="nav-item">
                <a className="nav-link" target="_blank" href="http://mageddo.github.io/dns-proxy-server/latest/en/2-features/">Features / Docs</a>
              </li>
            </ul>
          </div>
        </nav>
        <Settings />
      </>
    );
  }
}
