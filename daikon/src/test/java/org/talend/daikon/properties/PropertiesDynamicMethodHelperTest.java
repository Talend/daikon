// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.runtime.RuntimeContext;
import org.talend.daikon.properties.service.Repository;

/**
 * Unit-tests for {@link PropertiesDynamicMethodHelper}
 * 
 * Client code should call <code>isCall*()<code> methods of {@link Form} and {@link Widget} classes to discover whether it is
 * allowed to call callbacks.
 * There are 4 cases for each callback:
 * <ol>
 * <li>
 * Properties implementation has no callbacks. isCall() should return false. Exception will be thrown if client call callback
 * </li>
 * <li>
 * Properties implementation has callback without RuntimeContext parameter (old callback). This is current implementation.
 * It should work as before. isCall() should return true. Old callback may be called.
 * </li>
 * <li>
 * Properties implementation has callback with RuntimeContext parameter (new callback), but has no old callback.
 * It breaks current implementation as product may call service without passing RuntimeContext. So, it should be prohibited.
 * isCall() should return false.
 * Exception should be thrown, when client calls the callback.
 * </li>
 * <li>
 * Properties has both callbacks. This is correct implementation to support new feature. isCall() should return true. Product may
 * pass RuntimeContext argument.
 * Then new callback will be called. If Product doesn't pass RuntimeContext argument, then old callback should be called.
 * </li>
 * </ol>
 * 
 */
public class PropertiesDynamicMethodHelperTest {

    static class PropertiesWithoutCallbacks extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        @SuppressWarnings("rawtypes")
        public ReferenceProperties ref = new ReferenceProperties<>("ref", "AnyDefinitionName");

        public PropertiesWithoutCallbacks() {
            super("test");
        }

        /**
         * method with package visibility which won't be found
         */
        void beforeSomeProperty() {
            // This is test method. Implementation is empty intentionally
        }

