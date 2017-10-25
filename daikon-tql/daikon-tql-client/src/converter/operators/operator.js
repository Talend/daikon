import Empty from './empty';
import ISerializable from './serializable';

function isString(value) {
	return typeof value === 'string';
}

function wrap(value) {
	return isString(value) ? `'${value}'` : value;
}

export default class Operator extends ISerializable {
	constructor(field, ...operands) {
		super();
		this.field = field;
		this.operand = operands.length > 1 ? operands : operands[0];
	}

	serialize() {
		if (this.constructor.HasOperand !== false && this.operand !== '') {
			return `(${this.field} ${this.constructor.Value} ${wrap(this.operand)})`;
		} else if (this.constructor.HasOperand === false) {
			return `(${this.field} ${this.constructor.Value})`;
		}

		return `(${this.field} ${Empty.Value})`;
	}
}
