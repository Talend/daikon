package org.talend.daikon.content;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = MOCK) // MOCK environment is important not to start tomcat (see TDKN-145)
public abstract class DeletableLoaderResourceTests {

    @Autowired
    protected DeletablePathResolver resolver;

}
