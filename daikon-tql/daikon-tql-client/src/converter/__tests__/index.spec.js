import { Equal, Contains, GreaterThan, Valid, Invalid } from '../operators';

import and from '../and';
import or from '../or';

describe('converter', () => {
	it('should be able to build complex queries', () => {
		const complex = or(
			new Equal('f1', 42),
			new Contains('f2', 666),
			new Invalid('f3'),
			and(
				new Equal('f4', 42),
				new Contains('f5', 666),
				new Invalid('f6'),
				or(
					new Equal('f7', 42),
					new Valid('f8'),
					new Contains('f9', 666),
					new GreaterThan('f10', 13),
				),
				new Valid('f11'),
				new GreaterThan('f12', 13),
			),
			new Valid('f13'),
			new GreaterThan('f14', 13),
		);

		expect(complex).toBe(
			'(f1 = 42) or (f2 contains 666) or (f3 is invalid) or ((f4 = 42) and (f5 contains 666) and (f6 is invalid) and ((f7 = 42) or (f8 is valid) or (f9 contains 666) or (f10 > 13)) and (f11 is valid) and (f12 > 13)) or (f13 is valid) or (f14 > 13)',
		);
	});
});
