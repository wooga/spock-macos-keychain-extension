package com.wooga.spock.extensios.security

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class MacOsKeychainSpec extends Specification {
    def path = new File(File.createTempDir("test", "keychain"), "test.keychain")
    def keychain = MacOsKeychain.create(path, "12345")

    def cleanup() {
        keychain.delete()
    }

    @Ignore("This case can't be tested as macOS prompts for a password when keychain is locked")
    def "convenient method lock locks keychain"() {
        when:
        keychain.lock()

        then:
        !keychain.setLockWhenSystemSleeps(false)
    }

    def "convenient method unlock unlocks keychain"() {
        when:
        assert keychain.unlock()

        then:
        keychain.setLockWhenSystemSleeps(true)
    }

    def "convenient method getSettings sets keychain settings"() {
        given:
        keychain.unlock()
        def settings = keychain.getSettings()

        expect:
        settings.timeout == 300
        settings.lockWhenSystemSleeps
        settings.lockAfterTimeout
    }

    def "convenient method setSettings sets keychain settings"() {
        given: "unlocked keychain"
        keychain.unlock()
        def oldSettings = keychain.getSettings()
        and: "a settings object"
        def settings = new MacOsKeychain.Settings(true, 1000)
        assert oldSettings != settings

        when:
        keychain.setSettings(settings)

        then:
        def newSettings = keychain.getSettings()
        newSettings == settings
    }

    @Unroll()
    def "property #property returns value from settings"() {
        given: "unlocked keychain"
        keychain.unlock()
        def oldSettings = keychain.getSettings()
        and: "a settings object"
        assert oldSettings != settings
        keychain.setSettings(settings)
        assert oldSettings.getProperty(property) != settings.getProperty(property)

        when:
        def value = keychain.getProperty(property)

        then:
        value == settings.getProperty(property)

        where:
        property               | settings
        "lockWhenSystemSleeps" | new MacOsKeychain.Settings(false, 1000)
        "timeout"              | new MacOsKeychain.Settings(false, 100)
    }
}
