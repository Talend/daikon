import Operator from './operator';

/**
 * Class representing the Valid operator.
 * Will be serialized as follows : (field1 is valid)
 */
export default class Valid extends Operator {
	static value = 'is valid';
	static hasOperand = false;
}
