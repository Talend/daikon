import Query from './query';

const mapping = {
	contains: {
		operator: 'contains',
		getValues: node => node.args.phrase,
		getOptions: () => {},
	},
	exact: {
		operator: 'equal',
		getValues: node => node.args.phrase,
		getOptions: () => {},
	},
	inside_range: {
		operator: 'between',
		getValues: node => node.args.intervals,
		getOptions: () => {},
	},
	invalid_records: {
		operator: 'invalid',
		getValues: () => [],
		getOptions: () => {},
	},
	valid_records: {
		operator: 'valid',
		getValues: () => [],
		getOptions: () => {},
	},
	matches: {
		operator: 'complies',
		getValues: node => node.args.patterns,
		getOptions: () => {},
	},
	empty_records: {
		operator: 'empty',
		getValues: () => [],
		getOptions: () => {},
	},
	quality: {
		operator: 'quality',
		getValues: () => ['*'],
		getOptions: node => node.args,
	},
};

function buildSubQuery(values, operator, field, options) {
	const sub = new Query();

	values.reduce((acc, { value }, index) => {
		acc[operator](field, value, options);
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
			const field = filter.colId;
			const current = mapping[filter.type];
			const values = current.getValues(filter);
			const options = current.getOptions(filter);

			if (!values.length) {
				acc[current.operator](field, null, options);
			} else {
				acc.nest(buildSubQuery(values, current.operator, field, options));
			}

			return index < tree.length - 1 ? acc.and() : acc;
		}, query);

		return query;
	}
}
