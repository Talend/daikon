import { ContainsIgnoreCase } from '../';

describe('containsIgnoreCase', () => {
	it('should create a new containsIgnoreCase operator', () => {
		const test = new ContainsIgnoreCase('f1', 666);

		expect(test.field).toBe('f1');
		expect(test.operand).toBe(666);
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new ContainsIgnoreCase('f1', 666);

		expect(test.serialize()).toBe('(f1 containsIgnoreCase 666)');
	});

	it('should wrap strings', () => {
		const test = new ContainsIgnoreCase('f1', 'Charles');

		expect(test.serialize()).toBe("(f1 containsIgnoreCase 'Charles')");
	});

	it('should handle an empty operand', () => {
		const test = new ContainsIgnoreCase('f1', '');

		expect(test.serialize()).toBe('(f1 is empty)');
	});
});
