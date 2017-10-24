import Operator from './operator';

export default class Empty extends Operator {
	static Value = 'is empty';
	static HasOperand = false;
}
