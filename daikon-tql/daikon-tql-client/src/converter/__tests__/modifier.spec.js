import Modifier from '../modifier';
import { Equal } from '../operators';

describe('Modifier', () => {
	it('should create a NOT modifier', () => {
		const c = new Modifier(Modifier.not, new Equal('f1', 42));

		expect(c instanceof Modifier).toBe(true);
		expect(c.serialize()).toBe('not((f1 = 42))');
	});

	it('should throw if the given compositor does not exist', () => {
		expect(() => {
			const test = new Modifier('héhé');
			test.serialize();
		}).toThrow('Unknown modifier');
	});
});
