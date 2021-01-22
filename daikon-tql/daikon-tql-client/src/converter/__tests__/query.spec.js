import Query from '../query';

describe('Query', () => {
	it('should be able to perform AND statements', () => {
		const q = new Query();

		q
			.equal('f1', 42)
			.and()
			.empty('f2')
			.and()
			.valid('f3');

		expect(q.serialize()).toBe('(f1 = 42) and (f2 is empty) and (f3 is valid)');
	});

	it('should be able to perform OR statements', () => {
		const q = new Query();

		q
			.contains('f1', 'heho')
			.or()
			.greaterThan('f2', 42)
			.or()
			.equal('f2', 666);

		expect(q.serialize()).toBe("(f1 contains 'heho') or (f2 > 42) or (f2 = 666)");
	});

	it('should be able to mix OR and AND statements', () => {
		const q = new Query();

		q
			.greaterThan('f2', 42)
			.and()
			.lessThan('f2', 666)
			.or()
			.equal('f2', 777);

		expect(q.serialize()).toBe('(f2 > 42) and (f2 < 666) or (f2 = 777)');
	});

	it('should be able to nest queries', () => {
		const q1 = new Query();
		const q2 = new Query();
		const q3 = new Query();
		const q4 = new Query();

		q2
			.equal('q2f1', 76)
			.or()
			.equal('q2f2', 77);

		q3
			.equal('q3f1', 78)
			.and()
			.equal('q3f2', 79);

		q4
			.equal('q4f1', 80)
			.and()
			.equal('q4f2', 81);

		q1
			.nest(q2)
			.and()
			.greaterThan('f2', 42)
			.and()
			.lessThan('f2', 666)
			.or()
			.nest(q3)
			.or()
			.equal('f2', 777)
			.and()
			.nest(q4);

		expect(q1.serialize()).toBe(
			'((q2f1 = 76) or (q2f2 = 77)) and (f2 > 42) and (f2 < 666) or ((q3f1 = 78) and (q3f2 = 79)) or (f2 = 777) and ((q4f1 = 80) and (q4f2 = 81))',
		);
	});

	it('should throw if user tries to nest a query without AND or OR statement before', () => {
		const q1 = new Query();
		const q2 = new Query();
		const q3 = new Query();

		q2.equal('q2f1', 76);

		q3.equal('q3f1', 78);

		expect(() => {
			q1
				.greaterThan('f2', 42)
				.nest(q2)
				.and()
				.lessThan('f2', 666);
		}).toThrow("You can't nest a query if there is no AND or OR statement before.");
	});

	it('should throw if user tries to continue a query without AND or OR after a nest()', () => {
		const q1 = new Query();
		const q2 = new Query();
		const q3 = new Query();

		q2.equal('q2f1', 76);

		q3.equal('q3f1', 78);

		expect(() => {
			q1
				.greaterThan('f2', 42)
				.and()
				.nest(q2)
				.lessThan('f2', 666);
		}).toThrow('Only AND or OR operators are allowed after a query.');
	});

	it('should modify the query', () => {
		const q1 = new Query();
		const q2 = new Query();
		const q3 = new Query();

		q2
			.equal('q2f1', 76)
			.or()
			.greaterThan('q2f2', 666);

		q3.equal('q3f1', 78);

		q1
			.greaterThan('f2', 42)
			.or()
			.not(q2)
			.and()
			.lessThan('f2', 666);

		expect(q1.serialize()).toEqual(
			'(f2 > 42) or not((q2f1 = 76) or (q2f2 > 666)) and (f2 < 666)',
		);
	});
	it('should be able to perform AND statements', () => {
		const q = new Query();

		q.equal('f1', "should ' esca ' pe");

		expect(q.serialize()).toBe("(f1 = 'should \\' esca \\' pe')");
	});
});
