# Daikon extensions for Spring Security
http://www.talend.com


![alt text](https://www.talend.com/wp-content/uploads/2016/07/talend-logo.png "Talend")

This module contains Spring Security enhancements for Talend products.

## @RequiresAuthority

The `@RequiresAuthority` annotation allows to mark methods that requires the current user has the permission (*authority*) to execute the method. 

This annotation can be used on all methods in a `@Component` (`@RequestMapping` annotated methods **but** not only).
 
### Usage

#### Simple example
Let's start with a simple example:

```java
import org.springframework.stereotype.Component;
import org.talend.dataprep.access.RequiresAuthority;

@Component
public class MyRestrictedAccessClass {
    
    public String getMySecret() {
        return "something very secret";
    }
}
```

To add permission checks, you only need to add the `@RequiresAuthority` annotation:

```java
import org.springframework.stereotype.Component;
import org.talend.dataprep.access.RequiresAuthority;

@Component
public class MyRestrictedAccessClass {

    @RequiresAuthority("SECRET_ACCESS")
    public String getMySecret() {
        return "something very secret";
    }
}
```

The user that call the `getMySecret` method is required to at least have the `SECRET_ACCESS` authority.
If this requirement is not met, a `org.springframework.security.access.AccessDeniedException` is thrown.

#### Customize behavior in case of access denial

By default, `@RequiresAuthority` throws an exception in case of access denial. 

```java
import org.springframework.stereotype.Component;
import org.talend.dataprep.access.RequiresAuthority;
import org.talend.dataprep.access.AccessDeniedDefaults;

@Component
public class MyRestrictedAccessClass {

    @RequiresAuthority(authority = "SECRET_ACCESS", 
                       onDeny = AccessDeniedDefaults.EmptyString.class)
    public String getMySecret() {
        return "something very secret";
    }
}
```

Now, with the `onDeny` parameter, in case of access denial, the user will get an empty string instead of a `AccessDeniedException`.
