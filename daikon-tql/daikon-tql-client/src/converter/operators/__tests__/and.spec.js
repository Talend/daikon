import { And } from '../';

describe('and', () => {
	it('should create a new empty operator', () => {
		const test = new And();

		expect(test.constructor.Value).toBe('and');
	});

	it('should be serializable to a valid TQL operator', () => {
		const test = new And();

		expect(test.serialize()).toBe(' and ');
	});
});