        /**
         * method with private visibility which won't be found
         */
        @SuppressWarnings("unused")
        private void validateAnotherProperty() {
            // This is test method. Implementation is empty intentionally
        }

    }

    static class PropertiesWithOldCallbacks extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        @SuppressWarnings("rawtypes")
        public ReferenceProperties ref = new ReferenceProperties<>("ref", "AnyDefinitionName");

        public PropertiesWithOldCallbacks() {
            super("test");
        }

        public void beforeProperty() {
            // This is test method. Implementation is empty intentionally
        }

        public void validateProperty() {
            // This is test method. Implementation is empty intentionally
        }

        public void afterProperty() {
            // This is test method. Implementation is empty intentionally
        }

        public void beforeFormPresentMain() {
            // This is test method. Implementation is empty intentionally
        }

        public void afterFormBackMain() {
            // This is test method. Implementation is empty intentionally
        }

        public void afterFormNextMain() {
            // This is test method. Implementation is empty intentionally
        }

        public void afterRef() {
            // This is test method. Implementation is empty intentionally
        }

        @SuppressWarnings("rawtypes")
        public void afterFormFinishMain(Repository repository) {
            // This is test method. Implementation is empty intentionally
        }

    }

    static class PropertiesWithRuntimeContextCallbacks extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        @SuppressWarnings("rawtypes")
        public ReferenceProperties ref = new ReferenceProperties<>("ref", "AnyDefinitionName");

        public PropertiesWithRuntimeContextCallbacks() {
            super("test");
        }

        public void beforeProperty(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void validateProperty(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void afterProperty(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void beforeFormPresentMain(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void afterFormBackMain(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void afterFormNextMain(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        @SuppressWarnings("rawtypes")
        public void afterFormFinishMain(Repository repository, RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void afterRef(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }
    }

    static class PropertiesWithBothCallbacks extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        @SuppressWarnings("rawtypes")
        public ReferenceProperties ref = new ReferenceProperties<>("ref", "AnyDefinitionName");

        public PropertiesWithBothCallbacks() {
            super("test");
        }

        public void beforeProperty() {
            // This is test method. Implementation is empty intentionally
        }

        public void beforeProperty(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void validateProperty() {
            // This is test method. Implementation is empty intentionally
        }

        public void validateProperty(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void afterProperty() {
            // This is test method. Implementation is empty intentionally
        }

        public void afterProperty(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void beforeFormPresentMain() {
            // This is test method. Implementation is empty intentionally
        }

        public void beforeFormPresentMain(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void afterFormBackMain() {
            // This is test method. Implementation is empty intentionally
        }

        public void afterFormBackMain(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void afterFormNextMain() {
            // This is test method. Implementation is empty intentionally
        }

        public void afterFormNextMain(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        @SuppressWarnings("rawtypes")
        public void afterFormFinishMain(Repository repository) {
            // This is test method. Implementation is empty intentionally
        }

        @SuppressWarnings("rawtypes")
        public void afterFormFinishMain(Repository repository, RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }

        public void afterRef() {
            // This is test method. Implementation is empty intentionally
        }

        public void afterRef(RuntimeContext context) {
            // This is test method. Implementation is empty intentionally
        }
    }

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFindMethodNullPropertyName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The ComponentService was used to access a property with a null(or empty) property name. Type:");

        PropertiesWithBothCallbacks props = new PropertiesWithBothCallbacks();
        PropertiesDynamicMethodHelper.findMethod(props, Properties.METHOD_AFTER, null, true);
    }

    @Test
    public void testFindMethodNullObject() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Instance whose method is being searched for should not be null");
        PropertiesDynamicMethodHelper.findMethod(null, Properties.METHOD_AFTER, "property", true);
    }

    @Test
    public void testFindMethodUnknownTypeRequired() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: unknownTriggerTypeProperty not found");
        PropertiesWithBothCallbacks props = new PropertiesWithBothCallbacks();
        PropertiesDynamicMethodHelper.findMethod(props, "unknownTriggerType", "property", true);
    }

    @Test
    public void testFindMethodUnknownTypeNotRequired() {
        PropertiesWithBothCallbacks props = new PropertiesWithBothCallbacks();
        Method method = PropertiesDynamicMethodHelper.findMethod(props, "unknownTriggerType", "property", false);
        Assert.assertNull(method);
    }

    /**
     * Asserts public methods are found
     */
    @Test
    public void testFindMethodPublic() {
        String expectedMethodDefinition = "public void org.talend.daikon.properties.PropertiesDynamicMethodHelperTest$PropertiesWithBothCallbacks.afterProperty()";

        PropertiesWithBothCallbacks props = new PropertiesWithBothCallbacks();
        Method method = PropertiesDynamicMethodHelper.findMethod(props, Properties.METHOD_AFTER, "property", false);
        Assert.assertEquals(expectedMethodDefinition, method.toString());
    }

    /**
     * Asserts public method with parameter is found
     */
    @Test
    public void testFindMethodPublicWithParams() {
        String expectedMethodDefinition = "public void org.talend.daikon.properties.PropertiesDynamicMethodHelperTest$PropertiesWithBothCallbacks.afterProperty("
                + "org.talend.daikon.properties.runtime.RuntimeContext)";

        PropertiesWithBothCallbacks props = new PropertiesWithBothCallbacks();
        Method method = PropertiesDynamicMethodHelper.findMethod(props, Properties.METHOD_AFTER, "property", false,
                RuntimeContext.class);
        Assert.assertEquals(expectedMethodDefinition, method.toString());
    }

    /**
     * Asserts that package visible methods are not found
     */
    @Test
    public void testFindMethodPackage() {
        PropertiesWithoutCallbacks props = new PropertiesWithoutCallbacks();
        Method method = PropertiesDynamicMethodHelper.findMethod(props, Properties.METHOD_BEFORE, "someProperty", false);
        Assert.assertNull(method);
    }

    /**
     * Asserts that private visible methods are not found
     */
    @Test
    public void testFindMethodPrivate() {
        PropertiesWithoutCallbacks props = new PropertiesWithoutCallbacks();
        Method method = PropertiesDynamicMethodHelper.findMethod(props, Properties.METHOD_VALIDATE, "anotherProperty", false);
        Assert.assertNull(method);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormBackNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormBackMain not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        PropertiesDynamicMethodHelper.afterFormBack(withoutCallbacks, Form.MAIN);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormBackNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormBackMain not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        PropertiesDynamicMethodHelper.afterFormBack(withNewCallbacks, Form.MAIN);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormBackOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        PropertiesDynamicMethodHelper.afterFormBack(withOldCallbacks, Form.MAIN);
        verify(withOldCallbacks).afterFormBackMain();
    }

    /**
     * Asserts that callback without parameters is called even if {@link Properties} class has both kinds of callbacks
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormBackBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        PropertiesDynamicMethodHelper.afterFormBack(withBothCallbacks, Form.MAIN);
        verify(withBothCallbacks).afterFormBackMain();
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormBackRuntimeContextNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormBackMain not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormBack(withoutCallbacks, Form.MAIN, context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormBackRuntimeContextNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormBackMain not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormBack(withNewCallbacks, Form.MAIN, context);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormBackRuntimeContextOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormBack(withOldCallbacks, Form.MAIN, context);
        verify(withOldCallbacks).afterFormBackMain();
    }

    /**
     * Asserts that callback with {@link RuntimeContext} parameter is called, when {@link Properties} class has both kinds of
     * callbacks
     * and product passes {@link RuntimeContext} argument to the method
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormBackRuntimeContextBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormBack(withBothCallbacks, Form.MAIN, context);
        verify(withBothCallbacks).afterFormBackMain(context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormFinishkNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormFinishMain not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        Repository repository = mock(Repository.class);
        PropertiesDynamicMethodHelper.afterFormFinish(withoutCallbacks, Form.MAIN, repository);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormFinishNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormFinishMain not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        Repository repository = mock(Repository.class);
        PropertiesDynamicMethodHelper.afterFormFinish(withNewCallbacks, Form.MAIN, repository);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormFinishOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        Repository repository = mock(Repository.class);
        PropertiesDynamicMethodHelper.afterFormFinish(withOldCallbacks, Form.MAIN, repository);
        verify(withOldCallbacks).afterFormFinishMain(repository);
    }

    /**
     * Asserts that callback without parameters is called even if {@link Properties} class has both kinds of callbacks
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormFinishBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        Repository repository = mock(Repository.class);
        PropertiesDynamicMethodHelper.afterFormFinish(withBothCallbacks, Form.MAIN, repository);
        verify(withBothCallbacks).afterFormFinishMain(repository);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormFinishRuntimeContextNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormFinishMain not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        Repository repository = mock(Repository.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormFinish(withoutCallbacks, Form.MAIN, repository, context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormFinishRuntimeContextNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormFinishMain not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        Repository repository = mock(Repository.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormFinish(withNewCallbacks, Form.MAIN, repository, context);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormFinishRuntimeContextOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        Repository repository = mock(Repository.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormFinish(withOldCallbacks, Form.MAIN, repository, context);
        verify(withOldCallbacks).afterFormFinishMain(repository);
    }

    /**
     * Asserts that callback with {@link RuntimeContext} parameter is called, when {@link Properties} class has both kinds of
     * callbacks
     * and product passes {@link RuntimeContext} argument to the method
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormFinishRuntimeContextBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        Repository repository = mock(Repository.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormFinish(withBothCallbacks, Form.MAIN, repository, context);
        verify(withBothCallbacks).afterFormFinishMain(repository, context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormNextNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormNextMain not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        PropertiesDynamicMethodHelper.afterFormNext(withoutCallbacks, Form.MAIN);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormNextNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormNextMain not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        PropertiesDynamicMethodHelper.afterFormNext(withNewCallbacks, Form.MAIN);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormNextOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        PropertiesDynamicMethodHelper.afterFormNext(withOldCallbacks, Form.MAIN);
        verify(withOldCallbacks).afterFormNextMain();
    }

    /**
     * Asserts that callback without parameters is called even if {@link Properties} class has both kinds of callbacks
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormNextBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        PropertiesDynamicMethodHelper.afterFormNext(withBothCallbacks, Form.MAIN);
        verify(withBothCallbacks).afterFormNextMain();
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormNextRuntimeContextNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormNextMain not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormNext(withoutCallbacks, Form.MAIN, context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormNextRuntimeContextNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterFormNextMain not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormNext(withNewCallbacks, Form.MAIN, context);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormNextRuntimeContextOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormNext(withOldCallbacks, Form.MAIN, context);
        verify(withOldCallbacks).afterFormNextMain();
    }

    /**
     * Asserts that callback with {@link RuntimeContext} parameter is called, when {@link Properties} class has both kinds of
     * callbacks
     * and product passes {@link RuntimeContext} argument to the method
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterFormNextRuntimeContextBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterFormNext(withBothCallbacks, Form.MAIN, context);
        verify(withBothCallbacks).afterFormNextMain(context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterPropertyNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterProperty not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        PropertiesDynamicMethodHelper.afterProperty(withoutCallbacks, "property");
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterPropertyNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterProperty not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        PropertiesDynamicMethodHelper.afterProperty(withNewCallbacks, "property");
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterPropertyOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        PropertiesDynamicMethodHelper.afterProperty(withOldCallbacks, "property");
        verify(withOldCallbacks).afterProperty();
    }

    /**
     * Asserts that callback without parameters is called even if {@link Properties} class has both kinds of callbacks
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterPropertyBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        PropertiesDynamicMethodHelper.afterProperty(withBothCallbacks, "property");
        verify(withBothCallbacks).afterProperty();
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterPropertyRuntimeContextNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterProperty not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterProperty(withoutCallbacks, "property", context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterPropertyRuntimeContextNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: afterProperty not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterProperty(withNewCallbacks, "property", context);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterPropertyRuntimeContextOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterProperty(withOldCallbacks, "property", context);
        verify(withOldCallbacks).afterProperty();
    }

    /**
     * Asserts that callback with {@link RuntimeContext} parameter is called, when {@link Properties} class has both kinds of
     * callbacks
     * and product passes {@link RuntimeContext} argument to the method
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterPropertyRuntimeContextBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterProperty(withBothCallbacks, "property", context);
        verify(withBothCallbacks).afterProperty(context);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterReferenceOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = spy(PropertiesWithOldCallbacks.class);
        PropertiesDynamicMethodHelper.afterReference(withOldCallbacks, withOldCallbacks.ref);
        verify(withOldCallbacks).afterRef();
    }

    /**
     * Asserts that callback without parameters is called even if {@link Properties} class has both kinds of callbacks
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterReferenceBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = spy(PropertiesWithBothCallbacks.class);
        PropertiesDynamicMethodHelper.afterReference(withBothCallbacks, withBothCallbacks.ref);
        verify(withBothCallbacks).afterRef();
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterReferenceRuntimeContextOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = spy(PropertiesWithOldCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterReference(withOldCallbacks, withOldCallbacks.ref, context);
        verify(withOldCallbacks).afterRef();
    }

    /**
     * Asserts that callback with {@link RuntimeContext} parameter is called, when {@link Properties} class has both kinds of
     * callbacks
     * and product passes {@link RuntimeContext} argument to the method
     * 
     * @throws Throwable
     */
    @Test
    public void testAfterReferenceRuntimeContextBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = spy(PropertiesWithBothCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.afterReference(withBothCallbacks, withBothCallbacks.ref, context);
        verify(withBothCallbacks).afterRef(context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforeFormPresentNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeFormPresentMain not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        PropertiesDynamicMethodHelper.beforeFormPresent(withoutCallbacks, Form.MAIN);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforeFormPresentNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeFormPresentMain not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        PropertiesDynamicMethodHelper.beforeFormPresent(withNewCallbacks, Form.MAIN);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforeFormPresentOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        PropertiesDynamicMethodHelper.beforeFormPresent(withOldCallbacks, Form.MAIN);
        verify(withOldCallbacks).beforeFormPresentMain();
    }

    /**
     * Asserts that callback without parameters is called even if {@link Properties} class has both kinds of callbacks
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforeFormPresentBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        PropertiesDynamicMethodHelper.beforeFormPresent(withBothCallbacks, Form.MAIN);
        verify(withBothCallbacks).beforeFormPresentMain();
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforeFormPresentRuntimeContextNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeFormPresentMain not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforeFormPresent(withoutCallbacks, Form.MAIN, context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforeFormPresentRuntimeContextNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeFormPresentMain not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforeFormPresent(withNewCallbacks, Form.MAIN, context);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforeFormPresentRuntimeContextOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforeFormPresent(withOldCallbacks, Form.MAIN, context);
        verify(withOldCallbacks).beforeFormPresentMain();
    }

    /**
     * Asserts that callback with {@link RuntimeContext} parameter is called, when {@link Properties} class has both kinds of
     * callbacks
     * and product passes {@link RuntimeContext} argument to the method
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforeFormPresentRuntimeContextBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforeFormPresent(withBothCallbacks, Form.MAIN, context);
        verify(withBothCallbacks).beforeFormPresentMain(context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyActivateNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeProperty not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        PropertiesDynamicMethodHelper.beforePropertyActivate(withoutCallbacks, "property");
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyActivateNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeProperty not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        PropertiesDynamicMethodHelper.beforePropertyActivate(withNewCallbacks, "property");
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyActivateOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        PropertiesDynamicMethodHelper.beforePropertyActivate(withOldCallbacks, "property");
        verify(withOldCallbacks).beforeProperty();
    }

    /**
     * Asserts that callback without parameters is called even if {@link Properties} class has both kinds of callbacks
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyActivateBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        PropertiesDynamicMethodHelper.beforePropertyActivate(withBothCallbacks, "property");
        verify(withBothCallbacks).beforeProperty();
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyActivateRuntimeContextNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeProperty not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforePropertyActivate(withoutCallbacks, "property", context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyActivateRuntimeContextNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeProperty not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforePropertyActivate(withNewCallbacks, "property", context);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyActivateRuntimeContextOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforePropertyActivate(withOldCallbacks, "property", context);
        verify(withOldCallbacks).beforeProperty();
    }

    /**
     * Asserts that callback with {@link RuntimeContext} parameter is called, when {@link Properties} class has both kinds of
     * callbacks
     * and product passes {@link RuntimeContext} argument to the method
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyActivateRuntimeContextBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforePropertyActivate(withBothCallbacks, "property", context);
        verify(withBothCallbacks).beforeProperty(context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyPresentNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeProperty not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        PropertiesDynamicMethodHelper.beforePropertyPresent(withoutCallbacks, "property");
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyPresentNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeProperty not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        PropertiesDynamicMethodHelper.beforePropertyPresent(withNewCallbacks, "property");
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyPresentOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        PropertiesDynamicMethodHelper.beforePropertyPresent(withOldCallbacks, "property");
        verify(withOldCallbacks).beforeProperty();
    }

    /**
     * Asserts that callback without parameters is called even if {@link Properties} class has both kinds of callbacks
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyPresentBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        PropertiesDynamicMethodHelper.beforePropertyPresent(withBothCallbacks, "property");
        verify(withBothCallbacks).beforeProperty();
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyPresentRuntimeContextNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeProperty not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforePropertyPresent(withoutCallbacks, "property", context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyPresentRuntimeContextNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: beforeProperty not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforePropertyPresent(withNewCallbacks, "property", context);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyPresentRuntimeContextOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforePropertyPresent(withOldCallbacks, "property", context);
        verify(withOldCallbacks).beforeProperty();
    }

    /**
     * Asserts that callback with {@link RuntimeContext} parameter is called, when {@link Properties} class has both kinds of
     * callbacks
     * and product passes {@link RuntimeContext} argument to the method
     * 
     * @throws Throwable
     */
    @Test
    public void testBeforePropertyPresentRuntimeContextBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.beforePropertyPresent(withBothCallbacks, "property", context);
        verify(withBothCallbacks).beforeProperty(context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testValidatePropertyNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: validateProperty not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        PropertiesDynamicMethodHelper.validateProperty(withoutCallbacks, "property");
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testValidatePropertyNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: validateProperty not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        PropertiesDynamicMethodHelper.validateProperty(withNewCallbacks, "property");
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testValidatePropertyOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        PropertiesDynamicMethodHelper.validateProperty(withOldCallbacks, "property");
        verify(withOldCallbacks).validateProperty();
    }

    /**
     * Asserts that callback without parameters is called even if {@link Properties} class has both kinds of callbacks
     * 
     * @throws Throwable
     */
    @Test
    public void testValidatePropertyBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        PropertiesDynamicMethodHelper.validateProperty(withBothCallbacks, "property");
        verify(withBothCallbacks).validateProperty();
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has no triggers at all
     * 
     * @throws Throwable
     */
    @Test
    public void testValidatePropertyRuntimeContextNoCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: validateProperty not found");
        PropertiesWithoutCallbacks withoutCallbacks = mock(PropertiesWithoutCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.validateProperty(withoutCallbacks, "property", context);
    }

    /**
     * Asserts that method throws {@link IllegalArgumentException}, when {@link Properties} class has callback with
     * {@link RuntimeContext} parameter, but has no callback without parameters
     * 
     * @throws Throwable
     */
    @Test
    public void testValidatePropertyRuntimeContextNewCallback() throws Throwable {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method: validateProperty not found");
        PropertiesWithRuntimeContextCallbacks withNewCallbacks = mock(PropertiesWithRuntimeContextCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.validateProperty(withNewCallbacks, "property", context);
    }

    /**
     * Asserts that callback without parameters is called, when it is the only callback present in {@link Properties} class
     * 
     * @throws Throwable
     */
    @Test
    public void testValidatePropertyRuntimeContextOldCallback() throws Throwable {
        PropertiesWithOldCallbacks withOldCallbacks = mock(PropertiesWithOldCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.validateProperty(withOldCallbacks, "property", context);
        verify(withOldCallbacks).validateProperty();
    }

    /**
     * Asserts that callback with {@link RuntimeContext} parameter is called, when {@link Properties} class has both kinds of
     * callbacks
     * and product passes {@link RuntimeContext} argument to the method
     * 
     * @throws Throwable
     */
    @Test
    public void testValidatePropertyRuntimeContextBothCallbacks() throws Throwable {
        PropertiesWithBothCallbacks withBothCallbacks = mock(PropertiesWithBothCallbacks.class);
        RuntimeContext context = mock(RuntimeContext.class);
        PropertiesDynamicMethodHelper.validateProperty(withBothCallbacks, "property", context);
        verify(withBothCallbacks).validateProperty(context);
    }

    /**
     * Asserts {@link PropertiesDynamicMethodHelper#setFormLayoutMethods(Properties, String, Form)} sets {@link Form}'s callback
     * flags to <code>false</code>,
     * when {@link Properties} without callbacks is passed
     * Callbacks flags are checked by calling isCall*() methods of {@link Form} instance
     * When {@link Properties} class has no callbacks, Product shouldn't try to call them
     * Product should use isCall*() methods to discover whether callbacks are present
     */
    @Test
    public void testSetFormLayoutMethodsNoCallback() {
        PropertiesWithoutCallbacks withoutCallbacks = new PropertiesWithoutCallbacks();
        Form form = new Form(withoutCallbacks, Form.MAIN);
        PropertiesDynamicMethodHelper.setFormLayoutMethods(withoutCallbacks, Form.MAIN, form);
        Assert.assertFalse(form.isCallBeforeFormPresent());
        Assert.assertFalse(form.isCallAfterFormBack());
        Assert.assertFalse(form.isCallAfterFormNext());
        Assert.assertFalse(form.isCallAfterFormFinish());
    }

    /**
     * Asserts {@link PropertiesDynamicMethodHelper#setFormLayoutMethods(Properties, String, Form)} sets {@link Form}'s callback
     * flags to <code>true</code>,
     * when {@link Properties} with old callbacks is passed
     * Callbacks flags are checked by calling isCall*() methods of {@link Form} instance
     * When {@link Properties} class has callbacks without parameters, Product may call them
     * Product should use isCall*() methods to discover whether callbacks are present
     */
    @Test
    public void testSetFormLayoutMethodsOldCallback() {
        PropertiesWithOldCallbacks withOldCallbacks = new PropertiesWithOldCallbacks();
        Form form = new Form(withOldCallbacks, Form.MAIN);
        PropertiesDynamicMethodHelper.setFormLayoutMethods(withOldCallbacks, Form.MAIN, form);
        Assert.assertTrue(form.isCallBeforeFormPresent());
        Assert.assertTrue(form.isCallAfterFormBack());
        Assert.assertTrue(form.isCallAfterFormNext());
        Assert.assertTrue(form.isCallAfterFormFinish());
    }

    /**
     * Asserts {@link PropertiesDynamicMethodHelper#setFormLayoutMethods(Properties, String, Form)} sets {@link Form}'s callback
     * flags to <code>false</code>,
     * when {@link Properties} with new callbacks only is passed
     * Callbacks flags are checked by calling isCall*() methods of {@link Form} instance
     * When {@link Properties} class has only callbacks with {@link RuntimeContext} parameter, but has no callbacks without
     * parameters, Product shouldn't try to call them
     * Product should use isCall*() methods to discover whether callbacks without parameters are present
     */
    @Test
    public void testSetFormLayoutMethodsNewCallback() {
        PropertiesWithRuntimeContextCallbacks withRuntimeContextCallbacks = new PropertiesWithRuntimeContextCallbacks();
        Form form = new Form(withRuntimeContextCallbacks, Form.MAIN);
        PropertiesDynamicMethodHelper.setFormLayoutMethods(withRuntimeContextCallbacks, Form.MAIN, form);
        Assert.assertFalse(form.isCallBeforeFormPresent());
        Assert.assertFalse(form.isCallAfterFormBack());
        Assert.assertFalse(form.isCallAfterFormNext());
        Assert.assertFalse(form.isCallAfterFormFinish());
    }

    /**
     * Asserts {@link PropertiesDynamicMethodHelper#setFormLayoutMethods(Properties, String, Form)} sets {@link Form}'s callback
     * flags to <code>true</code>,
     * when {@link Properties} with both callbacks is passed
     * Callbacks flags are checked by calling isCall*() methods of {@link Form} instance
     * When {@link Properties} class has both callbacks, Product may call them
     * Product should use isCall*() methods to discover whether callbacks without parameters are present
     */
    @Test
    public void testSetFormLayoutMethodsBothCallbacks() {
        PropertiesWithBothCallbacks withBothCallbacks = new PropertiesWithBothCallbacks();
        Form form = new Form(withBothCallbacks, Form.MAIN);
        PropertiesDynamicMethodHelper.setFormLayoutMethods(withBothCallbacks, Form.MAIN, form);
        Assert.assertTrue(form.isCallBeforeFormPresent());
        Assert.assertTrue(form.isCallAfterFormBack());
        Assert.assertTrue(form.isCallAfterFormNext());
        Assert.assertTrue(form.isCallAfterFormFinish());
    }

    /**
     * Asserts {@link PropertiesDynamicMethodHelper#setWidgetLayoutMethods(Properties, String, Widget)} sets {@link Widget}'s
     * callbacks flags to <code>false</code>,
     * when {@link Properties} without callbacks is passed
     * Callbacks flags are checked by calling isCall*() methods of {@link Widget} instance
     * When {@link Properties} class has no callbacks, Product shouldn't try to call them
     * Product should use isCall*() methods to discover whether callbacks are present
     */
    @Test
    public void testSetWidgetLayoutMethodsNoCallback() {
        PropertiesWithoutCallbacks withoutCallbacks = new PropertiesWithoutCallbacks();
        Widget widget = new Widget(withoutCallbacks);
        PropertiesDynamicMethodHelper.setWidgetLayoutMethods(withoutCallbacks, "property", widget);
        Assert.assertFalse(widget.isCallBeforePresent());
        Assert.assertFalse(widget.isCallBeforeActivate());
        Assert.assertFalse(widget.isCallValidate());
        Assert.assertFalse(widget.isCallAfter());
    }

    /**
     * Asserts {@link PropertiesDynamicMethodHelper#setWidgetLayoutMethods(Properties, String, Widget)} sets {@link Widget}'s
     * callbacks flags to <code>true</code>,
     * when {@link Properties} with callbacks only is passed
     * Callbacks flags are checked by calling isCall*() methods of {@link Widget} instance
     * When {@link Properties} class has old callbacks, Product may call them
     * Product should use isCall*() methods to discover whether callbacks are present
     */
    @Test
    public void testSetWidgetLayoutMethodsOldCallback() {
        PropertiesWithOldCallbacks withOldCallbacks = new PropertiesWithOldCallbacks();
        Widget widget = new Widget(withOldCallbacks);
        PropertiesDynamicMethodHelper.setWidgetLayoutMethods(withOldCallbacks, "property", widget);
        Assert.assertTrue(widget.isCallBeforePresent());
        Assert.assertFalse(widget.isCallBeforeActivate());
        Assert.assertTrue(widget.isCallValidate());
        Assert.assertTrue(widget.isCallAfter());
    }

    /**
     * Asserts {@link PropertiesDynamicMethodHelper#setWidgetLayoutMethods(Properties, String, Widget)} sets {@link Widget}'s
     * callbacks flags to <code>false</code>,
     * when {@link Properties} without new callbacks only is passed
     * Callbacks flags are checked by calling isCall*() methods of {@link Widget} instance
     * When {@link Properties} class has new callbacks only, Product shouldn't try to call them
     * Product should use isCall*() methods to discover whether callbacks are present
     */
    @Test
    public void testSetWidgetLayoutMethodsNewCallback() {
        PropertiesWithRuntimeContextCallbacks withRuntimeContextCallbacks = new PropertiesWithRuntimeContextCallbacks();
        Widget widget = new Widget(withRuntimeContextCallbacks);
        PropertiesDynamicMethodHelper.setWidgetLayoutMethods(withRuntimeContextCallbacks, "property", widget);
        Assert.assertFalse(widget.isCallBeforePresent());
        Assert.assertFalse(widget.isCallBeforeActivate());
        Assert.assertFalse(widget.isCallValidate());
        Assert.assertFalse(widget.isCallAfter());
    }

    /**
     * Asserts {@link PropertiesDynamicMethodHelper#setWidgetLayoutMethods(Properties, String, Widget)} sets {@link Widget}'s
     * callbacks flags to <code>true</code>,
     * when {@link Properties} with both callbacks is passed
     * Callbacks flags are checked by calling isCall*() methods of {@link Widget} instance
     * When {@link Properties} class has both callbacks, Product may call them
     * Product should use isCall*() methods to discover whether callbacks are present
     */
    @Test
    public void testSetWidgetLayoutMethodsBothCallbacks() {
        PropertiesWithBothCallbacks withBothCallbacks = new PropertiesWithBothCallbacks();
        Widget widget = new Widget(withBothCallbacks);
        PropertiesDynamicMethodHelper.setWidgetLayoutMethods(withBothCallbacks, "property", widget);
        Assert.assertTrue(widget.isCallBeforePresent());
        Assert.assertFalse(widget.isCallBeforeActivate());
        Assert.assertTrue(widget.isCallValidate());
        Assert.assertTrue(widget.isCallAfter());
    }

}
