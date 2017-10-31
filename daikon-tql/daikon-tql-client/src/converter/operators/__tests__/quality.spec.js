import { Quality } from '../';

describe('quality', () => {
	it('should be convertible to a valid TQL query', () => {
		const test = new Quality(null, null, {
			empty: true,
			invalid: true,
		});

		expect(test.serialize()).toBe('((* is empty) or (* is invalid))');
	});
});
