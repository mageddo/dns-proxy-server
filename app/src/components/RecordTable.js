import React from 'react';
import jquery from 'jquery'
let $ = jquery;
export class RecordTable extends React.Component {
	constructor(props){
		super();
		this.state = {
			table: [],
			someText: "some text"
		};
		this.props = props;
	}
	componentDidMount(){
		this.reloadTable();
	}

	reloadTable() {
		var that = this;
		return $.ajax({
			url: '/hostname/find/?env=' + window.activeEnv + '&hostname='
		}).then(function (data) {
			that.setState({table: data});
			console.debug('m=getData, data=%o', data);
		}, function (err) {
			console.error('m=getData, status=error', err);
		});
	}

	swapEditionMode(row){
		row.editing = !row.editing;
		this.setState({someText: "hi"});
	}

	updateRecord(row){
		this.swapEditionMode(row);
		console.info('updating row ' + row.id);
		$.ajax( {
			url: '/hostname',
			method: 'PUT',
			contentType: 'application/json',
			data: JSON.stringify({
				env: window.activeEnv,
				id: row.id,
				hostname: row.hostname,
				ip: row.ip,
				ttl: row.ttl
			})
		}).then(function(data) {
			console.debug('m=save, status=success')
			window.$.notify({
				message: 'Saved'
			});
		}, function(err){
			console.error('m=save, status=error', err);
			window.$.notify({message: err.responseText}, {type: 'danger'});
		});
	}

	deleteRecord(row){
		console.info('deleting ' + row.id);
	}

	formatIp(ip){
		return ip.join('.');
	}

	handleChange(evt, row) {
		row[evt.target.name] = evt.target.value;
		console.debug('m=handleChange, %s=%s, row=%o', evt.target.name, evt.target.value, row);
		this.forceUpdate();
	}

	handleIpChange(e, row) {
		row[e.target.name] = e.target.value.split("\.").map(it => parseInt(it));
	}

	handleTargetChange(evt, row) {
		row[evt.target.name] = evt.target.value;
		console.debug('m=handleChange, %s=%s', evt.target.name, evt.target.value);
	}

	renderLineView(v, k){
		return <tr key={k}>
			<td>{v.hostname}</td>
			<td className="text-center">{v.type}</td>
			{v.type === 'A' && <td>{this.formatIp(v.ip)}</td>}
			{v.type === 'CNAME' && <td>{v.target}</td>}
			<td className="text-right">{v.ttl}</td>
			<td className="text-right records-actions">
				<button className="btn btn-info fa fa-pencil-alt" onClick={(e) => this.swapEditionMode(v) } ></button>
				<button className="btn btn-danger fa fa-trash-alt" onClick={(e) => this.deleteRecord(v) } ></button>
			</td>
		</tr>
	}

	renderLineEditing(v, k){
		return <tr key={k}>
			<td>
				<input className="form-control" name="hostname" onChange={(e) => this.handleChange(e, v)} value={v.hostname}/>
			</td>
			<td className="text-center">
				<input className="form-control" name="type" onChange={(e) => this.handleChange(e, v)} value={v.type}/>
			</td>
			{v.type === 'A' &&
			<td>
				<input className="form-control" name="ip" type="text" onChange={(e) => this.handleIpChange(e, v)} value={this.formatIp(v.ip)}/>
			</td>
			}
			{v.type === 'CNAME' &&
			<td>
				<input className="form-control" name="target" type="text" onChange={(e) => this.handleTargetChange(e, v)} value={v.target}/>
			</td>
			}
			<td className="text-right">
				<input className="form-control" name="ttl" type="text" onChange={(e) => this.handleChange(e, v)} value={v.ttl}/>
			</td>
			<td className="text-right records-actions">
				<button className="btn btn-primary fa fa-save" onClick={(e) => this.updateRecord(v) } ></button>
				<button className="btn btn-danger fa fa-window-close" onClick={(e) => this.swapEditionMode(v) } ></button>
			</td>
		</tr>
	}

	render(){
		return (
			<div >
				<h3>Records</h3>
				<table className="table table-bordered table-hover table-condensed editable-table demoTable table-hostnames" >
					<colgroup>
						<col width="50%"/>
						<col width="10%"/>
						<col width="15%"/>
						<col width="10%"/>
						<col width="10%"/>
					</colgroup>
					<thead className="thead-dark">
						<tr>
							<th>Hostname</th>
							<th className="text-center">Type</th>
							<th>Value</th>
							<th >TTL</th>
							<th className="text-center">Actions</th>
						</tr>
					</thead>
					<tbody>
					{
						this.state.table.map((v, k) => {
							return v.editing ? this.renderLineEditing(v, k) : this.renderLineView(v, k)
						})
					}
					</tbody>
				</table>
			</div>
		);
	}
}
