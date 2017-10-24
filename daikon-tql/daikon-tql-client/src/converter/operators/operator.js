import Empty from './empty';

function isString(value) {
	return typeof value === 'string';
}

function wrap(value) {
	return isString(value) ? `'${value}'` : value;
}

export default class Operator {
	constructor(field, operand) {
		this.field = field;
		this.operand = operand;
	}

	toTQL() {
		if (this.constructor.HasOperand !== false && this.operand !== '') {
			return `(${this.field} ${this.constructor.Value} ${wrap(this.operand)})`;
		} else if (this.constructor.HasOperand === false) {
			return `(${this.field} ${this.constructor.Value})`;
		}

		return `(${this.field} ${Empty.Value})`;
	}
}
