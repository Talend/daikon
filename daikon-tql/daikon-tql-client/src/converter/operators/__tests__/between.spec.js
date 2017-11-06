import { Between } from '../';

describe('between', () => {
	it('should create a new between operator', () => {
		const test = new Between('f1', [666, 777]);

		expect(test.serialize()).toBe('(f1 between [666, 777])');
	});

	it('should exclude max value', () => {
		const test = new Between('f1', [666, 777], { excludeMax: true });

		expect(test.serialize()).toBe('(f1 between [666, 777[)');
	});

	it('should exclude min value', () => {
		const test = new Between('f1', [666, 777], { excludeMin: true });

		expect(test.serialize()).toBe('(f1 between ]666, 777])');
	});

	it('should exclude both', () => {
		const test = new Between('f1', [666, 777], { excludeMin: true, excludeMax: true });

		expect(test.serialize()).toBe('(f1 between ]666, 777[)');
	});
});
