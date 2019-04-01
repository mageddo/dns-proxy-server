import React from 'react'
import {RecordForm} from './RecordForm.js'
import {RecordTable} from './RecordTable.js'

export class Home extends React.Component {
	constructor() {
		super();
		this.state = {
			forceUpdate: null
		};
	}
	onUpdate(){
		this.table.reloadTable();
	}
	render(){
		return (
			<div>
				<nav className="navbar navbar-inverse navbar-fixed-top" >
					<a className="navbar-brand" href="#">DNS PROXY SERVER</a>
					<button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
									aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
						<span className="navbar-toggler-icon"></span>
					</button>
					<div className="collapse navbar-collapse" id="navbarNav">
						<ul className="navbar-nav pull-right">
							<li className="nav-item active">
								<a className="nav-link" href="#">Home <span className="sr-only">(current)</span></a>
							</li>
							<li className="nav-item">
								<a className="nav-link" href="#">Features</a>
							</li>
							<li className="nav-item">
								<a className="nav-link" href="#">Pricing</a>
							</li>
							<li className="nav-item">
								<a className="nav-link disabled" href="#">Disabled</a>
							</li>
						</ul>
					</div>
				</nav>
				<div className="container">
					<h3>New Record</h3>
					<RecordForm onUpdate={(e) => this.onUpdate(e)} />
					<RecordTable ref={(it) => this.table = it} />
				</div>
			</div>
		);
	}
}
