import * as operators from './operators';

export default class Query {
	constructor() {
		this.stack = [];

		Object.keys(operators).forEach((k) => {
			const key = k.charAt(0).toLowerCase() + k.slice(1);
			this[key] = (...args) => this.add(new operators[k](...args));
		});
	}

	add(op) {
		if (
			this.last instanceof Query &&
			!(op instanceof operators.And || op instanceof operators.Or)
		) {
			throw new Error('Only AND or OR operators are allowed after a query.');
		}

		this.stack.push(op);
		return this;
	}

	nest(op) {
		if (
			op instanceof Query &&
			(this.last instanceof operators.And || this.last instanceof operators.Or)
		) {
			this.stack.push(op);
			return this;
		}

		throw new Error("You can't nest a query if there is no AND or OR statement before.");
	}

	serialize() {
		return this.stack
			.map((o) => {
				if (o instanceof Query) {
					return `(${o.serialize()})`;
				}
				return o.serialize();
			})
			.join(' ');
	}

	get last() {
		return this.stack.length ? this.stack[this.stack.length - 1] : undefined;
	}
}