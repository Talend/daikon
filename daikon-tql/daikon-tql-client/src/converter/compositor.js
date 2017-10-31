import ISerializable from './operators/iserializable';

export default class Compositor extends ISerializable {
	static and = 'and';
	static or = 'or';

	constructor(type) {
		super();
		if (type !== Compositor.and && type !== Compositor.or) {
			throw new Error('Unknown compositor');
		}

		this.type = type;
	}

	static get(type) {
		return new Compositor(type);
	}

	serialize() {
		return this.type;
	}
}
