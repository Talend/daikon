package org.talend.daikon.multitenant.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;
import org.talend.daikon.multitenant.context.DefaultTenancyContext;
import org.talend.daikon.multitenant.provider.DefaultTenant;

/**
 * Test serialization of tenancy components
 * 
 * @author David Green (Tasktop Technologies Inc.)
 */
public class TenancySerializationTest {

    @Test
    public void testDefaultTenantSerializable() {
        DefaultTenant defaultTenant = new DefaultTenant("id", "data");
        assertSerializable(defaultTenant);
    }

    @Test
    public void testDefaultTenancyContextSerializable() {
        DefaultTenancyContext context = new DefaultTenancyContext();
        assertSerializable(context);
    }

    private void assertSerializable(Object o) {
        Assert.assertTrue(o instanceof Serializable);
        Object copy;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            objectOutputStream.writeObject(o);
            objectOutputStream.close();

            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
            copy = objectInputStream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Cannot serialize object " + o);
            throw new IllegalStateException(e);
        }
        Assert.assertNotNull(copy);
        Assert.assertEquals(o, copy);
    }
}
