import Parser from '../parser';

const mock = [
	{
		type: 'contains',
		colId: '0001',
		colName: 'first_name',
		editable: true,
		args: {
			phrase: [
				{ value: 'euge', label: 'euge' },
				{ value: 'secondtestvalue', label: 'secondtestvalue' },
			],
		},
		badgeClass: 'contains',
		value: [{ value: 'euge', label: 'euge' }],
	},
	{
		type: 'inside_range',
		colId: '0000',
		colName: 'id',
		editable: false,
		args: {
			intervals: [
				{
					label: '[342,273 .. 542,874[',
					value: [342273, 542874],
					excludeMax: true,
					isMaxReached: false,
				},
				{
					label: '[13,456 .. 789,1011]',
					value: [13456, 7891011],
					isMaxReached: true,
				},
			],
			type: 'integer',
		},
		badgeClass: 'inside_range',
		value: [
			{
				label: '[342,273 .. 542,874[',
				value: [342273, 542874],
				isMaxReached: false,
			},
			{
				label: '[13,456 .. 789,1011]',
				value: [13456, 7891011],
				isMaxReached: false,
			},
		],
	},
	{
		type: 'invalid_records',
		editable: false,
		badgeClass: 'invalid_records',
		value: [{ label: 'rows with invalid values' }],
	},
	{
		type: 'valid_records',
		editable: false,
		badgeClass: 'valid_records',
		value: [{ label: 'rows with valid values' }],
	},
	{
		type: 'empty_records',
		editable: false,
		badgeClass: 'empty_records',
		value: [{ label: 'rows with invalid values' }],
	},
	{
		type: 'quality',
		editable: false,
		args: { invalid: true, empty: true },
		badgeClass: ' empty_records invalid_records',
		value: [{ label: 'rows with invalid or empty values' }],
	},
	{
		type: 'exact',
		colId: '0006',
		colName: 'country',
		editable: true,
		args: {
			phrase: [
				{ value: 'Indonesia', label: 'Indonesia' },
				{ value: 'Russia', label: 'Russia' },
			],
			caseSensitive: true,
		},
		removeFilterFn: null,
		badgeClass: 'exact',
		value: [
			{ value: 'Indonesia', label: 'Indonesia' },
			{ value: 'Russia', label: 'Russia' },
		],
	},
	{
		type: 'word_matches',
		colId: '0009',
		colName: 'departement_birth',
		editable: false,
		args: { patterns: [{ value: '[alnum]/[number]/[number]' }] },
		removeFilterFn: null,
		badgeClass: 'word_matches',
		value: [{ value: '[alnum]/[number]/[number]' }],
	},
	{
		type: 'not_equals',
		colId: '0009',
		colName: 'departement_birth',
		editable: false,
		args: { value: 2 },
	},
	{
		type: 'greater_than',
		colId: '0009',
		colName: 'departement_birth',
		editable: false,
		args: { value: 3 },
	},
	{
		type: 'greater_than_or_equals',
		colId: '0009',
		colName: 'departement_birth',
		editable: false,
		args: { value: 4 },
	},
	{
		type: 'less_than',
		colId: '0009',
		colName: 'departement_birth',
		editable: false,
		args: { value: 5 },
	},
	{
		type: 'less_than_or_equals',
		colId: '0009',
		colName: 'departement_birth',
		editable: false,
		args: { value: 6 },
	},
];

describe('Parser', () => {
	it('should parse a tree', () => {
		const query = Parser.parse(mock);

		expect(query.serialize()).toEqual(
			"((0001 contains 'euge') or (0001 contains 'secondtestvalue')) and ((0000 between [342273, 542874[) or (0000 between [13456, 7891011])) and (* is invalid) and (* is valid) and (* is empty) and ((* is empty) or (* is invalid)) and ((0006 = 'Indonesia') or (0006 = 'Russia')) and ((0009 wordComplies '[alnum]/[number]/[number]')) and ((0009 != 2)) and ((0009 > 3)) and ((0009 >= 4)) and ((0009 < 5)) and ((0009 <= 6))",
		);
	});
});
