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
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.i18n.TranslatableImpl;
import org.talend.daikon.properties.error.PropertiesErrorCode;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.property.PropertyValueEvaluator;
import org.talend.daikon.security.CryptoHelper;
import org.talend.daikon.strings.ToStringIndent;
import org.talend.daikon.strings.ToStringIndentUtil;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

/**
 * Implementation of {@link Properties} which must be subclassed to define your properties.
 */
public class PropertiesImpl extends TranslatableImpl implements Properties, AnyProperty, ToStringIndent {

    private String name;

    transient private List<Form> forms = new ArrayList<>();

    ValidationResult validationResult;

    transient private boolean layoutAlreadyInitalized;

    transient private boolean propsAlreadyInitialized;

    /**
     * Returns the Properties object previously serialized.
     *
     * @param serialized created by {@link #toSerialized()}.
     * @param propertiesclass, class type to deserialized
     * @param postSerializationSetup callback to setup the Properties class after deserialization and before layout and i18N
     *            setup.
     * @return a {@code Properties} object represented by the {@code serialized} value.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T extends Properties> Deserialized<T> fromSerialized(String serialized, Class<T> propertiesclass,
            PostSerializationSetup<T> postSerializationSetup) {
        Deserialized<T> d = new Deserialized<>();
        d.migration = new MigrationInformationImpl();
        // this set the proper classloader for the JsonReader especially for OSGI
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(Properties.class.getClassLoader());
            d.properties = (T) JsonReader.jsonToJava(serialized);
            if (d.properties instanceof PropertiesImpl) {
                ((PropertiesImpl) d.properties).handlePropEncryption(!ENCRYPT);
            } // else we nothing to be done
            if (postSerializationSetup != null) {
                postSerializationSetup.setup(d.properties);
            } // else no setup callback to call so ignore
            if (d.properties instanceof PropertiesImpl) {
                ((PropertiesImpl) d.properties).setupPropertiesPostDeserialization();
            } // else we nothing to be done
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
            if (prop instanceof PropertiesImpl) {
                ((PropertiesImpl) prop).setupPropertiesPostDeserialization();
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
    public PropertiesImpl(String name) {
        setName(name);
    }

    @Override
    public Properties init() {
        // init nested properties starting from the bottom ones
        initProperties();
        initLayout();
        return this;
    }

    @Override
    public Properties initForRuntime() {
        initProperties();
        return this;
    }

    private void initProperties() {
        if (!propsAlreadyInitialized) {
            List<Field> uninitializedProperties = initializeFields();
            setupProperties();
            // initialize all the properties that where found and not initialized
            // they must be initalized after the setup.
            for (Field f : uninitializedProperties) {
                NamedThing se;
                try {
                    f.setAccessible(true);
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

    protected List<Field> initializeFields() {
        List<Field> uninitializedProperties = new ArrayList<>();
        Field[] fields = getClass().getFields();
        for (Field f : fields) {
            try {
                if (isAPropertyType(f.getType())) {
                    f.setAccessible(true);
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
        return uninitializedProperties;
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

    @Override
    public void initializeField(Field f, NamedThing value) {
        // check that field name matches the NamedThing name
        if (!f.getName().equals(value.getName())) {
            throw new IllegalArgumentException("The java field [" + this.getClass().getCanonicalName() + "." + f.getName()
                    + "] should be named identically to the instance name [" + value.getName() + "]");
        }
        if (value instanceof Property) {
            // Do not set the i18N for nested Properties, they already handle their i18n
            value.setI18nMessageFormater(getI18nMessageFormater());
        } else if (value instanceof PropertiesImpl) {// a property so setit up
            ((PropertiesImpl) value).initProperties();
        } // else nothing to initialize.
    }

    private void initLayout() {
        if (!layoutAlreadyInitalized) {// prevent 2 initialization if the same Props instance is used in 2 comps
            List<NamedThing> properties = getProperties();
            for (NamedThing prop : properties) {
                if (prop instanceof PropertiesImpl) {
                    ((PropertiesImpl) prop).initLayout();
                } // else not layout to initialize.
            }
            setupLayout();
            refreshAllFormsLayout();
            layoutAlreadyInitalized = true;
        } // else already initialized
    }

    protected void refreshAllFormsLayout() {
        for (Form form : getForms()) {
            refreshLayout(form);
        }
    }

    @Override
    public void setupProperties() {
        // left empty for subclass to override
    }

    @Override
    public void setupLayout() {
        // left empty for subclass to override
    }

    @Override
    public String toSerialized() {
        handlePropEncryption(ENCRYPT);

        try {
            return JsonWriter.objectToJson(this);
        } finally {
            handlePropEncryption(!ENCRYPT);
        }

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

    @Override
    public void refreshLayout(Form form) {
        if (form != null) {
            form.setRefreshUI(true);

        } // else nothing to refresh
    }

    @Override
    public List<Form> getForms() {
        return forms;
    }

    @Override
    public Form getForm(String formName) {
        for (Form f : forms) {
            if (f.getName().equals(formName)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public String getSimpleClassName() {
        return getClass().getSimpleName();
    }

    @Override
    public void addForm(Form form) {
        forms.add(form);
    }

    @Override
    public List<NamedThing> getProperties() {
        // TODO this should be changed to AnyProperty type but it as impact everywhere
        List<NamedThing> properties = new ArrayList<>();
        List<Field> propertyFields = getAnyPropertyFields();
        for (Field f : propertyFields) {
            try {
                if (NamedThing.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
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
     * Is this object of type {@link Property} or {@link Properties}?
     * 
     * @param clazz, the class to be tested
     * @return true if the clazz inherits from {@link Property} or {@link Properties}.
     */
    protected boolean isAPropertyType(Class<?> clazz) {
        return AnyProperty.class.isAssignableFrom(clazz);
    }

