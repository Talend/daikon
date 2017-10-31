import { Empty } from '../';

describe('empty', () => {
	it('should create a new empty operator', () => {
		const test = new Empty('f1');

		expect(test.field).toBe('f1');
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new Empty('f1');

		expect(test.serialize()).toBe('(f1 is empty)');
	});
});
