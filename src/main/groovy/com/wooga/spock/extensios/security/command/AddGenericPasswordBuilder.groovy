package com.wooga.spock.extensios.security.command

import com.wooga.spock.extensios.security.MacOsKeychain

class AddGenericPasswordBuilder {
    final MacOsKeychain keychain

    String account
    String creator
    String type
    String kind
    String value
    String label

    String service
    String password

    Boolean accessFromAnyApplication
    Boolean updateItem
    List<File> appAccess

    AddGenericPasswordBuilder(MacOsKeychain keychain) {
        this.keychain = keychain
    }

    AddGenericPasswordBuilder withAccount(String account) {
        this.account = account
        this
    }

    AddGenericPasswordBuilder withCreator(String creator) {
        this.creator = creator
        this
    }

    AddGenericPasswordBuilder withType(String type) {
        this.type = type
        this
    }

    AddGenericPasswordBuilder withKind(String kind) {
        this.kind = kind
        this
    }

    AddGenericPasswordBuilder withValue(String value) {
        this.value = value
        this
    }

    AddGenericPasswordBuilder withLabel(String label) {
        this.label = label
        this
    }

    AddGenericPasswordBuilder withService(String service) {
        this.service = service
        this
    }

    AddGenericPasswordBuilder withPassword(String password) {
        this.password = password
        this
    }

    AddGenericPasswordBuilder withAccessFromAnyApplication(Boolean value) {
        this.accessFromAnyApplication = value
        this
    }
}
