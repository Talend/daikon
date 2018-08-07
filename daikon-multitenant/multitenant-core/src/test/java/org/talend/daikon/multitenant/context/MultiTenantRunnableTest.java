package org.talend.daikon.multitenant.context;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.multitenant.provider.DefaultTenant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.talend.daikon.multitenant.context.MultiTenantRunnable.wrap;

public class MultiTenantRunnableTest {

    private TenancyContext context;

    @Before
    public void setUp() {
        context = TenancyContextHolder.createEmptyContext();
        context.setTenant(new DefaultTenant("tenant-1234", null));
        TenancyContextHolder.setContext(context);
    }

    @Test
    public void shouldGetTenancyContextForExecution() throws InterruptedException {
        // Given
        final TestRunnable testRunnable = new TestRunnable();
        final Runnable runnable = wrap(testRunnable);
        final Thread thread = new Thread(runnable);

        // When
        thread.start();
        thread.join();

        // Then
        assertEquals(context, testRunnable.getContext());
    }

    @Test
    public void shouldNotGetTenancyContextForExecution() throws InterruptedException {
        // Given
        final TestRunnable testRunnable = new TestRunnable();
        final Thread thread = new Thread(testRunnable);

        // When
        thread.start();
        thread.join();

        // Then
        assertNotEquals(context, testRunnable.getContext());
    }

    private static class TestRunnable implements Runnable {

        private TenancyContext context;

        @Override
        public void run() {
            context = TenancyContextHolder.getContext();
        }

        public TenancyContext getContext() {
            return context;
        }
    }

}