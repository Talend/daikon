import * as operators from '../index';

describe('operators', () => {
	it('should export every operators', () => {
		expect(operators.And).toBeTruthy();
		expect(operators.Complies).toBeTruthy();
		expect(operators.Contains).toBeTruthy();
		expect(operators.Empty).toBeTruthy();
		expect(operators.Equal).toBeTruthy();
		expect(operators.GreaterThan).toBeTruthy();
		expect(operators.Invalid).toBeTruthy();
		expect(operators.LessThan).toBeTruthy();
		expect(operators.Or).toBeTruthy();
		expect(operators.Valid).toBeTruthy();
	});
});
