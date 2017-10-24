import Operator from './operator';

export default class Invalid extends Operator {
	static Value = 'is invalid';
	static HasOperand = false;
}
