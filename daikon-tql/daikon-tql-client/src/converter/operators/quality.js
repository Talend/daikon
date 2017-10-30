import Operator from './operator';
import { Empty, Invalid, Valid } from './';
import Compositor from '../compositor';

/**
 * Class representing the Quality operator.
 * Will be serialized as follows : ((field1 is empty) or (field1 is invalid))
 */
export default class Quality extends Operator {
	serialize() {
		const operations = [];

		if (this.options.empty) {
			operations.push(new Empty(this.field));
		}
		if (this.options.invalid) {
			operations.push(new Invalid(this.field));
		}
		if (this.options.valid) {
			operations.push(new Valid(this.field));
		}

		if (!operations.length) {
			throw new Error('Invalid options given to quality operator.');
		}

		return `(${operations.map(o => o.serialize()).join(` ${Compositor.or} `)})`;
	}
}
