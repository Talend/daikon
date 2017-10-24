import { Empty, Equal, Valid, Invalid, GreaterThan, LessThan, Contains } from '../operators';

import or from '../or';

describe('or', () => {
	it('should be able to perform a AND statements', () => {
		const complex = or(new Equal('f1', 42), new Empty('f2', 666), new Valid('f3'));

		expect(complex).toBe('(f1 = 42) or (f2 is empty) or (f3 is valid)');
	});

	it('should be able to nest multiple AND statements', () => {
		const complex = or(
			new Equal('f1', 42),
			new Empty('f2', 666),
			or(
				new Invalid('f3'),
				or(new LessThan('f4', 10), new Contains('f5', 'yolo')),
				new GreaterThan('f4', 76),
			),
			new Valid('f5'),
		);

		expect(complex).toBe(
			"(f1 = 42) or (f2 is empty) or ((f3 is invalid) or ((f4 < 10) or (f5 contains 'yolo')) or (f4 > 76)) or (f5 is valid)",
		);
	});
});
