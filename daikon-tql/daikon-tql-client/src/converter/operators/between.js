import Operator from './operator';

/**
 * Class representing the Between operator.
 * Will be serialized as follows : (field1 between [42, 76])
 */
export default class Between extends Operator {
	static value = 'between';
	static hasOperand = true;

	serialize() {
		const min = this.options.excludeMin ? ']' : '[';
		const max = this.options.excludeMax ? '[' : ']';
		return `(${this.field} ${this.constructor.value} ${min}${this.operand.join(', ')}${max})`;
	}
}
