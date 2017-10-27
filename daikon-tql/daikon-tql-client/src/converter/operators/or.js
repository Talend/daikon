import ISerializable from './iserializable';

/**
 * Class representing the Or operator.
 * Will be serialized as follows : or
 */
export default class Or extends ISerializable {
	static value = 'or';

	serialize() {
		return ` ${this.constructor.value} `;
	}
}
