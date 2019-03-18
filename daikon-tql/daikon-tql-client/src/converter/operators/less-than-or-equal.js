import Operator from './operator';

/**
 * Class representing the Less Than Or Equal operator.
 * Will be serialized as follows : (field1 <= 42)
 */
export default class LessThanOrEqual extends Operator {
	static value = '<=';
	static hasOperand = true;
}
