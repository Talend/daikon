import { Or } from '../';

describe('or', () => {
	it('should create a new empty operator', () => {
		const test = new Or();

		expect(test.constructor.Value).toBe('or');
	});

	it('should be serializable to a valid TQL operator', () => {
		const test = new Or();

		expect(test.serialize()).toBe(' or ');
	});
});
