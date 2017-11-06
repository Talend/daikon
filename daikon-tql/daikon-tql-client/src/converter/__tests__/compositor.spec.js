import Compositor from '../compositor';

describe('Compositor', () => {
	it('should create an AND compositor', () => {
		const c = Compositor.get(Compositor.and);

		expect(c instanceof Compositor).toBe(true);
		expect(c.serialize()).toBe('and');
	});

	it('should create a OR compositor', () => {
		const c = Compositor.get(Compositor.or);

		expect(c instanceof Compositor).toBe(true);
		expect(c.serialize()).toBe('or');
	});

	it('should throw if the given compositor does not exist', () => {
		expect(() => {
			Compositor.get('héhé');
		}).toThrow('Unknown compositor');
	});
});
