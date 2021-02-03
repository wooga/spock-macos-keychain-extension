package com.wooga.spock.extensios.security.interceptor

import com.wooga.spock.extensios.security.Keychain
import com.wooga.spock.extensios.security.MacOsKeychain
import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.NodeInfo
import spock.lang.Specification

abstract class KeychainInterceptor<T extends NodeInfo> extends AbstractMethodInterceptor {
    protected final Keychain metadata
    protected T info

    KeychainInterceptor(Keychain metadata) {
        super
        this.metadata = metadata
    }

    MacOsKeychain createKeychain(IMethodInvocation invocation) {
        File keychainDestination = File.createTempDir("test", "keychain")
        File keychainLocation = new File(keychainDestination, metadata.fileName())
        def keychain = MacOsKeychain.create(keychainLocation, metadata.password())
        keychain.withSettings {
            it.lockWhenSystemSleeps = metadata.lockWhenSystemSleeps()
            it.timeout = metadata.timeout()
        }

        if(!metadata.unlockKeychain()) {
            keychain.lock()
        }

        keychain
    }

    abstract void install(T info)

}
