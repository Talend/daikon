var TqlLexer = require("./TqlLexer").TqlLexer;
var TqlParser = require("./TqlParser").TqlParser;
var TqlListener = require("./TqlParserListener").TqlParserListener;

var antlr4 = require("antlr4");

var parse = function(
    tql,
    onExactFilter,
    onContainsFilter,
    onContainsIgnoreCaseFilter,
    onCompliesFilter,
    onBetweenFilter,
    onEmptyFilter,
    onValidFilter,
    onInvalidFilter,
    onWordCompliesFilter,
    onNotEqualsFilter,
    onGreaterThanFilter,
    onGreaterThanOrEqualsFilter,
    onLessThanFilter,
    onLessThanOrEqualsFilter
) {
    var chars = new antlr4.InputStream(tql);
    var lexer = new TqlLexer(chars);
    var tokens = new antlr4.CommonTokenStream(lexer);
    var listener = new TqlListener();
    var parser = new TqlParser(tokens);
    var noop = function() {};
    var literalComparisonOperator = {
    "=": onExactFilter,
    "!=": onNotEqualsFilter,
    ">": onGreaterThanFilter,
    ">=": onGreaterThanOrEqualsFilter,
    "<": onLessThanFilter,
    "<=": onLessThanOrEqualsFilter
    };

    parser.buildParseTrees = true;

    // Define listeners
    listener.enterFieldIsEmpty = onEmptyFilter || noop;
    listener.enterFieldIsValid = onValidFilter || noop;
    listener.enterFieldIsInvalid = onInvalidFilter || noop;
    listener.enterFieldContains = onContainsFilter || noop;
    listener.enterFieldContainsIgnoreCase = onContainsIgnoreCaseFilter || noop;
    listener.enterFieldCompliesPattern = onCompliesFilter || noop;
    listener.enterFieldWordCompliesPattern = onWordCompliesFilter || noop;
    listener.enterFieldBetween = onBetweenFilter || noop;
    listener.enterLiteralComparison = function(ctx) {
        var filter = literalComparisonOperator[ctx.children[1].getText()];
        if (filter) {
          filter(ctx);
        }
    };

    // Bind listeners to tree
    antlr4.tree.ParseTreeWalker.DEFAULT.walk(listener, parser.expression());
};

export { TqlLexer, TqlParser, TqlListener, parse };
