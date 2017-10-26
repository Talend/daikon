import Operator from './operator';
import { Empty, Invalid, Or } from './';

// FIXME [NC]: this should be a query, not an operator.
export default class Quality extends Operator {
	serialize() {
		return `((${this.field} ${Empty.Value}) ${Or.Value} (${this.field} ${Invalid.Value}))`;
	}
}
