$.notifyDefaults({
	mouse_over: 'pause',
	z_index: 5000,
	delay: 5000,
	placement: {
		from: "bottom",
		align: "right"
	},
	animate: {
		enter: 'animated fadeInRight',
		exit: 'animated fadeOutRight'
	}

});

class Login extends React.Component {
	constructor() {
		super();
		this.state = {
			form: {
				hostname: "support@acme.com",
				ip: "192.168.0.1",
				target: "",
				type: "A",
				ttl: 60
			},
			showIp: true,
			showTarget: false,
			valueField: {}
		};
	}

	componentDidMount(){
		this.processValueLabel(this.state.form.type);
	}

	handleIp(e){
		let form = this.state.form;
		form[e.target.name] = e.target.value.split("\.").map(it => parseInt(it));
		this.setState({ form });
	}

	handleChange(evt) {
		let form = this.state.form;
		form[evt.target.name] = evt.target.value;
		this.setState({ form });
		console.debug('m=handleChange, %s=%s', evt.target.name, evt.target.value);
	}

	handleType(evt){
		let form = this.state.form;
		form[evt.target.name] = evt.target.value;
		if(evt.target.name === 'A'){
			this.state.showIp = true;
			this.state.showTarget = false;
		} else {
			this.state.showIp = false;
			this.state.showTarget = true;
		}
		this.setState({form: form});
	}

	processValueLabel(k){
		let label = {
			'A': {
				label: 'IP *',
				field: 'ip'
			},
			'CNAME': {
				label: 'CNAME *',
				field: 'target'
			}
		}[k];
		this.setState({valueField: label});
		return label;
	}

	handleSubmit(e) {
		e.preventDefault();
		e.target.checkValidity();
		$.ajax({
			method: 'POST',
			url: '/hostname/',
			contentType: 'application/json',
			// dataType: 'json',
			data: JSON.stringify(this.state.form),
		})
		.done(function(){
			$.notify({
				message: 'Saved'
			});
		})
		.fail(function(err){
			console.error('m=saveNewLine, status=error', err);
			if(err.status < 500){
				$.notify({message: JSON.parse(err.responseText).message}, {type: 'danger'});
			} else {
				$.notify({message: err.responseText}, {type: 'danger'});
			}
		});
		;
	}

	render() {
		return (
			<form onSubmit={(e) => this.handleSubmit(e)}>
				<table className="table table-bordered table-hover table-condensed ">
					<colgroup>
						<col width="50%"/>
						<col width="14.5%"/>
						<col width="14.5%"/>
						<col width="9%" style={{textAlign: "right"}}/>
						<col width="7.5%"/>
					</colgroup>
					<tbody>
					<tr>
						<th>
							<label className="control-label " htmlFor="hostname">
								Hostname<span className="asteriskField">*</span>
							</label>
						</th>
						<th>
							{this.state.showIp &&
							<label className="control-label requiredField" htmlFor="ip">
								IP<span className="asteriskField">*</span>
							</label>
							}
							{
								this.state.showTarget &&
								<label className="control-label requiredField" htmlFor="target" required>
									Target<span className="asteriskField">*</span>
								</label>
							}
						</th>
						<th>
							<label className="control-label">
								Type<span className="asteriskField">*</span>
							</label>
						</th>
						<th>
							<label className="control-label requiredField" htmlFor="ttl">
								TTL<span className="asteriskField">*</span>
							</label>
						</th>
						<th>Actions</th>
					</tr>
					<tr>
						<td>
							<input
								className="form-control"
								id="hostname"
								name="hostname"
								onChange={(e) => this.handleChange(e)}
								value={this.state.form.hostname}
								type="text"
								required
							/>
						</td>
						<td>
							{
								this.state.showIp &&
								<input className="form-control"
											 pattern="[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+"
											 title="Please provide a valid IP" name="ip" id="ip"
											 onChange={(e) => this.handleIp(e)}
											 required
								/>
							}
							{
								this.state.showTarget &&
								<input className="form-control" name="target" id="target" onChange={(e) => this.handleChange(e)}/>
							}
						</td>
						<td>
							<select name="type" onChange={(e) => this.handleType(e)} className="form-control" type="text">
								<option value="A">A</option>
								<option value="CNAME">CNAME</option>
							</select>
						</td>
						<td>
							<input
								onChange={(e) => this.handleChange(e)}
								className="form-control"
								value={this.state.form.ttl}
								id="ttl"
								name="ttl"
								type="number"
								size="3"
								min="1"
								required
							/>
						</td>
						<td className="text-right">
							<button type="submit" className="btn btn-info">
								<span className="fa fa-save"/>
							</button>
						</td>
					</tr>
					</tbody>
				</table>
			</form>
		);
	}
}

ReactDOM.render(<Login />, document.getElementById("root"));
