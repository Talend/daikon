import { GreaterThan } from '../';

describe('greater than', () => {
	it('should create a new greater than operator', () => {
		const test = new GreaterThan('f1', 666);

		expect(test.field).toBe('f1');
		expect(test.operand).toBe(666);
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new GreaterThan('f1', 666);

		expect(test.toTQL()).toBe('(f1 > 666)');
	});
});
