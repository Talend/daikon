Extensions for Hystrix
================================================

This module provides extensions for Hystrix commands.

# Using commands



```java
import org.talend.daikon.hystrix.GenericCommand;
import org.springframework.http.HttpStatus.OK;
import org.talend.daikon.hystrix.Defaults;

public class MyCommand extends GenericCommand<String> {

    public MyCommand() {
        super(() -> "MyGroup");
        on(HttpStatus.OK).then(Defaults.pipeStream());
    }
}
```

