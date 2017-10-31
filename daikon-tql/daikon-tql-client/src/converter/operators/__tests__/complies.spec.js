import { Complies } from '../';

describe('complies', () => {
	it('should create a new complies operator', () => {
		const test = new Complies('f1', 666);

		expect(test.field).toBe('f1');
		expect(test.operand).toBe(666);
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new Complies('f1', 666);

		expect(test.serialize()).toBe('(f1 complies 666)');
	});

	it('should wrap strings', () => {
		const test = new Complies('f1', 'Charles');

		expect(test.serialize()).toBe("(f1 complies 'Charles')");
	});

	it('should handle an empty operand', () => {
		const test = new Complies('f1', '');

		expect(test.serialize()).toBe('(f1 is empty)');
	});
});
