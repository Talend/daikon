import { Quality } from '../';

describe('quality', () => {
	it('should be convertible to a valid TQL query', () => {
		const test = new Quality(null, null, {
			empty: true,
			invalid: true,
		});

		expect(test.serialize()).toBe('((* is empty) or (* is invalid))');
	});

	it('should be serialized as a "is invalid" query', () => {
		const test = new Quality(null, null, {
			invalid: true,
		});

		expect(test.serialize()).toBe('(* is invalid)');
	});

	it('should be serialized as a "is empty" query', () => {
		const test = new Quality(null, null, {
			empty: true,
		});

		expect(test.serialize()).toBe('(* is empty)');
	});

	it('should be serialized as a "is valid" query', () => {
		const test = new Quality(null, null, {
			valid: true,
		});

		expect(test.serialize()).toBe('(* is valid)');
	});

	it('should throw if no parameters is specified', () => {
		const test = new Quality(null, null, {});

		expect(() => {
			test.serialize();
		}).toThrow('Invalid options given to quality operator.');
	});
});
