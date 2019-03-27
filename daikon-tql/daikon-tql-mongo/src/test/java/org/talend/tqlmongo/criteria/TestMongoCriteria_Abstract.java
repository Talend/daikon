package org.talend.tqlmongo.criteria;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.talend.tql.TqlLexer;
import org.talend.tql.TqlParser;
import org.talend.tql.model.TqlElement;
import org.talend.tql.parser.TqlExpressionVisitor;
import org.talend.tqlmongo.ASTVisitor;

import com.github.fakemongo.Fongo;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.FongoDB;

/**
 * Created by gmzoughi on 06/07/16.
 */
public abstract class TestMongoCriteria_Abstract {

    private final static String DB_NAME = "tql-mongo";

    private final static String COLLECTION_NAME = "record";

    private final static Map<String, Double> RECORDS = Collections.unmodifiableMap(Stream
            .of(new AbstractMap.SimpleEntry<>("ghassen", 30d), new AbstractMap.SimpleEntry<>("Ghassen", 31.2d),
                    new AbstractMap.SimpleEntry<>("Benoit", 29d), new AbstractMap.SimpleEntry<>("Benoit 2eme", 28.8d),
                    new AbstractMap.SimpleEntry<>("+?'n$", 28.8d))
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));

    private static MongoTemplate mongoTemplate;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        DB db = new Fongo(DB_NAME).getDB(DB_NAME);
        mongoTemplate = new MongoTemplate(db.getMongo(), DB_NAME);
        insertData();
    }

    private void insertData() {
        RECORDS.forEach((name, age) -> {
            BasicDBObject document = new BasicDBObject();
            document.put("name", name);
            document.put("age", age);
            document.put("isGoodBoy", age % 2 == 0);
            mongoTemplate.insert(document, COLLECTION_NAME);
        });
    }

    protected Criteria doTest(String query) {
        ANTLRInputStream input = new ANTLRInputStream(query);
        TqlLexer lexer = new TqlLexer(input);
        TqlParser parser = new TqlParser(new CommonTokenStream(lexer));
        TqlParser.ExpressionContext expression = parser.expression();
        TqlElement tqlElement = expression.accept(new TqlExpressionVisitor());
        Object accept = tqlElement.accept(new ASTVisitor());
        return (Criteria) accept;
    }

    List<Record> getRecords(Criteria criteria) {
        Query q = new Query();
        if (mongoTemplate.getDb() instanceof FongoDB) {
            q.addCriteria(new Criteria() {

                @Override
                public DBObject getCriteriaObject() {
                    return replaceRegexRecursively(criteria.getCriteriaObject());
                }
            });
        } else
            q.addCriteria(criteria);
        return mongoTemplate.find(q, Record.class);
    }

    private DBObject replaceRegexRecursively(DBObject criteriaObject) {
        for (String key : criteriaObject.keySet()) {
            if (StringUtils.equals(key, "$regex")) {
                String newRegex = ((String) criteriaObject.get(key)).replaceAll("Han", "script=Han");
                criteriaObject.put(key, newRegex);
            } else if (criteriaObject.get(key) instanceof DBObject) {
                criteriaObject.put(key, replaceRegexRecursively((DBObject) criteriaObject.get(key)));
            }
        }
        return criteriaObject;
    }

    public class Record {

        private final String name;

        private final double age;

        public Record(String name, double age) {
            this.name = name;
            this.age = age;
        }

        String getName() {
            return name;
        }

        double getAge() {
            return age;
        }
    }
}
