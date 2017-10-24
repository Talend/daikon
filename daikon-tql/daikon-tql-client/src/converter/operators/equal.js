import Operator from './operator';

export default class Equal extends Operator {
	static Value = '=';
	static HasOperand = true;
}
