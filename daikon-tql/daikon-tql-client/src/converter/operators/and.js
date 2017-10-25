import ISerializable from './serializable';

export default class And extends ISerializable {
	static Value = 'and';

	serialize() {
		return ` ${this.constructor.Value} `;
	}
}