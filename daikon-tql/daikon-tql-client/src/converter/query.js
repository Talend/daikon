import * as operators from './operators';

export default class Query {
	constructor() {
		this.stack = [];

		Object.keys(operators).forEach((k) => {
			this[k] = (...args) => this.add(new operators[k](...args));
		});
	}

	add(op) {
		this.stack.push(op);
		return this;
	}

	toTQL() {
		return this.stack.map(o => o.toTQL()).join(' ');
	}
}
