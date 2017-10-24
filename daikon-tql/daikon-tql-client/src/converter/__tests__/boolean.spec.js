import { Valid, Invalid } from '../operators';
import boolean from '../boolean';

const op1 = new Valid('v1');
const op2 = new Invalid('v2');

describe('boolean', () => {
	it('should create a new boolean operator based on the given operation', () => {
		const and = boolean('and');
		expect(and(op1, op2)).toBe('(v1 is valid) and (v2 is invalid)');

		const toto = boolean('toto');
		expect(toto(op1, op2)).toBe('(v1 is valid) toto (v2 is invalid)');
	});
});
