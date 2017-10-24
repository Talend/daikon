export default class And {
	static Value = 'and';

	toTQL() {
		return ` ${this.constructor.Value} `;
	}
}
