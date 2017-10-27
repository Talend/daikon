import Operator from './operator';

/**
 * Class representing the Equal operator.
 * Will be serialized as follows : (field1 = 42)
 * Text values will be automatically wrapped : (field1 = 'Talend')
 */
export default class Equal extends Operator {
	static value = '=';
	static hasOperand = true;
	static allowEmpty = true;
}
