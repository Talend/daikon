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
		operator: 'compliesTo',
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

function buildSubQuery(values, operator, column) {
	const sub = new Query();

	values.reduce((acc, value, index) => {
		acc[operator](column, value.value);
		return index < values.length - 1 ? acc.or() : acc;
	}, sub);

	return sub;
}

export default class Parser {
	static parse(tree) {
		const query = new Query();

		tree.reduce((acc, value, index) => {
			const current = mapping[value.type];
			const values = current.getValues(value);
			const column = value.colId;

			if (!values.length) {
				acc[current.operator](column);
			} else {
				acc.nest(buildSubQuery(values, current.operator, column));
			}

			return index < tree.length - 1 ? acc.and() : acc;
		}, query);

		return query;
	}
}
