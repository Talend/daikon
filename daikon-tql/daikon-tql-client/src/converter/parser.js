import Query from './query';

const mapping = {
	contains: {
		operator: 'contains',
		getValues: node => node.args.phrase,
		getOptions: () => {},
	},
	contains_ignore_case: {
		operator: 'containsIgnoreCase',
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
		getOptions: ({ excludeMax, excludeMin }) => ({ excludeMax, excludeMin }),
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
	word_matches: {
		operator: 'wordComplies',
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
		getValues: () => [],
		getOptions: value => value,
	},
	not_equals: {
		operator: 'unequal',
		getValues: node => [node.args],
		getOptions: () => {},
	},
	greater_than: {
		operator: 'greaterThan',
		getValues: node => [node.args],
		getOptions: () => {},
	},
	greater_than_or_equals: {
		operator: 'greaterThanOrEqual',
		getValues: node => [node.args],
		getOptions: () => {},
	},
	less_than: {
		operator: 'lessThan',
		getValues: node => [node.args],
		getOptions: () => {},
	},
	less_than_or_equals: {
		operator: 'lessThanOrEqual',
		getValues: node => [node.args],
		getOptions: () => {},
	},
};

function buildSubQuery(current, filter) {
	const values = current.getValues(filter);
	const operator = current.operator;

	return values.reduce((acc, item, index) => {
		const options = current.getOptions(item);
		acc[operator](filter.colId, item.value, options);
		return index < values.length - 1 ? acc.or() : acc;
	}, new Query());
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

			if (!values.length) {
				const opt = current.getOptions(filter.args);
				acc[current.operator](filter.colId, null, opt);
			} else {
				acc.nest(buildSubQuery(current, filter));
			}

			return index < tree.length - 1 ? acc.and() : acc;
		}, query);

		return query;
	}
}
