import Operator from './operator';

/**
 * Class representing the Unequal operator.
 * Will be serialized as follows : (field1 != 42)
 * Text values will be automatically wrapped : (field1 != 'Talend')
 */
export default class Unequal extends Operator {
	static value = '!=';
	static hasOperand = true;
}
