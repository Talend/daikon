import { CompliesTo } from '../';

describe('complies to', () => {
	it('should create a new complies to operator', () => {
		const test = new CompliesTo('f1', 666);

		expect(test.field).toBe('f1');
		expect(test.operand).toBe(666);
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new CompliesTo('f1', 666);

		expect(test.toTQL()).toBe('(f1 complies to 666)');
	});

	it('should wrap strings', () => {
		const test = new CompliesTo('f1', 'Charles');

		expect(test.toTQL()).toBe("(f1 complies to 'Charles')");
	});

	it('should handle an empty operand', () => {
		const test = new CompliesTo('f1', '');

		expect(test.toTQL()).toBe('(f1 is empty)');
	});
});
