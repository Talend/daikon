import Operator from './operator';
import { Empty, Invalid, Or } from './';

/**
 * Class representing the Quality operator.
 * Will be serialized as follows : ((field1 is empty) or (field1 is invalid))
 */
export default class Quality extends Operator {
	serialize() {
		return `((${this.field} ${Empty.value}) ${Or.value} (${this.field} ${Invalid.value}))`;
	}
}
