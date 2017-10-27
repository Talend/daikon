import Operator from './operator';

/**
 * Class representing the Invalid operator.
 * Will be serialized as follows : (field1 is invalid)
 */
export default class Invalid extends Operator {
	static value = 'is invalid';
	static hasOperand = false;
}
