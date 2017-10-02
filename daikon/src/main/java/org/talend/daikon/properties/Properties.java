// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import java.util.List;

import org.talend.daikon.NamedThing;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.property.PropertyValueEvaluator;
import org.talend.daikon.properties.validation.ValidationHelper;
import org.talend.daikon.serialize.PostDeserializeSetup;
import org.talend.daikon.serialize.SerializerDeserializer;
import org.talend.daikon.serialize.SerializerDeserializer.Deserialized;
import org.talend.daikon.strings.ToStringIndent;

/**
 * An implementation of the {@code Properties} interface contains the definitions of a set of properties. These
 * definitions contain enough information to automatically construct a nice looking user interface (UI) to populate and
 * validate the properties. The objective is that no actual (graphical) UI code is included in the component's
 * definition and as well no custom graphical UI is required for most components. The types of UIs that can be defined
 * include those for desktop (Eclipse), web, and scripting. All of these will use the code defined here for their
 * construction and validation.
 * <p/>
 * All aspects of the properties are defined in a subclass of this class using the {@link Property},
 * {@link PresentationItem}, {@link Widget}, and {@link Form} classes. In addition in cases where user interface
 * decisions are made in code, methods can be added to the subclass to influence the flow of the user interface and help
 * with validation.
 * <p/>
 * Each property can be a Java type, both simple types and collections are permitted. In addition, {@code Properties}
 * classes can be composed allowing hierarchies of properties and collections of properties to be reused.
 * <p/>
 * A property is defined using a field in a subclass of this class. Each property field is initialized with one of the
 * following:
 * <ol>
 * <li>For a single property, a {@link Property} object, usually using a static method from the
 * {@link PropertyFactory}.</li>
 * <li>For a reference to other properties, a subclass of {@code Properties}.</li>
 * <li>For a presentation item that's not actually a property, but is necessary for the user interface, a
 * {@link PresentationItem} .</li>
 * </ol>
 * <p/>
 * For construction of user interfaces, properties are grouped into {@link Form} objects which can be presented in
 * various ways by the user interface (for example, a wizard page, a tab in a property sheet, or a dialog). The same
 * property can appear in multiple forms.
 * <p/>
 * Methods can be added in subclasses according to the conventions below to help direct the UI. These methods will be
 * automatically called by the UI code.
 * <ul>
 * <li>{@code before<PropertyName>} - Called before the property is presented in the UI. This can be used to compute
 * anything required to display the property.</li>
 * <li>{@code after<PropertyName>} - Called after the property is presented and validated in the UI. This can be used to
 * update the properties state to consider the changed in this property.</li>
 * <li>{@code validate<PropertyName>} - Called to validate the property value that has been entered in the UI. This will
 * return a {@link ValidationResult} object with any error information.</li>
 * <li>{@code beforeFormPresent<FormName>} - Called before the form is displayed.</li>
 * </ul>
 * {@code PropertyName} and {@code FormName} are the property or form name with their first in letter uppercase.
 * </p>
 * wizard lifecycle related form methods are :
 * <ul>
 * <li>{@code afterFormBack<FormName>} - Called when the current edited form is &lt;FormName&gt; and when the user has
 * pressed the back button.</li>
 * <li>{@code afterFormNext<FormName>} - Called when the current edited form is &lt;FormName&gt; and when the user has
 * pressed the next button.</li>
 * <li>{@code afterFormFinish<FormName>(Repository<Properties> prop)} - Called when the current edited form is
 * &lt;FormName&gt; and when the finish button is pressed. this method is supposed to serialize the current Properties
 * instance and it's sub properties</li>
 * </ul>
 * <p/>
 * Once the Properties is create by the service, the {@link Properties#setupProperties()} and
 * {@link Properties#setupLayout()} is called.
 * <p/>
 * <b>WARNING</b> - It is not recommanded to instanciate a Property field after {@link Properties#setupProperties()} is
 * called. If you want to create the property later you'll have to call
 * {@link Property#setI18nMessageFormatter(I18nMessages)} manually.
 */
public interface Properties extends AnyProperty, ToStringIndent {

    String METHOD_BEFORE = "before";

    String METHOD_AFTER = "after";

    String METHOD_VALIDATE = "validate";

    // consider removing this in favor of beforeRender at the property level
    String METHOD_BEFORE_FORM = "beforeFormPresent";

    String METHOD_AFTER_FORM_BACK = "afterFormBack";

    String METHOD_AFTER_FORM_NEXT = "afterFormNext";

    String METHOD_AFTER_FORM_FINISH = "afterFormFinish";

    boolean ENCRYPT = true;

    /**
     * Must be called once the class is instantiated to setup the properties and the layout
     *
     * @return this instance
     */
    Properties init();

    /**
     * Initialize the properties without any layout initialization.
     *
     * @return this instance
     */
    Properties initForRuntime();

    /**
     * Initialize this object, all subclass initialization should override this, and call the super. <br>
     *
     * @warning call super() first.
     */
    void setupProperties();

    /**
     * Declare the widget layout information for each of the properties.<br>
     *
     * @warning call super() first.
     */
    void setupLayout();

    /**
     * Returns a serialized version of this for storage in a repository.
     *
     * @return the serialized {@code String}, use {@link Helper#fromSerializedPersistent(String, Class)} to materialize
     * the object.
     */
    String toSerialized();

