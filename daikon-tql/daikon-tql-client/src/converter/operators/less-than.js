import Operator from './operator';

/**
 * Class representing the Less Than operator.
 * Will be serialized as follows : (field1 < 42)
 */
export default class LessThan extends Operator {
	static value = '<';
	static hasOperand = true;
}
