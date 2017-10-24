import { Empty, Equal, Contains, And } from './operators';

export default class Query {
	constructor() {
		this.stack = [];
	}

	add(op) {
		this.stack.push(op);
		return this;
	}

	toTQL() {
		return this.stack.map(o => o.toTQL()).join(' ');
	}

	and = () => this.add(new And());
	empty = field => this.add(new Empty(field));
	equal = (field, op) => this.add(new Equal(field, op));
	contains = (field, op) => this.add(new Contains(field, op));
}
