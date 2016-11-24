// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.serialize.jsonio;

import java.text.ParseException;

import org.junit.Test;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.serialize.FullExampleProperties;
import org.talend.daikon.serialize.FullExampleTestUtil;

public class FormTransientTest {

    /**
     * There is a change on PropertiesImpl, mark forms as transient, test if it break the json-io ser/des.
     * Use the generated json which PropertiesImpl with forms, and deserialize to the PropertiesImpl which has transient forms
     * 
     * @throws ParseException
     */
    @Test
    public void test() throws ParseException {
        FullExampleProperties fullExampleProperties = FullExampleTestUtil.createASetupFullExampleProperties();
        // System.out.println(fullExampleProperties.toSerialized());
        // beforeFormsTransient is generated by fullExampleProperties.toSerialized before forms mark as transient
        String beforeFormsTransient = "{\"@id\":1,\"@type\":\"org.talend.daikon.serialize.FullExampleProperties\",\"stringProp\":{\"@id\":20,\"@type\":\"org.talend.daikon.properties.property.StringProperty\",\"possibleValues2\":null,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":\"abc\",\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.lang.String\",\"name\":\"stringProp\",\"displayName\":null,\"title\":null},\"hideStringPropProp\":{\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":{\"@type\":\"boolean\",\"value\":false},\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.lang.Boolean\",\"name\":\"hideStringPropProp\",\"displayName\":null,\"title\":null},\"multipleSelectionProp\":{\"@id\":19,\"@type\":\"org.talend.daikon.properties.property.StringProperty\",\"possibleValues2\":null,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":null,\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":{\"@type\":\"java.util.ArrayList\",\"@items\":[{\"@type\":\"org.talend.daikon.SimpleNamedThing\",\"name\":\"foo\",\"displayName\":null,\"title\":null},{\"@type\":\"org.talend.daikon.SimpleNamedThing\",\"name\":\"bar\",\"displayName\":null,\"title\":null},{\"@type\":\"org.talend.daikon.SimpleNamedThing\",\"name\":\"foobar\",\"displayName\":null,\"title\":null}]},\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.lang.String\",\"name\":\"multipleSelectionProp\",\"displayName\":null,\"title\":null},\"showNewForm\":{\"@id\":18,\"formtoShow\":{\"@id\":10,\"subtitle\":null,\"properties\":{\"@ref\":1},\"widgetMap\":{},\"cancelable\":false,\"originalValues\":null,\"callBeforeFormPresent\":true,\"callAfterFormBack\":false,\"callAfterFormNext\":false,\"callAfterFormFinish\":false,\"allowBack\":false,\"allowForward\":false,\"allowFinish\":false,\"refreshUI\":true,\"name\":\"popup\",\"displayName\":\"popup\",\"title\":null},\"name\":\"showNewForm\",\"displayName\":\"Show new form\",\"title\":null},\"tableProp\":{\"@id\":2,\"colListString\":{\"@id\":5,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":{\"@type\":\"java.util.Arrays$ArrayList\",\"@items\":[\"a\",\"b\",\"c\"]},\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.util.List<java.lang.String>\",\"name\":\"colListString\",\"displayName\":null,\"title\":null},\"colListEnum\":{\"@id\":4,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":{\"@type\":\"java.util.Arrays$ArrayList\",\"@items\":[{\"@type\":\"org.talend.daikon.serialize.FullExampleProperties$TableProperties$ColEnum\",\"name\":\"FOO\",\"ordinal\":0},{\"@type\":\"org.talend.daikon.serialize.FullExampleProperties$TableProperties$ColEnum\",\"name\":\"BAR\",\"ordinal\":1},{\"@type\":\"org.talend.daikon.serialize.FullExampleProperties$TableProperties$ColEnum\",\"name\":\"FOO\",\"ordinal\":0}]},\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":{\"@type\":\"java.util.Arrays$ArrayList\",\"@items\":[{\"@type\":\"org.talend.daikon.serialize.FullExampleProperties$TableProperties$ColEnum\",\"name\":\"FOO\",\"ordinal\":0},{\"@type\":\"org.talend.daikon.serialize.FullExampleProperties$TableProperties$ColEnum\",\"name\":\"BAR\",\"ordinal\":1}]},\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.util.List<org.talend.daikon.serialize.FullExampleProperties.TableProperties.ColEnum>\",\"name\":\"colListEnum\",\"displayName\":null,\"title\":null},\"colListBoolean\":{\"@id\":3,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":{\"@type\":\"java.util.Arrays$ArrayList\",\"@items\":[true,false,true]},\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.util.List<java.lang.Boolean>\",\"name\":\"colListBoolean\",\"displayName\":null,\"title\":null},\"name\":\"tableProp\",\"forms\":{\"@type\":\"java.util.ArrayList\",\"@items\":[{\"@type\":\"org.talend.daikon.properties.presentation.Form\",\"subtitle\":null,\"properties\":{\"@ref\":2},\"widgetMap\":{\"colListString\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":0,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.default\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":5},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"colListEnum\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":0,\"order\":2,\"hidden\":false,\"widgetType\":\"widget.type.default\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":4},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"colListBoolean\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":0,\"order\":3,\"hidden\":false,\"widgetType\":\"widget.type.default\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":3},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}}},\"cancelable\":false,\"originalValues\":null,\"callBeforeFormPresent\":false,\"callAfterFormBack\":false,\"callAfterFormNext\":false,\"callAfterFormFinish\":false,\"allowBack\":false,\"allowForward\":false,\"allowFinish\":false,\"refreshUI\":true,\"name\":\"Main\",\"displayName\":\"Main\",\"title\":null}]},\"validationResult\":null},\"commonProp\":{\"@id\":6,\"colString\":{\"@id\":9,\"@type\":\"org.talend.daikon.properties.property.StringProperty\",\"possibleValues2\":null,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":\"common_abc\",\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.lang.String\",\"name\":\"colString\",\"displayName\":null,\"title\":null},\"colEnum\":{\"@id\":8,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":{\"@type\":\"org.talend.daikon.serialize.FullExampleProperties$CommonProperties$ColEnum\",\"name\":\"FOO\",\"ordinal\":0},\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":{\"@type\":\"java.util.Arrays$ArrayList\",\"@items\":[{\"@type\":\"org.talend.daikon.serialize.FullExampleProperties$CommonProperties$ColEnum\",\"name\":\"FOO\",\"ordinal\":0},{\"@type\":\"org.talend.daikon.serialize.FullExampleProperties$CommonProperties$ColEnum\",\"name\":\"BAR\",\"ordinal\":1}]},\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"org.talend.daikon.serialize.FullExampleProperties.CommonProperties.ColEnum\",\"name\":\"colEnum\",\"displayName\":null,\"title\":null},\"colBoolean\":{\"@id\":7,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":{\"@type\":\"boolean\",\"value\":true},\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.lang.Boolean\",\"name\":\"colBoolean\",\"displayName\":null,\"title\":null},\"name\":\"commonProp\",\"forms\":{\"@type\":\"java.util.ArrayList\",\"@items\":[{\"@type\":\"org.talend.daikon.properties.presentation.Form\",\"subtitle\":null,\"properties\":{\"@ref\":6},\"widgetMap\":{\"colString\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":1,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.default\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":9},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"colEnum\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":2,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.default\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":8},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"colBoolean\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":3,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.default\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":7},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}}},\"cancelable\":false,\"originalValues\":null,\"callBeforeFormPresent\":false,\"callAfterFormBack\":false,\"callAfterFormNext\":false,\"callAfterFormFinish\":false,\"allowBack\":false,\"allowForward\":false,\"allowFinish\":false,\"refreshUI\":true,\"name\":\"Main\",\"displayName\":\"Main\",\"title\":null}]},\"validationResult\":null},\"filepathProp\":{\"@id\":17,\"@type\":\"org.talend.daikon.properties.property.StringProperty\",\"possibleValues2\":null,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":null,\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.lang.String\",\"name\":\"filepathProp\",\"displayName\":null,\"title\":null},\"hiddenTextProp\":{\"@id\":16,\"@type\":\"org.talend.daikon.properties.property.StringProperty\",\"possibleValues2\":null,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":null,\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.lang.String\",\"name\":\"hiddenTextProp\",\"displayName\":null,\"title\":null},\"integerProp\":{\"@id\":15,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":{\"@type\":\"int\",\"value\":1},\"size\":-1,\"occurMinTimes\":1,\"occurMaxTimes\":1,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.lang.Integer\",\"name\":\"integerProp\",\"displayName\":null,\"title\":null},\"longProp\":{\"@id\":14,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":{\"@type\":\"long\",\"value\":100},\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.lang.Long\",\"name\":\"longProp\",\"displayName\":null,\"title\":null},\"dateProp\":{\"@id\":13,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":{\"@type\":\"date\",\"value\":1475630625000},\"size\":-1,\"occurMinTimes\":1,\"occurMaxTimes\":1,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.util.Date\",\"name\":\"dateProp\",\"displayName\":null,\"title\":null},\"textareaProp\":{\"@id\":12,\"@type\":\"org.talend.daikon.properties.property.StringProperty\",\"possibleValues2\":null,\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":null,\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"java.lang.String\",\"name\":\"textareaProp\",\"displayName\":null,\"title\":null},\"schema\":{\"@id\":11,\"@type\":\"org.talend.daikon.properties.property.SchemaProperty\",\"flags\":null,\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"storedValue\":\"{\\\"type\\\":\\\"record\\\",\\\"name\\\":\\\"test\\\",\\\"fields\\\":[],\\\"include-all-fields\\\":\\\"true\\\"}\",\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"children\":{\"@type\":\"java.util.ArrayList\"},\"currentType\":\"org.apache.avro.Schema\",\"name\":\"schema\",\"displayName\":null,\"title\":null},\"validateAllCallbackCalled\":{\"formtoShow\":null,\"name\":\"validateAllCallbackCalled\",\"displayName\":\"Validate All Callbacks called\",\"title\":null},\"methodCalled\":{\"@type\":\"java.util.ArrayList\"},\"name\":\"fullexample\",\"forms\":{\"@type\":\"java.util.ArrayList\",\"@items\":[{\"@type\":\"org.talend.daikon.properties.presentation.Form\",\"subtitle\":null,\"properties\":{\"@ref\":1},\"widgetMap\":{\"stringProp\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":1,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.default\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":true,\"callValidate\":true,\"callAfter\":true,\"content\":{\"@ref\":20},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"schema\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":3,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.schema.reference\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":true,\"callBeforePresent\":false,\"callValidate\":true,\"callAfter\":true,\"content\":{\"@ref\":11},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"multipleSelectionProp\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":5,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.name.selection.widget\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":true,\"callBeforePresent\":false,\"callValidate\":true,\"callAfter\":true,\"content\":{\"@ref\":19},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"showNewForm\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":6,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.button\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":true,\"callValidate\":true,\"callAfter\":true,\"content\":{\"@ref\":18},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"commonProp\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":6,\"order\":2,\"hidden\":false,\"widgetType\":\"widget.type.default\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":6},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"hiddenTextProp\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":6,\"order\":3,\"hidden\":false,\"widgetType\":\"widget.type.hidden.text\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":true,\"callValidate\":true,\"callAfter\":true,\"content\":{\"@ref\":16},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"filepathProp\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":6,\"order\":4,\"hidden\":false,\"widgetType\":\"widget.type.file\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":true,\"callValidate\":true,\"callAfter\":true,\"content\":{\"@ref\":17},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"integerProp\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":7,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.default\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":15},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"longProp\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":8,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.default\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":14},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"dateProp\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":9,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.default\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":13},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}},\"tableProp\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":10,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.table\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":2},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}}},\"cancelable\":false,\"originalValues\":null,\"callBeforeFormPresent\":false,\"callAfterFormBack\":false,\"callAfterFormNext\":false,\"callAfterFormFinish\":false,\"allowBack\":false,\"allowForward\":false,\"allowFinish\":false,\"refreshUI\":true,\"name\":\"Main\",\"displayName\":\"Main\",\"title\":null},{\"@ref\":10},{\"@type\":\"org.talend.daikon.properties.presentation.Form\",\"subtitle\":null,\"properties\":{\"@ref\":1},\"widgetMap\":{\"textareaProp\":{\"@type\":\"org.talend.daikon.properties.presentation.Widget\",\"row\":1,\"order\":1,\"hidden\":false,\"widgetType\":\"widget.type.textArea\",\"longRunning\":false,\"deemphasize\":false,\"callBeforeActivate\":false,\"callBeforePresent\":false,\"callValidate\":false,\"callAfter\":false,\"content\":{\"@ref\":12},\"configurationValues\":{\"@type\":\"java.util.HashMap\"}}},\"cancelable\":false,\"originalValues\":null,\"callBeforeFormPresent\":false,\"callAfterFormBack\":false,\"callAfterFormNext\":false,\"callAfterFormFinish\":false,\"allowBack\":false,\"allowForward\":false,\"allowFinish\":false,\"refreshUI\":true,\"name\":\"Advanced\",\"displayName\":\"Advanced\",\"title\":null}]},\"validationResult\":null}";
        FullExampleProperties object = Properties.Helper.fromSerializedPersistent(beforeFormsTransient,
                FullExampleProperties.class).object;
        FullExampleTestUtil.comparePropertiesValue(fullExampleProperties, object);
    }
}
