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
package org.talend.daikon.exception.json;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.exception.error.ErrorCode;

public class JsonErrorCodeTest {

    @Test
    public void testDeserialize() throws IOException {
        // check that the CodeError serialization can be deserialized into an JsonCodeError
        TalendRuntimeException talendRuntimeException = new TalendRuntimeException(CommonErrorCodes.MISSING_I18N_TRANSLATOR,
                ExceptionContext.build().put("key", "the key").put("baseName", "the baseName"));
        StringWriter writer = new StringWriter();
        talendRuntimeException.writeTo(writer);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonErrorCode deserializedCode = objectMapper.reader(JsonErrorCode.class).readValue(writer.toString());
        ErrorCode expectedCode = talendRuntimeException.getCode();
        assertEquals(expectedCode.getCode(), deserializedCode.getCode());
        assertEquals(expectedCode.getGroup(), deserializedCode.getGroup());
        assertEquals(expectedCode.getProduct(), deserializedCode.getProduct());
        assertThat(expectedCode.getExpectedContextEntries(),
                containsInAnyOrder(deserializedCode.getExpectedContextEntries().toArray()));
        assertThat(talendRuntimeException.getContext().entries(),
                containsInAnyOrder(deserializedCode.getContext().entrySet().toArray()));
    }

}
