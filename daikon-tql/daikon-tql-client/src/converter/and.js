export default function (operations) {
	return operations.reduce((acc, item) => (acc && item ? `${acc} and ${item}` : item));
}
