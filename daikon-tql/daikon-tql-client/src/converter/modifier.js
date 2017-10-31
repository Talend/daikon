import ISerializable from './operators/iserializable';

export default class Modifier extends ISerializable {
	static not = 'not';

	constructor(type, query) {
		super();
		if (type !== Modifier.not) {
			throw new Error('Unknown modifier');
		}

		this.type = type;
		this.query = query;
	}

	serialize() {
		return `${this.type}(${this.query.serialize()})`;
	}
}
