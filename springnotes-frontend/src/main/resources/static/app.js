'use strict';

const React = require('react');
const when = require('when');
const client = require('./client');

const follow = require('./follow'); // function to hop multiple links by "rel"

const stompClient = require('./websocket-listener');

const root = '/api';

class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {notes: [], attributes: [], page: 1, pageSize: 5, links: {}};
		this.updatePageSize = this.updatePageSize.bind(this);
		this.onCreate = this.onCreate.bind(this);
		this.onUpdate = this.onUpdate.bind(this);
		this.onDelete = this.onDelete.bind(this);
		this.onNavigate = this.onNavigate.bind(this);
		this.refreshCurrentPage = this.refreshCurrentPage.bind(this);
		this.refreshAndGoToLastPage = this.refreshAndGoToLastPage.bind(this);
	}

	loadFromServer(pageSize) {
		follow(client, root, [
				{rel: 'notes', params: {size: pageSize}}]
		).then(noteCollection => {
				return client({
					method: 'GET',
					path: noteCollection.entity._links.profile.href,
					headers: {'Accept': 'application/schema+json'}
				}).then(schema => {
					this.schema = schema.entity;
					this.links = noteCollection.entity._links;
					return noteCollection;
				});
		}).then(noteCollection => {
			this.page = noteCollection.entity.page;
			return noteCollection.entity._embedded.notes.map(note =>
					client({
						method: 'GET',
						path: note._links.self.href
					})
			);
		}).then(notePromises => {
			return when.all(notePromises);
		}).done(notes => {
			this.setState({
				page: this.page,
				notes: notes,
				attributes: Object.keys(this.schema.properties),
				pageSize: pageSize,
				links: this.links
			});
		});
	}

	// tag::on-create[]
	onCreate(newNote) {
		follow(client, root, ['notes']).done(response => {
			client({
				method: 'POST',
				path: response.entity._links.self.href,
				entity: newNote,
				headers: {'Content-Type': 'application/json'}
			})
		})
	}
	// end::on-create[]

	onUpdate(note, updatedNote) {
		client({
			method: 'PUT',
			path: note.entity._links.self.href,
			entity: updatedNote,
			headers: {
				'Content-Type': 'application/json',
				'If-Match': note.headers.Etag
			}
		}).done(response => {
			/* Let the websocket handler update the state */
		}, response => {
			if (response.status.code === 412) {
				alert('DENIED: Unable to update ' + note.entity._links.self.href + '. Your copy is stale.');
			}
		});
	}

	onDelete(note) {
		client({method: 'DELETE', path: note.entity._links.self.href});
	}

	onNavigate(navUri) {
		client({
			method: 'GET',
			path: navUri
		}).then(noteCollection => {
			this.links = noteCollection.entity._links;
			this.page = noteCollection.entity.page;

			return noteCollection.entity._embedded.notes.map(note =>
					client({
						method: 'GET',
						path: note._links.self.href
					})
			);
		}).then(notePromises => {
			return when.all(notePromises);
		}).done(notes => {
			this.setState({
				page: this.page,
				notes: notes,
				attributes: Object.keys(this.schema.properties),
				pageSize: this.state.pageSize,
				links: this.links
			});
		});
	}

	updatePageSize(pageSize) {
		if (pageSize !== this.state.pageSize) {
			this.loadFromServer(pageSize);
		}
	}

	// tag::websocket-handlers[]
	refreshAndGoToLastPage(message) {
		follow(client, root, [{
			rel: 'notes',
			params: {size: this.state.pageSize}
		}]).done(response => {
			this.onNavigate(response.entity._links.last.href);
		})
	}

	refreshCurrentPage(message) {
		follow(client, root, [{
			rel: 'notes',
			params: {
				size: this.state.pageSize,
				page: this.state.page.number
			}
		}]).then(noteCollection => {
			this.links = noteCollection.entity._links;
			this.page = noteCollection.entity.page;

			return noteCollection.entity._embedded.notes.map(note => {
				return client({
					method: 'GET',
					path: note._links.self.href
				})
			});
		}).then(notePromises => {
			return when.all(notePromises);
		}).then(notes => {
			this.setState({
				page: this.page,
				notes: notes,
				attributes: Object.keys(this.schema.properties),
				pageSize: this.state.pageSize,
				links: this.links
			});
		});
	}
	// end::websocket-handlers[]

	// tag::register-handlers[]
	componentDidMount() {
		this.loadFromServer(this.state.pageSize);
		stompClient.register([
			{route: '/topic/newNote', callback: this.refreshAndGoToLastPage},
			{route: '/topic/updateNote', callback: this.refreshCurrentPage},
			{route: '/topic/deleteNote', callback: this.refreshCurrentPage}
		]);
	}
	// end::register-handlers[]

	render() {
		return (
			<div>
				<CreateDialog attributes={this.state.attributes} onCreate={this.onCreate}/>
				<NoteList page={this.state.page}
							  notes={this.state.notes}
							  links={this.state.links}
							  pageSize={this.state.pageSize}
							  attributes={this.state.attributes}
							  onNavigate={this.onNavigate}
							  onUpdate={this.onUpdate}
							  onDelete={this.onDelete}
							  updatePageSize={this.updatePageSize}/>
			</div>
		)
	}
}

