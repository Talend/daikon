import { WordComplies } from '../';

describe('wordComplies', () => {
	it('should create a new wordComplies operator', () => {
		const test = new WordComplies('f1', 666);

		expect(test.field).toBe('f1');
		expect(test.operand).toBe(666);
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new WordComplies('f1', 666);

		expect(test.serialize()).toBe('(f1 wordComplies 666)');
	});

	it('should wrap strings', () => {
		const test = new WordComplies('f1', 'Charles');

		expect(test.serialize()).toBe("(f1 wordComplies 'Charles')");
	});

	it('should handle an empty operand', () => {
		const test = new WordComplies('f1', '');

		expect(test.serialize()).toBe('(f1 is empty)');
	});
});