    /**
     * This is called by within the execution of actions associated with {@code Properties} when the presentation of the
     * properties needs to be updated due to some value change. The main reason for calling this is to allow the
     * visibility of properties to be changed when values change.
     *
     * Note: This is automatically called at startup after all of the setupLayout() calls are done.
     */
    void refreshLayout(Form form);

    /**
     * Returns all of the {@link Form} objects associated with this object.
     */
    List<Form> getForms();

    /**
     * Gets a particular {@link Form} object.
     *
     * @param formName the wanted form name. If null, the default form will be
     * {@link org.talend.daikon.properties.presentation.Form.MAIN}
     */
    Form getForm(String formName);

    /**
     * Returns the requested {@link Form} object, but if that's not defined for this object, returns the first defined
     * form that exists. For example, the {@code Form.CITIZEN_USER} might be requested, but not defined, so the
     * {@code Form.MAIN} is returned instead. Return null if form is null
     *
     */
    Form getPreferredForm(String formName);

    /**
     * Adds a {@link Form} object to this object.
     */
    void addForm(Form form);

    /**
     * Returns the list of properties associated with this object.
     *
     * @return all properties associated with this object (including those defined in superclasses).
     */
    List<NamedThing> getProperties();

    /**
     * Returns {@link Property} or a {@link Properties} as specified by a qualifed property name string representing the
     * field name.
     * <p/>
     * The first component is the property name within this object. The optional subsequent components, separated by a
     * "." are property names in the nested {@link Properties} objects.
     *
     * @param propName a qualified property name, should never be null
     * @return the object denoted with the name or null if not found
     */
    NamedThing getProperty(String propName);

    /**
     * same as {@link Properties#getProperty(String)} but returns null if the property is not of type {@link Property}.
     */
    Property<?> getValuedProperty(String propPath);

    /**
     * same as {@link Properties#getProperty(String)} but returns null if the property is not of type {@link Properties}
     * .
     */
    Properties getProperties(String propPath);

    /**
     * Sets the stored value associated with the specified {@link Property} object.
     *
     * @param property the name of the {@code Property} object.
     * @param value the value to set
     */
    void setValue(String property, Object value);

    /**
     * Helper method to set the evaluator to all properties handled by this instance and all the nested Properties
     * instances.
     *
     * @param ve value evaluator to be used for evaluation.
     */
    void setValueEvaluator(PropertyValueEvaluator ve);

    /**
     * Returns the {@link ValidationResult} for the property being validated if requested.
     *
     * @return a ValidationResult
     */
    ValidationResult getValidationResult();

    /**
     * This goes through all nested properties recursively and replaces them with the newValueProperties given as
     * parameters as long as they are assignable to the Properties type. <br/>
     * Once the property is assigned it will not be recursively scanned. But if many nested Properties have the
     * appropriate type they will all be assigned to the new value.
     *
     * @param newValueProperties list of Properties to be assigned to this instance nested Properties
     */
    void assignNestedProperties(Properties... newValueProperties);

    /**
     * same as {@link #copyValuesFrom(Properties, boolean, boolean)} with copyTaggedValues set to true and copyEvaluator
     * set to true.
     */
    void copyValuesFrom(Properties props);

    /**
     * Copy all of the values from the specified {@link Properties} object. This includes the values from any nested
     * objects. This can be used even if the {@code Properties} objects are not the same class. Fields that are not
     * present in the this {@code Properties} object are ignored.
     *
     * @param props pros to copy into this Properties
     * @param copyTaggedValues if true all tagged values are copied
     * @param copyEvaluators if true all evaluators are copied
     */
    void copyValuesFrom(Properties props, boolean copyTaggedValues, boolean copyEvaluators);

    NamedThing createPropertyInstance(NamedThing otherProp) throws ReflectiveOperationException;

    Properties setName(String name);

    /**
     * Method to validate this Properties instance and get {@link ValidationResults} for this Object. This method is called using
     * {@link ValidationHelper#validateProperties(Properties)} to validate this Properties object and all properties it includes.
     * 
     * @return validation results for this Properties
     */
    ValidationResults validate();

    class Helper {

        public static synchronized <T extends Properties> SerializerDeserializer.Deserialized<T> fromSerializedPersistent(
                String serialized, Class<T> propertiesclass, PostDeserializeSetup postSetup) {
            Deserialized<T> prop = SerializerDeserializer.fromSerialized(serialized, propertiesclass, postSetup,
                    SerializerDeserializer.PERSISTENT);

            if (prop.object instanceof Properties) {
                ((Properties) prop.object).init();
            }

            return prop;
        }

        public static synchronized <T extends Properties> SerializerDeserializer.Deserialized<T> fromSerializedPersistent(
                String serialized, Class<T> propertiesclass) {
            Deserialized<T> prop = SerializerDeserializer.fromSerialized(serialized, propertiesclass, null,
                    SerializerDeserializer.PERSISTENT);

            if (prop.object instanceof Properties) {
                ((Properties) prop.object).init();
            }

            return prop;
        }

        public static synchronized <T extends Properties> SerializerDeserializer.Deserialized<T> fromSerializedTransient(
                String serialized, Class<T> propertiesclass) {
            return SerializerDeserializer.fromSerialized(serialized, propertiesclass, null, SerializerDeserializer.TRANSIENT);
        }
    }

}
