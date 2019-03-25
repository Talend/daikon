import { Unequal } from '..';

describe('unequal', () => {
	it('should create a new unequal operator', () => {
		const test = new Unequal('f1', 666);

		expect(test.field).toBe('f1');
		expect(test.operand).toBe(666);
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new Unequal('f1', 666);

		expect(test.serialize()).toBe('(f1 != 666)');
	});

	it('should wrap strings', () => {
		const test = new Unequal('f1', 'Charles');

		expect(test.serialize()).toBe("(f1 != 'Charles')");
	});

	it('should not allow empty operand', () => {
		const test = new Unequal('f1');

		expect(() => {
			test.serialize();
		}).toThrow('!= does not allow empty.');
	});
});
