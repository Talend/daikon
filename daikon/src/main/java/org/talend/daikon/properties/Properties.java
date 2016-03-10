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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.talend.daikon.NamedThing;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.i18n.TranslatableImpl;
import org.talend.daikon.properties.error.PropertiesErrorCode;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.security.CryptoHelper;
import org.talend.daikon.strings.ToStringIndent;
import org.talend.daikon.strings.ToStringIndentUtil;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

/**
 * The {@code Properties} class contains the definitions of the properties associated with a component. These
 * definitions contain enough information to automatically construct a nice looking user interface (UI) to populate and
 * validate the properties. The objective is that no actual (graphical) UI code is included in the component's
 * definition and as well no custom graphical UI is required for most components. The types of UIs that can be defined
 * include those for desktop (Eclipse), web, and scripting. All of these will use the code defined here for their
 * construction and validation.
 * <p/>
 * All aspects of the properties are defined in a subclass of this class using the {@link Property},
 * {@Link PresentationItem}, {@link Widget}, and {@link Form} classes. In addition in cases where user interface
 * decisions are made in code, methods can be added to the subclass to influence the flow of the user interface and help
 * with validation.
 * <p/>
 * Each property can be a Java type, both simple types and collections are permitted. In addition, {@code Properties}
 * classes can be composed allowing hierarchies of properties and collections of properties to be reused.
 * <p/>
 * A property is defined using a field in a subclass of this class. Each property field is initialized with one of the
 * following:
 * <ol>
 * <li>For a single property, a {@link Property} object, usually using a static method from the {@link PropertyFactory}.
 * </li>
 * <li>For a reference to other properties, a subclass of {@code Properties}.</li>
 * <li>For a presentation item that's not actually a property, but is necessary for the user interface, a
 * {@link PresentationItem}.</li>
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
 * {@code<PropertyName>} and {@code<FormName>} are the property or form name with their first in letter uppercase.
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
 * {@link SchemaElement#setI18nMessageFormater(I18nMessages)} manually.
 */

/**
 *
 */
public abstract class Properties extends TranslatableImpl implements AnyProperty, ToStringIndent {

    static final String METHOD_BEFORE = "before";

    static final String METHOD_AFTER = "after";

    static final String METHOD_VALIDATE = "validate";

    // consider removing this in favor of beforeRender at the property level
    static final String METHOD_BEFORE_FORM = "beforeFormPresent";

    static final String METHOD_AFTER_FORM_BACK = "afterFormBack";

    static final String METHOD_AFTER_FORM_NEXT = "afterFormNext";

    static final String METHOD_AFTER_FORM_FINISH = "afterFormFinish";

    private String name;

    private List<Form> forms = new ArrayList<>();

    ValidationResult validationResult;

    transient private boolean layoutAlreadyInitalized;

    transient private boolean propsAlreadyInitialized;

    /**
     * Holder class for the results of a deserialization.
     */
    public static class Deserialized<T extends Properties> {

        public T properties;

        public MigrationInformation migration;
    }

    // FIXME - will be moved
    public static class MigrationInformationImpl implements MigrationInformation {

        @Override
        public boolean isMigrated() {
            return false;
        }

        @Override
        public String getVersion() {
            return null;
        }
    }

