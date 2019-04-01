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

	formatIp(ip){
		return ip.join('.');
	}

	renderLineView(v, k){
		return <tr key={k}>
			<td>{v.hostname}</td>
			<td className="text-center">{v.type}</td>
			{v.type === 'A' && <td>{this.formatIp(v.ip)}</td>}
			{v.type === 'CNAME' && <td>{v.target}</td>}
			<td className="text-right">{v.ttl}</td>
			<td className="text-right records-actions">
				<button className="btn btn-info fa fa-pencil-alt" onClick={(e) => { v.editing = !v.editing; this.setState({someText: "hi"})} } ></button>
				<button className="btn btn-danger fa fa-trash-alt" ></button>
				{/*<button className="btn btn-primary fa fa-save" ></button>*/}
				{/*<button className="btn btn-danger fa fa-window-close" ></button>*/}
			</td>
		</tr>
	}

	renderLineEditing(v, k){
		return <tr key={k}>
			<td><input className="form-control" value={v.hostname}/></td>
			<td className="text-center"><input className="form-control" value={v.type}/></td>
			{v.type === 'A' && <td><input className="form-control"  type="text" value={this.formatIp(v.ip)}/></td>}
			{v.type === 'CNAME' && <td><input className="form-control" type="text" value={v.target}/></td>}
			<td className="text-right"><input className="form-control" type="text" value={v.ttl}/></td>
			<td className="text-right records-actions">
				<button className="btn btn-primary fa fa-save" ></button>
				<button className="btn btn-danger fa fa-window-close" ></button>
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
					<tbody>
					<tr>
						<td>Hostname</td>
						<td className="text-center">Type</td>
						<td>Value</td>
						<td >TTL</td>
						<td className="text-center">Actions</td>
					</tr>
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
