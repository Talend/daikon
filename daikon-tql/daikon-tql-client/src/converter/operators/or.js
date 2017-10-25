import ISerializable from './serializable';

export default class Or extends ISerializable {
	static Value = 'or';

	serialize() {
		return ` ${this.constructor.Value} `;
	}
}
