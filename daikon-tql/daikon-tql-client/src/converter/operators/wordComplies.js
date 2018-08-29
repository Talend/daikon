import Operator from './operator';

/**
 * Class representing the WordComplies operator.
 * Will be serialized as follows : (field1 wordComplies 'Aaa')
 */
export default class WordComplies extends Operator {
	static value = 'wordComplies';
	static hasOperand = true;
	static allowEmpty = true;
}
