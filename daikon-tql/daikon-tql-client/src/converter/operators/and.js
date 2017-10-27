import ISerializable from './iserializable';

/**
 * Class representing the And operator.
 * Will be serialized as follows : and
 */
export default class And extends ISerializable {
	static value = 'and';

	serialize() {
		return ` ${this.constructor.value} `;
	}
}
