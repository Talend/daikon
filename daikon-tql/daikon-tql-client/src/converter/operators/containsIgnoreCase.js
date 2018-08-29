import Operator from './operator';

/**
 * Class representing the ContainsIgnoreCase operator.
 * Will be serialized as follows : (field1 containsIgnoreCase 'alen')
 */
export default class ContainsIgnoreCase extends Operator {
	static value = 'containsIgnoreCase';
	static hasOperand = true;
	static allowEmpty = true;
}
