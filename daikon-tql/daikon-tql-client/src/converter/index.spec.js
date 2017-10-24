import Operation from './query';

describe('TEST TEMP QUERY', () => {
	it('should make a query', () => {
		const op = new Operation();

		op
			.equal('f1', 1)
			.and()
			.empty('f2')
			.and()
			.contains('f3', 'yolo')
			.toTQL();

		console.log('[NC] op: ', op);
	});
});
