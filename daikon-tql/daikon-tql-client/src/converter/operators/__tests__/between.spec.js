import { Between } from '../';

describe('between', () => {
	it('should create a new between operator', () => {
		const test = new Between('f1', 666, 777);

		expect(test.field).toBe('f1');
		expect(test.operand).toEqual([666, 777]);
	});

	it('should be convertible to a valid TQL query even if values are passed as array', () => {
		const test = new Between('f1', [666, 777]);

		expect(test.serialize()).toBe('(f1 between [666, 777])');
	});

	it('should be convertible to a valid TQL query', () => {
		const test = new Between('f1', 666, 777);

		expect(test.serialize()).toBe('(f1 between [666, 777])');
	});
});
