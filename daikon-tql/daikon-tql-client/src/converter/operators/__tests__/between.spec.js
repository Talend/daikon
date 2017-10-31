import { Between } from '../';

describe('between', () => {
	it('should create a new between operator', () => {
		const test = new Between('f1', [666, 777]);

		expect(test.serialize()).toBe('(f1 between [666, 777])');
	});
});
