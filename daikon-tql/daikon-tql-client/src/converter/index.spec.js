import Operation from './query';

describe('TEST TEMP QUERY', () => {
	it('should make a query', () => {
		const op = new Operation();

		op
			.Equal('f1', 1)
			.And()
			.Empty('f2')
			.And()
			.Contains('f3', 'yolo');

		console.log('[NC] op: ', op.toTQL());
	});
});
