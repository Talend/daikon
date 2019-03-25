import Operator from './operator';

/**
 * Class representing the Greater Than Or Equal operator.
 * Will be serialized as follows : (field1 >= 42)
 */
export default class GreaterThanOrEqual extends Operator {
	static value = '>=';
	static hasOperand = true;
}
