package com.wooga.spock.extensios.security

import com.wooga.spock.extensios.security.command.AddGenericPasswordBuilder
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

class MacOsKeychain {

    static class Settings {
        Boolean lockWhenSystemSleeps
        Integer timeout

        Boolean getLockAfterTimeout() {
            timeout >= 0
        }

        Settings(Boolean lockWhenSystemSleeps, Integer timeout) {
            this.lockWhenSystemSleeps = lockWhenSystemSleeps
            this.timeout = timeout
        }

        Iterable<String> toArgs() {
            def args = []
            if (lockWhenSystemSleeps) {
                args << "-l"
            }

            if (timeout > -1) {
                args << "-t" << timeout.toString()
            }
            args
        }

        private static fromOutput(String output) {
            Boolean lockWhenSystemSleep = output.contains("lock-on-sleep")
            Integer timeout = -1
            def m = (output.trim() =~ /.*timeout=(\d+)s.*/)
            if (m.matches()) {
                timeout = Integer.parseInt(m.group(1))
            }

            new Settings(lockWhenSystemSleep, timeout)
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (!(o instanceof Settings)) return false

            Settings settings = (Settings) o

            if (timeout != settings.timeout) return false
            if (lockWhenSystemSleeps != settings.lockWhenSystemSleeps) return false

            return true
        }

        int hashCode() {
            int result
            result = lockWhenSystemSleeps.hashCode()
            result = 31 * result + timeout.hashCode()
            return result
        }
    }

    final File location
    final String password

    MacOsKeychain(File location, String password) {
        this.location = location
        this.password = password
    }

    Boolean unlock() {
        try {
            callSecurity("unlock-keychain", "-p", "'${this.password}'", this.location.path)
            return true
        } catch (IOException ignored) {
            return false
        }
    }

    Boolean lock() {
        try {
            callSecurity("lock-keychain", this.location.path)
            return true
        } catch (IOException ignored) {
            return false
        }
    }

    Boolean getLockWhenSystemSleeps() {
        getSettings().lockWhenSystemSleeps
    }

    Boolean setLockWhenSystemSleeps(Boolean value) {
        withSettings {
            it.lockWhenSystemSleeps = value
        }
    }

    Integer getTimeout() {
        getSettings().timeout
    }

    Boolean setTimeout(Integer timeout) {
        withSettings {
            it.timeout = timeout
        }
    }

    Boolean getLockAfterTimeout() {
        getSettings().lockAfterTimeout
    }

    Boolean withSettings(@ClosureParams(value = FromString.class, options = ["com.wooga.spock.extensios.security.MacOsKeychain.Settings"]) Closure action) {
        def settings = getSettings()

        action.call(settings)
        setSettings(settings)
    }

    Settings getSettings() {
        def output = callSecurity("show-keychain-info", this.location.path)
        Settings.fromOutput(output)
    }

    Boolean setSettings(Settings settings) {
        try {
            callSecurity("set-keychain-settings", *settings.toArgs(), this.location.path)
            return true
        } catch (IOException ignored) {
            return false
        }
    }

    AddGenericPasswordBuilder addGenericPassword() {
        new AddGenericPasswordBuilder(this)
    }

    Boolean delete() {
        callSecurity("delete-keychain", this.location.path)
        this.location.delete()
    }

    Boolean exists() {
        this.location.exists()
    }

    static String callSecurity(String command, String... arguments) {
        def processBuilder = new ProcessBuilder()
        def args = ["security", command]
        args.addAll(arguments)
        processBuilder.command(args)
        def process = processBuilder.start()

        def out = new StringBuffer()
        def err = new StringBuffer()
        process.consumeProcessOutput(out, err)
        if (out.size() > 0) println out
        if (err.size() > 0) println err

        def result = process.waitFor()
        if (result != 0) {
            def message = ""
            if (err.size() > 0) {
                message = err.toString()
            }
            throw new IOException("Security command ${command} failed: ${message}")
        }

        err.toString()
    }

    static MacOsKeychain create(File location, String password) {
        callSecurity("create-keychain", "-p", "'${password}'", location.path)
        new MacOsKeychain(location, password)
    }

    @Override
    String toString() {
        return "MacOsKeychain{" +
                "location=" + location +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof MacOsKeychain)) return false

        MacOsKeychain that = (MacOsKeychain) o

        if (location != that.location) return false
        if (password != that.password) return false

        return true
    }

    int hashCode() {
        int result
        result = (location != null ? location.hashCode() : 0)
        result = 31 * result + (password != null ? password.hashCode() : 0)
        return result
    }
}