    @Override
    public NamedThing getProperty(String propName) {
        String[] propComps = propName.split("\\.");
        PropertiesImpl currentProps = this;
        int i = 0;
        for (String prop : propComps) {
            if (i++ == propComps.length - 1) {
                return currentProps.getLocalProperty(prop);
            }
            NamedThing se = currentProps.getLocalProperty(prop);
            if (!(se instanceof PropertiesImpl)) {
                return null;
            }
            currentProps = (PropertiesImpl) se;
        }
        return null;
    }

    @Override
    public Property<?> getValuedProperty(String propPath) {
        NamedThing prop = getProperty(propPath);
        return (prop instanceof Property) ? (Property<?>) prop : null;
    }

    @Override
    public Properties getProperties(String propPath) {
        NamedThing prop = getProperty(propPath);
        return (prop instanceof Properties) ? (Properties) prop : null;
    }

    /**
     * Returns the property in this object specified by a the simple (unqualified) property name.
     * 
     * @param propName a simple property name. Should never be null
     */
    protected NamedThing getLocalProperty(String propName) {
        List<NamedThing> properties = getProperties();
        for (NamedThing prop : properties) {
            if (propName.equals(prop.getName())) {
                return prop;
            }
        }
        return null;
    }

    @Override
    public void setValue(String property, Object value) {
        NamedThing p = getProperty(property);
        if (!(p instanceof Property)) {
            throw new IllegalArgumentException("setValue but property: " + property + " is not a Property");
        }
        ((Property) p).setValue(value);
    }

    @Override
    public void setValueEvaluator(PropertyValueEvaluator ve) {
        List<NamedThing> properties = getProperties();
        for (NamedThing prop : properties) {
            if (prop instanceof Property) {
                ((Property<?>) prop).setValueEvaluator(ve);
            } else if (prop instanceof Properties) {
                ((Properties) prop).setValueEvaluator(ve);
            }
        }
    }

