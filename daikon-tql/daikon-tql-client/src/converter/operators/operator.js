import Empty from './empty';
import ISerializable from './iserializable';

const WILDCARD = '*';

function isString(value) {
	return typeof value === 'string';
}

export function wrap(value) {
	return isString(value) ? `'${value.replace(/'/g, "\\'")}'` : value;
}

export function isDefined(value) {
	return value != null && value !== '';
}

/**
 * Class representing an operator.
 * @extends ISerializable
 */
export default class Operator extends ISerializable {
	/**
	 * Create an Operator.
	 * @param {string} field - The field on which to apply the operator.
	 * @param {any} operands - The operand(s) of the operation.
	 * @param {object} options - The options of the operator.
	 * This could be an array or a single value.
	 */
	constructor(field, operands, options) {
		super();
		this.field = field || WILDCARD;
		this.options = options || {};

		if (Array.isArray(operands)) {
			this.operand = operands.length > 1 ? operands : operands[0];
		} else {
			this.operand = operands;
		}
	}

	/**
	 * Converts the operator to its TQL equivalent.
	 * @return {string} The TQL expression.
	 */
	serialize() {
		if (this.constructor.hasOperand !== false && isDefined(this.operand)) {
			return `(${this.field} ${this.constructor.value} ${wrap(this.operand)})`;
		} else if (this.constructor.hasOperand === false) {
			return `(${this.field} ${this.constructor.value})`;
		} else if (this.constructor.allowEmpty === true) {
			return `(${this.field} ${Empty.value})`;
		}

		throw new Error(`${this.constructor.value} does not allow empty.`);
	}
}
