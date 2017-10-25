import { LessThan } from '../';

describe('less than', () => {
	it('should create a new less than operator', () => {
		const test = new LessThan('f1', 666);

		expect(test.field).toBe('f1');
		expect(test.operand).toBe(666);
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new LessThan('f1', 666);

		expect(test.serialize()).toBe('(f1 < 666)');
	});
});
