import { Invalid } from '../';

describe('invalid', () => {
	it('should create a new invalid operator', () => {
		const test = new Invalid('f1');

		expect(test.field).toBe('f1');
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new Invalid('f1');

		expect(test.toTQL()).toBe('(f1 is invalid)');
	});
});