    public void setValidationResult(ValidationResult vr) {
        validationResult = vr;
    }

    @Override
    public ValidationResult getValidationResult() {
        return validationResult;
    }

    @Override
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

    @Override
    public void copyValuesFrom(Properties props) {
        copyValuesFrom(props, true, true);
    }

    @Override
    public void copyValuesFrom(Properties props, boolean copyTaggedValues, boolean copyEvaluators) {
        for (NamedThing otherProp : props.getProperties()) {
            NamedThing thisProp = getProperty(otherProp.getName());
            if (thisProp == null) {
                // the current Property or Properties is null so we need to create a new instance
                try {
                    thisProp = createPropertyInstance(otherProp);
                    // assign the newly created instance to the field.
                    try {
                        Field f = getClass().getField(otherProp.getName());
                        f.set(this, thisProp);
                    } catch (NoSuchFieldException e) {
                        // A field exists in the other that's not in ours, just ignore it
                        continue;
                    }
                } catch (ReflectiveOperationException | SecurityException e) {
                    TalendRuntimeException.unexpectedException(e);
                }
            }

            // thisProp cannot be null here.
            // recurse if it is a Properties
            if (otherProp instanceof PropertiesImpl) {
                ((PropertiesImpl) thisProp).copyValuesFrom((Properties) otherProp);
                ((PropertiesImpl) thisProp).refreshAllFormsLayout();
            } else if (otherProp instanceof Property) {
                // copy the value
                Object value = ((Property) otherProp).getStoredValue();
                ((Property) thisProp).setStoredValue(value);
                if (copyTaggedValues) {
                    ((Property) thisProp).copyTaggedValues((Property) otherProp);
                }
                if (copyEvaluators) {
                    ((Property) thisProp).setValueEvaluator(((Property) otherProp).getValueEvaluator());
                }
            } else {
                TalendRuntimeException
                        .unexpectedException("The property " + otherProp.getClass().getName() + " is not of the expected type.");
            }

        }

    }

    @Override
    public NamedThing createPropertyInstance(NamedThing otherProp) throws ReflectiveOperationException {
        NamedThing thisProp = null;
        Class<? extends NamedThing> otherClass = otherProp.getClass();
        if (Property.class.isAssignableFrom(otherClass)) {
            Property<?> otherPy = (Property<?>) otherProp;
            Constructor<? extends NamedThing> c = otherClass.getDeclaredConstructor(String.class, String.class);
            c.setAccessible(true);
            thisProp = c.newInstance(otherPy.getType(), otherPy.getName());
        } else if (Properties.class.isAssignableFrom(otherClass)) {
            // Look for single arg String, but an inner class will have a Properties as first arg
            Constructor<?>[] constructors = otherClass.getConstructors();
            for (Constructor<?> c : constructors) {
                Class<?> pts[] = c.getParameterTypes();
                c.setAccessible(true);
                if (pts.length == 1 && String.class.isAssignableFrom(pts[0])) {
                    thisProp = (NamedThing) c.newInstance(otherProp.getName());
                    break;
                }
                if (pts.length == 2 && Properties.class.isAssignableFrom(pts[0]) && String.class.isAssignableFrom(pts[1])) {
                    thisProp = (NamedThing) c.newInstance(this, otherProp.getName());
                    break;
                }
            }
            if (thisProp == null) {
                TalendRuntimeException
                        .unexpectedException("Failed to find a proper constructor in Properties : " + otherClass.getName());
            }
        } else {
            TalendRuntimeException
                    .unexpectedException("Unexpected property class: " + otherProp.getClass() + " prop: " + otherProp);
        }
        return thisProp;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Properties setName(String name) {

        this.name = name;
        return this;
    }

    @Override
    public String getDisplayName() {
        return getI18nMessage("properties." + (getName() == null ? getName() : "") + ".displayName");
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
            String value = prop instanceof Property ? ((Property<?>) prop).getStringValue() : null;
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
