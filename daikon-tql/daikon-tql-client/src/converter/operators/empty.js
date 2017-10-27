import Operator from './operator';

/**
 * Class representing the Empty operator.
 * Will be serialized as follows : (field1 is empty)
 */
export default class Empty extends Operator {
	static value = 'is empty';
	static hasOperand = false;
}
