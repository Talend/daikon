package org.talend.daikon.content.journal;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@DataMongoTest
@ContextConfiguration
public class MongoResourceJournalResolverTest {

    @Autowired
    private MongoResourceJournalResolver resolver;

    /** Spring MongoDB template. */
    @Autowired
    private MongoResourceJournalRepository repository;

    @Configuration
    @ComponentScan("org.talend.daikon.content.journal")
    public static class SpringConfig {

    }

    @Before
    public void initData() {
        repository.deleteAll();
        repository.save(new ResourceJournalEntry("location1.1"));
        repository.save(new ResourceJournalEntry("location1.2"));
        repository.save(new ResourceJournalEntry("location1.3"));
        repository.save(new ResourceJournalEntry("location2.1"));
        repository.save(new ResourceJournalEntry("location2.2"));
    }

    @After
    public void cleanData() {
        repository.deleteAll();
    }

    @Test
    public void testContext() {
        Assert.assertNotNull(resolver);
        Assert.assertNotNull(repository);
    }

    @Test
    public void testClear() {

        resolver.clear("location1");

        Assert.assertEquals("Location 1.1 should not exist anymore", 0L, repository.countByName("location1.1"));
        Assert.assertEquals("Location 1.2 should not exist anymore", 0L, repository.countByName("location1.2"));
        Assert.assertEquals("Location 1.3 should not exist anymore", 0L, repository.countByName("location1.3"));
        Assert.assertEquals("Location 2.1 should still exist anymore", 1L, repository.countByName("location2.1"));
        Assert.assertEquals("Location 2.3 should still exist anymore", 1L, repository.countByName("location2.2"));
    }

    @Test
    public void testExist() {
        Assert.assertTrue(resolver.exist("location1.1"));
        Assert.assertFalse(resolver.exist("location1.5"));
    }

    @Test
    public void testMatches() throws IOException {

        List<String> listLocation = resolver.matches("location1").collect(Collectors.toList());

        Assert.assertEquals("Size of the list should be equals", 3, listLocation.size());
        for (String location : listLocation) {
            Assert.assertEquals("Location should start by location1", 0, location.indexOf("location1"));
        }
    }

    @Test
    public void testAdd() {
        long nbLocation = repository.count();
        resolver.add("location3.0");

        Assert.assertEquals("Nb location should be equals", nbLocation + 1, repository.count());
    }

    @Test
    public void testRemove() {
        long nbLocation = repository.count();

        resolver.remove("location2.2");

        Assert.assertEquals("Nb location should be equals", nbLocation - 1, repository.count());
    }

    @Test
    public void testMove() {

        Assert.assertTrue(resolver.exist("location1.1"));
        Assert.assertFalse(resolver.exist("location1.5"));

        resolver.move("location1.1", "location1.5");

        Assert.assertTrue(resolver.exist("location1.5"));
        Assert.assertFalse(resolver.exist("location1.1"));
    }
}
