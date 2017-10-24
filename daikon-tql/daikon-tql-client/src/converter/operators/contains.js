import Operator from './operator';

export default class Contains extends Operator {
	static Value = 'contains';
	static HasOperand = true;
}
