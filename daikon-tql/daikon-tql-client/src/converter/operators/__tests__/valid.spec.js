import { Valid } from '../';

describe('valid', () => {
	it('should create a new valid operator', () => {
		const test = new Valid('f1');

		expect(test.field).toBe('f1');
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new Valid('f1');

		expect(test.serialize()).toBe('(f1 is valid)');
	});
});
