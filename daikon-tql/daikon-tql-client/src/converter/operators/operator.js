import Empty from './empty';
import ISerializable from './iserializable';

function isString(value) {
	return typeof value === 'string';
}

function wrap(value) {
	return isString(value) ? `'${value}'` : value;
}

/**
 * Class representing an operator.
 * @extends ISerializable
 */
export default class Operator extends ISerializable {
	/**
	 * Create an Operator.
	 * @param {string} field - The field on which to apply the operator.
	 * @param {Array} operands - The operand(s) of the operation.
	 */
	constructor(field, ...operands) {
		super();
		this.field = field || '*';
		this.operand = operands.length > 1 ? operands : operands[0];
	}

	/**
	 * Converts the operator to its TQL equivalent.
	 * @return {string} The TQL expression.
	 */
	serialize() {
		if (this.constructor.hasOperand !== false && this.operand !== '') {
			return `(${this.field} ${this.constructor.value} ${wrap(this.operand)})`;
		} else if (this.constructor.hasOperand === false) {
			return `(${this.field} ${this.constructor.value})`;
		}

		return `(${this.field} ${Empty.value})`;
	}
}
