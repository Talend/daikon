package org.talend.logging.audit.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.talend.logging.audit.Context;
import org.talend.logging.audit.LogLevel;

/**
 *
 */
public class ProxyAuditLogger implements InvocationHandler {

    private final AuditLoggerBase auditLoggerBase;

    public ProxyAuditLogger(AuditLoggerBase auditLoggerBase) {
        this.auditLoggerBase = auditLoggerBase;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final List<String> nameParts = splitByCamelCase(method.getName());

        final LogLevel logLevel;
        final String category;

        if (nameParts.size() == 1) {
            category = (String) args[0];
            logLevel = LogLevel.valueOf(nameParts.get(0).toUpperCase());
        } else if (nameParts.size() == 2) {
            category = nameParts.get(0);
            logLevel = LogLevel.valueOf(nameParts.get(1).toUpperCase());
        } else {
            throw new UnsupportedOperationException("Unknown method " + method);
        }

        Context context = Utils.getSpecificClassParam(args, Context.class);
        Throwable throwable = Utils.getSpecificClassParam(args, Throwable.class);
        String message = getMessage(method, args);

        auditLoggerBase.log(logLevel, category, context, throwable, message);
        return null;
    }

    private static String getMessage(Method method, Object[] args) {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (String.class.equals(paramTypes[paramTypes.length - 1])) {
            return String.class.cast(args[args.length - 1]);
        }
        return null;
    }

    private static List<String> splitByCamelCase(String str) {
        final List<String> answer = new ArrayList<>();
        if (str.isEmpty()) {
            return answer;
        }

        int lastCut = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i == 0) {
                    continue;
                }
                answer.add(str.substring(lastCut, i));
                lastCut = i;
            }
        }

        answer.add(str.substring(lastCut));
        return answer;
    }
}