class CreateDialog extends React.Component {

	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		e.preventDefault();
		var newNote = {};
		this.props.attributes.forEach(attribute => {
			newNote[attribute] = React.findDOMNode(this.refs[attribute]).value.trim();
		});
		this.props.onCreate(newNote);
		this.props.attributes.forEach(attribute => {
			React.findDOMNode(this.refs[attribute]).value = ''; // clear out the dialog's inputs
		});
		window.location = "#";
	}

	render() {
		var inputs = this.props.attributes.map(attribute =>
				<p key={attribute}>
					<input type="text" placeholder={attribute} ref={attribute} className="field" />
				</p>
		);
		return (
			<div>
				<a href="#createNote">Create</a>

				<div id="createNote" className="modalDialog">
					<div>
						<a href="#" title="Close" className="close">X</a>

						<h2>Create new note</h2>

						<form>
							{inputs}
							<button onClick={this.handleSubmit}>Create</button>
						</form>
					</div>
				</div>
			</div>
		)
	}
}

class UpdateDialog extends React.Component {

	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		e.preventDefault();
		var updatedNote = {};
		this.props.attributes.forEach(attribute => {
			updatedNote[attribute] = React.findDOMNode(this.refs[attribute]).value.trim();
		});
		this.props.onUpdate(this.props.note, updatedNote);
		window.location = "#";
	}

	render() {
		var inputs = this.props.attributes.map(attribute =>
				<p key={this.props.note.entity[attribute]}>
					<input type="text" placeholder={attribute}
						   defaultValue={this.props.note.entity[attribute]}
						   ref={attribute} className="field" />
				</p>
		);

		var dialogId = "updateNote-" + this.props.note.entity._links.self.href;

		return (
			<div>
				<a href={"#" + dialogId}>Update</a>

				<div id={dialogId} className="modalDialog">
					<div>
						<a href="#" title="Close" className="close">X</a>

						<h2>Update an note</h2>

						<form>
							{inputs}
							<button onClick={this.handleSubmit}>Update</button>
						</form>
					</div>
				</div>
			</div>
		)
	}

}

class NoteList extends React.Component {

	constructor(props) {
		super(props);
		this.handleNavFirst = this.handleNavFirst.bind(this);
		this.handleNavPrev = this.handleNavPrev.bind(this);
		this.handleNavNext = this.handleNavNext.bind(this);
		this.handleNavLast = this.handleNavLast.bind(this);
		this.handleInput = this.handleInput.bind(this);
	}

	handleInput(e) {
		e.preventDefault();
		var pageSize = React.findDOMNode(this.refs.pageSize).value;
		if (/^[0-9]+$/.test(pageSize)) {
			this.props.updatePageSize(pageSize);
		} else {
			React.findDOMNode(this.refs.pageSize).value = pageSize.substring(0, pageSize.length - 1);
		}
	}

	handleNavFirst(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.first.href);
	}

	handleNavPrev(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.prev.href);
	}

	handleNavNext(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.next.href);
	}

	handleNavLast(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.last.href);
	}

	render() {
		var pageInfo = this.props.page.hasOwnProperty("number") ?
			<h3>Notes - Page {this.props.page.number + 1} of {this.props.page.totalPages}</h3> : null;

		var notes = this.props.notes.map(note =>
			<Note key={note.entity._links.self.href}
					  note={note}
					  attributes={this.props.attributes}
					  onUpdate={this.props.onUpdate}
					  onDelete={this.props.onDelete}/>
		);

		var navLinks = [];
		if ("first" in this.props.links) {
			navLinks.push(<button key="first" onClick={this.handleNavFirst}>&lt;&lt;</button>);
		}
		if ("prev" in this.props.links) {
			navLinks.push(<button key="prev" onClick={this.handleNavPrev}>&lt;</button>);
		}
		if ("next" in this.props.links) {
			navLinks.push(<button key="next" onClick={this.handleNavNext}>&gt;</button>);
		}
		if ("last" in this.props.links) {
			navLinks.push(<button key="last" onClick={this.handleNavLast}>&gt;&gt;</button>);
		}

		return (
			<div>
				{pageInfo}
				<input ref="pageSize" defaultValue={this.props.pageSize} onInput={this.handleInput}/>
				<table>
					<tr>
						<th>Title</th>
						<th>Text</th>
						<th>Notebook</th>
						<th></th>
						<th></th>
					</tr>
					{notes}
				</table>
				<div>
					{navLinks}
				</div>
			</div>
		)
	}
}

class Note extends React.Component {

	constructor(props) {
		super(props);
		this.handleDelete = this.handleDelete.bind(this);
	}

	handleDelete() {
		this.props.onDelete(this.props.note);
	}

	render() {
		return (
			<tr>
				<td>{this.props.note.entity.title}</td>
				<td>{this.props.note.entity.body}</td>
				<td>{this.props.note.entity.notebook.title}</td>
				<td>
					<UpdateDialog note={this.props.note}
								  attributes={this.props.attributes}
								  onUpdate={this.props.onUpdate}/>
				</td>
				<td>
					<button onClick={this.handleDelete}>Delete</button>
				</td>
			</tr>
		)
	}
}

React.render(
	<App />,
	document.getElementById('react')
)
