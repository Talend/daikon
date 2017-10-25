import Operator from './operator';

export default class Between extends Operator {
	static Value = 'between';
	static HasOperand = true;

	serialize() {
		return `(${this.field} ${this.constructor.Value} [${this.operand.join(', ')}])`;
	}
}
