import EnvPicker from './EnvPicker';
import EnvForm from './EnvForm';
import {NavBar} from './NavBar';
import React from 'react'
import $ from 'jquery';
import {RecordForm} from './RecordForm.js'
import {RecordTable} from './RecordTable.js'

const DEFAULT_ENV = '';

export class Home extends React.PureComponent {
	constructor(...args) {
		super(...args);

		this.state = {
			forceUpdate: null,
			createEnv: false,
			isLoading: true,
			env: ''
		};
	}

	componentDidMount() {
		this.getActiveEnvironment().then(
			data => {
				this.setState({
					isLoading: false,
					env: data.name
				})
			},
			err => {
				window.$.notify({
					message: 'Failed to fetch current environment'
				}, {
					type: 'danger'
				});
				console.error('m=componentDidMount, err=%o', err)
			}
		);
	}

	activate(env) {
		console.log('c=Home, m=activate, env=%s', env);

		const defer = $.Deferred();

		// API is returning an empty body with content type 'application/json', this is causing a parse error
		$.ajax('/env/active', {
			type: 'PUT',
			contentType: 'application/json',
			error: response => {
				if (response.status === 200) {
					defer.resolve();
					return;
				}

				defer.reject();
			},
			success: () => defer.resolve(),
			data: JSON.stringify({
				name: env
			})
		});

		return defer.promise();
	}

	toggleEnvForm() {
		const { createEnv } = this.state;
		console.debug('m=Home, m=toggleEnvForm, createEnv=%s', createEnv);

		this.setState({
			createEnv: !createEnv
		});
	}

	onUpdate(){
		const { state: { env } } = this;

		this.table.reloadTable(env);
		this.envPicker.reload(env);
	}

	onChangeEnv(env) {
		console.debug('c=Home, m=onChangeEnv, env=%s', env);
		this.activate(env)
			.then(
				() => this.setState({ env, createEnv: false }, () => this.table.reloadTable(this.state.env)),
				err => {
					window.$.notify({ message: 'Could not activate environment' }, { type: 'danger' })
					console.error('c=Home, m=onChangeEnv, err=%o', err)
				}
			)
	}

	onCreate(env) {
		this.onChangeEnv(env)
	}

	onDelete() {
		this.onChangeEnv(DEFAULT_ENV)
	}

	getActiveEnvironment() {
		return $.get('/env/active');
	}

	renderTable() {
		return (
			<>
				<div className="row mb-2">
					<div className="col-12">
						<h3>Environments</h3>
						{this.state.createEnv
							? <EnvForm
								onCancel={() => this.toggleEnvForm()}
								onCreate={env => this.onCreate(env)}
							/>
							: <EnvPicker
								onChange={env => this.onChangeEnv(env)}
								onDelete={() => this.onDelete()}
								onToggle={() => this.toggleEnvForm()}
								ref={(it) => this.envPicker = it}
								env={this.state.env}
							/>
						}
					</div>
				</div>
				<div className="row">
					<div className="col-12">
						<h3>New Record</h3>
						<RecordForm env={this.state.env} onUpdate={(e) => this.onUpdate(e)}/>
						<RecordTable env={this.state.env} ref={(it) => this.table = it}/>
					</div>
				</div>
			</>
		)
	}

	renderLoading() {
		return (
			<div className="card">
				<div className="card-body">
					<div className="text-center m-5">
						<div className="spinner-border text-primary" role="status">
							<span className="sr-only">Loading...</span>
						</div>
					</div>
				</div>
			</div>
		)
	}

	render(){
		console.debug('c=Home, m=render, state=%o', this.state)

		return (
			<>
				<NavBar />

				<div className="container mb-5">
					{this.state.isLoading
						? this.renderLoading()
						: this.renderTable()
					}
				</div>
			</>
		);
	}
}
