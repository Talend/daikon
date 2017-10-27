import Operator from './operator';

/**
 * Class representing the Complies operator.
 * Will be serialized as follows : (field1 complies 'Aaa')
 */
export default class Complies extends Operator {
	static value = 'complies';
	static hasOperand = true;
	static allowEmpty = true;
}
