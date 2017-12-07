package org.talend.logging.audit.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.talend.logging.audit.AuditEvent;
import org.talend.logging.audit.Context;

/**
 *
 */
public class ProxyEventAuditLogger implements InvocationHandler {

    private final AuditLoggerBase auditLoggerBase;

    public ProxyEventAuditLogger(AuditLoggerBase auditLoggerBase) {
        this.auditLoggerBase = auditLoggerBase;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        EventDefinition event = getEvent(method);

        if (event == null) {
            throw new IllegalArgumentException("Unknown event " + method.getName());
        }

        List<Object> allArgs = getAllArgs(args);

        validateArgs(allArgs);

        Context context = Utils.getSpecificClassParam(allArgs, Context.class);
        Throwable throwable = Utils.getSpecificClassParam(allArgs, Throwable.class);

        auditLoggerBase.log(event.getLogLevel(), event.getCategory(), context, throwable, event.getMessage());
        return null;
    }

    private static void validateArgs(List<Object> args) {
        if (args.size() > 2) {
            throw new IllegalArgumentException("Unexpected number of arguments");
        }

        boolean hasContext = false;
        boolean hasThrowable = false;

        for (Object obj : args) {
            if (obj instanceof Throwable) {
                if (hasThrowable) {
                    throw new IllegalArgumentException("Two instances of Throwable have been passed as arguments");
                }
                hasThrowable = true;
            } else if (obj instanceof Context) {
                if (hasContext) {
                    throw new IllegalArgumentException("Two instances of Context have been passed as arguments");
                }
                hasContext = true;
            } else {
                throw new IllegalArgumentException("Unsupported argument type " + obj.getClass().toString());
            }
        }
    }

    private static EventDefinition getEvent(Method method) {
        AuditEvent event = method.getAnnotation(AuditEvent.class);
        if (event == null) {
            return null;
        }

        final EventDefinition answer = new EventDefinition(method.getName());

        answer.setCategory(event.category());
        answer.setMessage("".equals(event.message()) ? null : event.message());
        answer.setLogLevel(event.level());

        return answer;
    }

    private static List<Object> getAllArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return new ArrayList<>();
        }

        List<Object> answer = new ArrayList<>(Arrays.asList(args));

        int lastElemIndex = answer.size() - 1;
        Object lastElem = answer.get(lastElemIndex);

        if (lastElem.getClass().isArray()) {
            answer.remove(lastElemIndex);
            answer.addAll(Arrays.asList((Object[]) lastElem));
        }

        return answer;
    }
}
