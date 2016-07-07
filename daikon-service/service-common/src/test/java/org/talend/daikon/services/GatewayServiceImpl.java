package org.talend.daikon.services;

import org.talend.daikon.annotation.Call;
import org.talend.daikon.annotation.ServiceImplementation;

@ServiceImplementation
public class GatewayServiceImpl implements GatewayService {

    @Override
    @Call(service = TestService.class, operation = "sayHi")
    public native String say();

    @Override
    @Call(service = TestService.class, operation = "sayHiWithMyName")
    public native String sayMyName(String name);

    @Override
    @Call(service = TestService.class, operation = "missingMethod")
    public native String missingOperation();

    @Override
    @Call(service = TestService.class, operation = "missingMethod")
    public native String missingService();

    @Override
    @Call(using = CustomCallCommand.class)
    public String custom() {
        return "MyString";
    }

}
