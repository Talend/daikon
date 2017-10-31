import { Equal } from '../';

describe('equal', () => {
	it('should create a new equal operator', () => {
		const test = new Equal('f1', 666);

		expect(test.field).toBe('f1');
		expect(test.operand).toBe(666);
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new Equal('f1', 666);

		expect(test.serialize()).toBe('(f1 = 666)');
	});

	it('should wrap strings', () => {
		const test = new Equal('f1', 'Charles');

		expect(test.serialize()).toBe("(f1 = 'Charles')");
	});

	it('should handle an empty operand', () => {
		const test = new Equal('f1', '');

		expect(test.serialize()).toBe('(f1 is empty)');
	});
});
