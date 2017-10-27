import Query from './query';

const mapping = {
	contains: {
		operator: 'contains',
		getValues: node => node.args.phrase,
	},
	exact: {
		operator: 'equal',
		getValues: node => node.args.phrase,
	},
	inside_range: {
		operator: 'between',
		getValues: node => node.args.intervals,
	},
	invalid_records: {
		operator: 'invalid',
		getValues: () => [],
	},
	valid_records: {
		operator: 'valid',
		getValues: () => [],
	},
	matches: {
		operator: 'complies',
		getValues: node => node.args.patterns,
	},
	empty_records: {
		operator: 'empty',
		getValues: () => [],
	},
	quality: {
		operator: 'quality',
		getValues: () => ['*'],
	},
};

function buildSubQuery(values, operator, field) {
	const sub = new Query();

	values.reduce((acc, { value }, index) => {
		acc[operator](field, value);
		return index < values.length - 1 ? acc.or() : acc;
	}, sub);

	return sub;
}

/**
 * Class representing the Parser.
 */
export default class Parser {
	/**
	 * Convert a Javascript-style filters tree to a serializable query.
	 * @param {object} tree - The Javascript-style filters tree.
	 * @return {Query} The serializable query.
	 */
	static parse(tree) {
		const query = new Query();

		tree.reduce((acc, filter, index) => {
			const current = mapping[filter.type];
			const values = current.getValues(filter);
			const field = filter.colId;

			if (!values.length) {
				acc[current.operator](field);
			} else {
				acc.nest(buildSubQuery(values, current.operator, field));
			}

			return index < tree.length - 1 ? acc.and() : acc;
		}, query);

		return query;
	}
}
