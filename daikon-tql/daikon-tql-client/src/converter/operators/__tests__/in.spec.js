import { In } from '..';

describe('in', () => {
	it('should create a new in operator', () => {
		const test = new In('f1', [666, 777]);

		expect(test.serialize()).toBe('(f1 in [666, 777])');
	});

	it('should accept multiple values', () => {
		const test = new In('f1', [555, 666, 777, 888, 999]);

		expect(test.serialize()).toBe('(f1 in [555, 666, 777, 888, 999])');
	});

	it('should wrap string value in simple quotes', () => {
		const test = new In('f1', ['666', '777']);

		expect(test.serialize()).toBe("(f1 in ['666', '777'])");
	});

	it('should accept operand with only one value', () => {
		const test = new In('f1', [666]);

		expect(test.serialize()).toBe('(f1 in [666])');
	});

	it('should accept operand with only one value not in an array', () => {
		const test = new In('f1', 666);

		expect(test.serialize()).toBe('(f1 in [666])');
	});
});
