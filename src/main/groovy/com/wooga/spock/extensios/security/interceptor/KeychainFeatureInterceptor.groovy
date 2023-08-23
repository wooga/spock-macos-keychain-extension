package com.wooga.spock.extensios.security.interceptor

import com.wooga.security.MacOsKeychain
import groovy.transform.InheritConstructors
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.MethodInfo

import java.lang.reflect.Parameter

@InheritConstructors
class KeychainFeatureInterceptor extends KeychainInterceptor<FeatureInfo> {

    private MacOsKeychain currentKeychain

    //Spock 2 does away with the unpredictable argument array size
    //https://spockframework.org/spock/docs/2.3/extensions.html#_injecting_method_parameters
    private static void injectKeychain(IMethodInvocation invocation, MacOsKeychain keychain) {
        Map<Parameter, Integer> parameters = [:]
        invocation.method.reflection.parameters.eachWithIndex { parameter, i ->
            parameters << [(parameter): i]
        }

        parameters.findAll { MacOsKeychain.equals(it.key.type) }
                .each { parameter, i ->
                    if(!invocation.arguments[i] || invocation.arguments[i] == MethodInfo.MISSING_ARGUMENT) {
                        invocation.arguments[i] = keychain
                    }
                }
    }

    //execute feature
    @Override
    void interceptFeatureMethod(IMethodInvocation invocation) throws Throwable {
        currentKeychain = createKeychain(invocation)
        injectKeychain(invocation, currentKeychain)
        try {
            invocation.proceed()
        } finally {
            currentKeychain.delete()
        }
    }

    //NEW ITERATION
    @Override
    void interceptIterationExecution(IMethodInvocation invocation) throws Throwable {
        invocation.proceed()
    }

    @Override
    void interceptSetupMethod(IMethodInvocation invocation) throws Throwable {
        invocation.proceed()
        invocation.spec.setupInterceptors.remove(this)
    }

    //SETUP FEATURE
    @Override
    void interceptFeatureExecution(IMethodInvocation invocation) throws Throwable {
        invocation.spec.addSetupInterceptor(this)
        invocation.proceed()
    }

    @Override
    void install(FeatureInfo info) {
        info.addInterceptor(this)
        info.addIterationInterceptor(this)
        info.featureMethod.addInterceptor(this)
    }
}
