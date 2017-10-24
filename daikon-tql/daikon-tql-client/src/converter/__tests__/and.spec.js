import { Empty, Equal, Valid, Invalid, GreaterThan, LessThan, Contains } from '../operators';

import and from '../and';

describe('and', () => {
	it('should be able to perform a AND statements', () => {
		const complex = and(new Equal('f1', 42), new Empty('f2', 666), new Valid('f3'));

		expect(complex).toBe('(f1 = 42) and (f2 is empty) and (f3 is valid)');
	});

	it('should be able to nest multiple AND statements', () => {
		const complex = and(
			new Equal('f1', 42),
			new Empty('f2', 666),
			and(
				new Invalid('f3'),
				and(new LessThan('f4', 10), new Contains('f5', 'yolo')),
				new GreaterThan('f4', 76),
			),
			new Valid('f5'),
		);

		expect(complex).toBe(
			"(f1 = 42) and (f2 is empty) and ((f3 is invalid) and ((f4 < 10) and (f5 contains 'yolo')) and (f4 > 76)) and (f5 is valid)",
		);
	});
});
