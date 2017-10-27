/**
 * Interface to be implemented by the operators and queries
 */
export default class ISerializable {
	/* eslint-disable class-methods-use-this */
	serialize() {
		throw new Error('serialize() is not implemented.');
	}
	/* eslint-enable class-methods-use-this */
}
