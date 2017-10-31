import Operator from './operator';

/**
 * Class representing the Contains operator.
 * Will be serialized as follows : (field1 contains 'alen')
 */
export default class Contains extends Operator {
	static value = 'contains';
	static hasOperand = true;
	static allowEmpty = true;
}
