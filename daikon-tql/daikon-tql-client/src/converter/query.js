import * as operators from './operators';
import ISerializable from './operators/iserializable';
import Compositor from './compositor';
import Modifier from './modifier';
import Operator from './operators/operator';

export default class Query extends ISerializable {
	constructor() {
		super();
		this.stack = [];

		Object.keys(operators).forEach((k) => {
			const key = k.charAt(0).toLowerCase() + k.slice(1);
			this[key] = (...args) => this.add(new operators[k](...args));
		});

		this.and = () => this.add(Compositor.get(Compositor.and));
		this.or = () => this.add(Compositor.get(Compositor.or));

		this.not = query => this.add(new Modifier(Modifier.not, query));
	}

	add(op) {
		if (
			(this.last instanceof Query ||
				this.last instanceof Modifier ||
				this.last instanceof Operator) &&
			!(op instanceof Compositor)
		) {
			throw new Error('Only AND or OR operators are allowed after a query.');
		}

		if (op instanceof Modifier && !(this.last instanceof Compositor)) {
			throw new Error('Only Compositors are allowed after a Modifier.');
		}

		this.stack.push(op);
		return this;
	}

	nest(op) {
		if (op instanceof Query && this.last && !(this.last instanceof Compositor)) {
			throw new Error("You can't nest a query if there is no AND or OR statement before.");
		}

		this.stack.push(op);
		return this;
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
