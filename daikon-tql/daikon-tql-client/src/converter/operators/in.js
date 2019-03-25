import Operator, {
	isDefined,
	wrap,
} from './operator';

/**
 * Class representing the In operator.
 * Will be serialized as follows : (field1 in [42, 76])
 */
export default class In extends Operator {
	static value = 'in';
	static hasOperand = true;

	serialize() {
		if (!isDefined(this.operand)) {
			throw new Error(`${this.constructor.value} does not allow empty.`);
		}

		const operandAsArray = (
			Array.isArray(this.operand)
				? this.operand
				: [this.operand]
		);

		const operandAsArrayFormatted = operandAsArray
			.map(value => wrap(value));

		return `(${this.field} ${this.constructor.value} [${operandAsArrayFormatted.join(', ')}])`;
	}
}