    /**
     * Returns the Properties object previously serialized.
     *
     * @param serialized created by {@link #toSerialized()}.
     * @return a {@code Properties} object represented by the {@code serialized} value.
     */
    public static synchronized <T extends Properties> Deserialized<T> fromSerialized(String serialized,
            Class<T> propertiesclass) {
        Deserialized<T> d = new Deserialized<T>();
        d.migration = new MigrationInformationImpl();
        // this set the proper classloader for the JsonReader especially for OSGI
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(Properties.class.getClassLoader());
            d.properties = (T) JsonReader.jsonToJava(serialized);
            d.properties.handlePropEncryption(!ENCRYPT);
            d.properties.setupPropertiesPostDeserialization();
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }
        return d;
    }

    /**
     * This will setup all Properties after the deserialization process. For now it will just setup i18N
     */
    void setupPropertiesPostDeserialization() {
        initLayout();
        List<NamedThing> properties = getProperties();
        for (NamedThing prop : properties) {
            if (prop instanceof Properties) {
                ((Properties) prop).setupPropertiesPostDeserialization();
            } else {
                prop.setI18nMessageFormater(getI18nMessageFormater());
            }
        }

    }

    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     * 
     * @param name, uniquely identify the property among other properties when used as nested properties.
     */
    public Properties(String name) {
        setName(name);
    }

    /**
     * Must be called once the class is instanciated to setup the properties and the layout
     * 
     * @return this instance
     */
    public Properties init() {
        // init nested properties starting from the bottom ones
        initProperties();
        initLayout();
        return this;
    }

    /**
     * only initilize the properties but not the layout.
     * 
     * @return this instance
     */
    public Properties initForRuntime() {
        initProperties();
        return this;
    }

    private void initProperties() {
        if (!propsAlreadyInitialized) {
            List<Field> uninitializedProperties = new ArrayList<>();
            Field[] fields = getClass().getFields();
            for (Field f : fields) {
                try {
                    if (isAPropertyType(f.getType())) {
                        NamedThing se = (NamedThing) f.get(this);
                        if (se != null) {
                            initializeField(f, se);
                        } else {// not yet initialized to record it
                            uninitializedProperties.add(f);
                        }
                    } // else not a field that ought to be initialized
                } catch (IllegalAccessException e) {
                    throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                }
            }
            setupProperties();
            // initialize all the properties that where found and not initialized
            // they must be initalized after the setup.
            for (Field f : uninitializedProperties) {
                NamedThing se;
                try {
                    se = (NamedThing) f.get(this);
                    if (se != null) {
                        initializeField(f, se);
                    } else {// field not initilaized but is should be (except for returns field)
                        if (!acceptUninitializedField(f)) {
                            throw new TalendRuntimeException(PropertiesErrorCode.PROPERTIES_HAS_UNITIALIZED_PROPS,
                                    ExceptionContext.withBuilder().put("name", this.getClass().getCanonicalName())
                                            .put("field", f.getName()).build());
                        } // else a returns field that may not be initialized
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                }
            }
            propsAlreadyInitialized = true;
        } // else already intialized
    }

    /**
     * this is called during setProperties to check after everything is setup that some properties may be null. Usually
     * it is not recommended to have properties not setup. But for example the RETURN properties for ComponentProperties
     * may be null.
     * 
     * @param f field to be check if a null value is tolerated after initialization.
     * @return true if the null value is accepted for the given field after setup.
     */
    protected boolean acceptUninitializedField(Field f) {
        return false;// by default all property need to be initialized after setup.
    }

    /**
     * This shall set the value holder for all the properties, set the i18n formatter of this current class to the
     * properties so that the i18n values are computed agains this class message properties. This calls the
     * initProperties for all field of type {@link Property}
     * 
     * @param f field to be initialized
     * @param value associated with this field, never null
     */
    public void initializeField(Field f, NamedThing value) {
        // check that field name matches the NamedThing name
        if (!f.getName().equals(value.getName())) {
            throw new IllegalArgumentException("The java field [" + this.getClass().getCanonicalName() + "." + f.getName()
                    + "] should be named identically to the instance name [" + value.getName() + "]");
        }
        if (value instanceof Property) {
            // Do not set the i18N for nested Properties, they already handle their i18n
            value.setI18nMessageFormater(getI18nMessageFormater());
        } else {// a property so setit up
            ((Properties) value).initProperties();
        }
    }

    private void initLayout() {
        if (!layoutAlreadyInitalized) {// prevent 2 initialization if the same Props instance is used in 2 comps
            List<NamedThing> properties = getProperties();
            for (NamedThing prop : properties) {
                if (prop instanceof Properties) {
                    ((Properties) prop).initLayout();
                }
            }
            setupLayout();
            for (Form form : getForms()) {
                refreshLayout(form);
            }
            layoutAlreadyInitalized = true;
        } // else already initialized
    }

    /**
     * Initialize this object, all subclass initialization should override this, and call the super. <br>
     * WARNING : make sure to call super() first otherwise you may endup with NPE because of not initialised properties
     */
    public void setupProperties() {
        // left empty for subclass to override
    }

    /**
     * Declare the widget layout information for each of the properties.<br>
     * WARNING : make sure to call super() first otherwise you may endup with NPE because of not initialised layout
     */
    public void setupLayout() {
        // left empty for subclass to override
    }

    /**
     * Returns a serialized version of this for storage in a repository.
     *
     * @return the serialized {@code String}, use {@link #fromSerialized(String, Class)} to materialize the object.
     */
    public String toSerialized() {
        handlePropEncryption(ENCRYPT);
        List<Form> currentForms = forms;
        String ser = null;
        try {
            // The forms are recreated upon deserialization
            forms = new ArrayList<>();
            ser = JsonWriter.objectToJson(this);
        } finally {
            forms = currentForms;
        }
        handlePropEncryption(!ENCRYPT);
        return ser;

    }

    protected static final boolean ENCRYPT = true;

    protected void handlePropEncryption(final boolean encrypt) {
        accept(new AnyPropertyVisitor() {

            @Override
            public void visit(Properties properties, Properties parent) {
                // nothing to be encrypted here
            }

            @Override
            public void visit(Property property, Properties parent) {
                if (property.isFlag(Property.Flags.ENCRYPT)) {
                    String value = (String) property.getStoredValue();
                    CryptoHelper ch = new CryptoHelper(CryptoHelper.PASSPHRASE);
                    if (encrypt) {
                        property.setValue(ch.encrypt(value));
                    } else {
                        property.setValue(ch.decrypt(value));
                    }
                }
            }
        }, null);// null cause we are visiting ourself
    }

    /**
     * This is called every time the presentation of the components properties needs to be updated.
     *
     * Note: This is automatically called at startup after all of the setupLayout() calls are done. It only needs to be
     * called after that when the layout has been changed.
     */
    public void refreshLayout(Form form) {
        form.setRefreshUI(true);
    }

    public List<Form> getForms() {
        return forms;
    }

    public Form getForm(String formName) {
        for (Form f : forms) {
            if (f.getName().equals(formName)) {
                return f;
            }
        }
        return null;
    }

    public String getSimpleClassName() {
        return getClass().getSimpleName();
    }

    public void addForm(Form form) {
        forms.add(form);
    }

    /**
     * Returns the list of properties associated with this object.
     * 
     * @return all properties associated with this object (including those defined in superclasses).
     */
    public List<NamedThing> getProperties() {
        // TODO this should be changed to AnyProperty type but it as impact everywhere
        List<NamedThing> properties = new ArrayList<>();
        List<Field> propertyFields = getAnyPropertyFields();
        for (Field f : propertyFields) {
            try {
                if (NamedThing.class.isAssignableFrom(f.getType())) {
                    Object fValue = f.get(this);
                    if (fValue != null) {
                        NamedThing se = (NamedThing) fValue;
                        properties.add(se);
                    } // else not initalized but this is already handled in the initProperties that must be called
                      // before the getProperties
                }
            } catch (IllegalAccessException e) {
                throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
            }
        }
        return properties;
    }

    /**
     * @return a direct list of field assignable from AnyProperty
     */
    private List<Field> getAnyPropertyFields() {
        List<Field> propertyFields = new ArrayList<>();
        Field[] fields = getClass().getFields();
        for (Field f : fields) {
            if (isAPropertyType(f.getType())) {
                propertyFields.add(f);
            }
        }
        return propertyFields;
    }

    @Override
    public void accept(AnyPropertyVisitor visitor, Properties parent) {
        List<NamedThing> properties = getProperties();
        for (NamedThing nt : properties) {
            if (nt instanceof AnyProperty) {
                ((AnyProperty) nt).accept(visitor, this);
            }
        }
        visitor.visit(this, parent);
    }

    /**
     * is this object of type Property or ComponenetProperties, the properties type handle by this class.
     * 
     * @param clazz, the class to be tested
     * @return true if the clazz inherites from Property or ComponenetProperties
     */
    protected boolean isAPropertyType(Class<?> clazz) {
        return AnyProperty.class.isAssignableFrom(clazz);
    }

    /**
     * Returns Property or a CompoentProperties as specified by a qualifed property name string representing the field
     * name.
     * <p/>
     * The first component is the property name within this object. The optional subsequent components, separated by a
     * "." are property names in the nested {@link Properties} objects.
     *
     * @param name a qualified property name, should never be null
     * @return the Property or Componenent denoted with the name or null if not found
     */
    public NamedThing getProperty(String name) {
        String[] propComps = name.split("\\.");
        Properties currentProps = this;
        int i = 0;
        for (String prop : propComps) {
            if (i++ == propComps.length - 1) {
                return currentProps.getLocalProperty(prop);
            }
            NamedThing se = currentProps.getLocalProperty(prop);
            if (!(se instanceof Properties)) {
                return null;
            }
            currentProps = (Properties) se;
        }
        return null;
    }

    /**
     * same as {@link Properties#getProperties()} but returns null if the Property is not of type Property.
     */
    public Property getValuedProperty(String propPath) {
        NamedThing prop = getProperty(propPath);
        return (prop instanceof Property) ? (Property) prop : null;
    }

    /**
     * same as {@link Properties#getProperties()} but returns null if the Property is not of type ComponentProperty.
     */
    public Properties getProperties(String propPath) {
        NamedThing prop = getProperty(propPath);
        return (prop instanceof Properties) ? (Properties) prop : null;
    }

    /**
     * Returns the property in this object specified by a the simple (unqualified) property name.
     * 
     * @param name a simple property name. Should never be null
     */
    protected NamedThing getLocalProperty(String name) {
        List<NamedThing> properties = getProperties();
        for (NamedThing prop : properties) {
            if (name.equals(prop.getName())) {
                return prop;
            }
        }
        return null;
    }

    public void setValue(String property, Object value) {
        NamedThing p = getProperty(property);
        if (!(p instanceof Property)) {
            throw new IllegalArgumentException("setValue but property: " + property + " is not a Property");
        }
        ((Property) p).setValue(value);
    }

    /**
     * Helper method to set the evaluator to all properties handled by this instance and all the nested Properties
     * instances.
     * 
     * @param ve value evalurator to be used for evaluation.
     */
    public void setValueEvaluator(PropertyValueEvaluator ve) {
        List<NamedThing> properties = getProperties();
        for (NamedThing prop : properties) {
            if (prop instanceof Property) {
                ((Property) prop).setValueEvaluator(ve);
            } else if (prop instanceof Properties) {
                ((Properties) prop).setValueEvaluator(ve);
            }
        }
    }

    /**
     * Returns the {@link ValidationResult} for the property being validated if requested.
     *
     * @return a ValidationResult
     */
    public ValidationResult getValidationResult() {
        return validationResult;
    }

    /**
     * This goes through all nested properties recusively and replace them with the newValueProperties given as
     * parameters as long as they are assignable to the Properties type. <br/>
     * Once the property is assigned it will not be recusively scanned. But if many nested Properties have the
     * appropriate type they will all be assigned to the new value.
     * 
     * @param newValueProperties list of Properties to be assigned to this instance nested Properties
     */
    public void assignNestedProperties(Properties... newValueProperties) {
        List<Field> propertyFields = getAnyPropertyFields();
        for (Field propField : propertyFields) {
            Class<?> propType = propField.getType();
            if (Properties.class.isAssignableFrom(propType)) {
                boolean isNewAssignment = false;
                for (Properties newValue : newValueProperties) {
                    if (propType.isAssignableFrom(newValue.getClass())) {
                        try {
                            propField.set(this, newValue);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                        }
                        isNewAssignment = true;
                    } // else not a compatible type so keep looking
                }
                if (!isNewAssignment) {// recurse
                    Properties prop;
                    try {
                        prop = (Properties) propField.get(this);
                        if (prop != null) {
                            prop.assignNestedProperties(newValueProperties);
                        } // else prop value is null so we can't recurse. this should never happend
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                    } // cast is ok we check it was assignable before.
                }
            } // else not a nestedProperties so keep looking.
        }
    }

    /**
     * Copy all of the values from the specified {@link Properties} object. This includes the values from any nested
     * objects. This can be used even if the {@code Properties} objects are not the same class. Fields that are not
     * present in the this {@code Properties} object are ignored.
     * 
     * @param props
     */
    public void copyValuesFrom(Properties props) {
        for (NamedThing otherProp : props.getProperties()) {
            NamedThing thisProp = getProperty(otherProp.getName());
            if (thisProp == null) {
                try {
                    Class otherClass = otherProp.getClass();

                    if (Property.class.isAssignableFrom(otherClass)) {
                        Constructor c = otherClass.getConstructor(String.class);
                        thisProp = (NamedThing) c.newInstance(otherProp.getName());
                    } else if (Properties.class.isAssignableFrom(otherClass)) {
                        // Look for single arg String, but an inner class will have a Properties as first arg
                        Constructor constructors[] = otherClass.getConstructors();
                        for (Constructor c : constructors) {
                            Class pts[] = c.getParameterTypes();
                            if (pts.length == 1 && String.class.isAssignableFrom(pts[0])) {
                                thisProp = (NamedThing) c.newInstance(otherProp.getName());
                                break;
                            }
                            if (pts.length == 2 && Properties.class.isAssignableFrom(pts[0])
                                    && String.class.isAssignableFrom(pts[1])) {
                                thisProp = (NamedThing) c.newInstance(this, otherProp.getName());
                                break;
                            }
                        }
                    } else {
                        TalendRuntimeException.unexpectedException(
                                "Unexpected property class: " + otherProp.getClass() + " prop: " + otherProp);
                    }

                    try {
                        Field f = getClass().getField(otherProp.getName());
                        f.set(this, thisProp);
                    } catch (NoSuchFieldException e) {
                        // A field exists in the other that's not in ours, just ignore it
                        continue;
                    }
                } catch (Exception e) {
                    TalendRuntimeException.unexpectedException(e);
                }
            }
            if (otherProp instanceof Properties) {
                ((Properties) thisProp).copyValuesFrom((Properties) otherProp);
            } else {
                Object value = ((Property) otherProp).getStoredValue();
                ((Property) thisProp).setValue(value);
            }
        }

    }

    @Override
    public String getName() {
        return name;
    }

    public Properties setName(String name) {

        this.name = name;
        return this;
    }

    @Override
    public String getDisplayName() {
        return getI18nMessage("properties" + (getName() == null ? getName() : "") + ".displayName");
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String toString() {
        return toStringIndent(0);
    }

    @Override
    public String toStringIndent(int indent) {
        StringBuilder sb = new StringBuilder();
        String is = ToStringIndentUtil.indentString(indent);
        sb.append(is + getName() + " - " + getTitle() + " " + getClass().getName());
        sb.append("\n" + is + "   Properties:");
        for (NamedThing prop : getProperties()) {
            if (prop instanceof ToStringIndent) {
                sb.append('\n' + ((ToStringIndent) prop).toStringIndent(indent + 6));
            } else {
                sb.append('\n' + prop.toString());
            }
            String value = prop instanceof Property ? ((Property) prop).getStringValue() : null;
            if (value != null) {
                sb.append(" [" + value + "]");
            }
        }
        sb.append("\n " + is + "  Forms:");
        for (Form form : getForms()) {
            sb.append("\n" + form.toStringIndent(indent + 6));
        }
        return sb.toString();
    }

}
