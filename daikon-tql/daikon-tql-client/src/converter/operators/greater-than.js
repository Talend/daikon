import Operator from './operator';

/**
 * Class representing the Greater Than operator.
 * Will be serialized as follows : (field1 > 42)
 */
export default class GreaterThan extends Operator {
	static value = '>';
	static hasOperand = true;
}
