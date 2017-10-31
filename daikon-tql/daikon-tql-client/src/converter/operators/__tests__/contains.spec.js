import { Contains } from '../';

describe('contains', () => {
	it('should create a new contains operator', () => {
		const test = new Contains('f1', 666);

		expect(test.field).toBe('f1');
		expect(test.operand).toBe(666);
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new Contains('f1', 666);

		expect(test.serialize()).toBe('(f1 contains 666)');
	});

	it('should wrap strings', () => {
		const test = new Contains('f1', 'Charles');

		expect(test.serialize()).toBe("(f1 contains 'Charles')");
	});

	it('should handle an empty operand', () => {
		const test = new Contains('f1', '');

		expect(test.serialize()).toBe('(f1 is empty)');
	});
});
