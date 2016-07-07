package org.talend.daikon.annotation;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * A marker to check if annotation has default value or not
 * @see Call#service() 
 */
public abstract class DefaultHystrixCommand extends HystrixCommand {

    protected DefaultHystrixCommand(HystrixCommandGroupKey group) {
        super(group);
    }
}
