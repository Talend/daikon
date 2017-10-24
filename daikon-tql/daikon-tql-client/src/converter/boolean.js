import Operator from './operators/operator';

export default function (operator) {
	return (...operations) =>
		operations.reduce((acc, item) => {
			let tqlItem = item;
			const tqlAcc = acc instanceof Operator ? acc.toTQL() : acc;

			if (item instanceof Operator) {
				tqlItem = item.toTQL();
			} else {
				tqlItem = `(${tqlItem})`;
			}

			return acc && item ? `${tqlAcc} ${operator} ${tqlItem}` : tqlItem;
		});
}
