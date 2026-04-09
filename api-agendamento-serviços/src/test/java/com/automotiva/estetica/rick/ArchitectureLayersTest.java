package com.automotiva.estetica.rick;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@AnalyzeClasses(packages = "com.automotiva.estetica.rick", importOptions = {ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class})
class ArchitectureLayersTest {

    // noinspection unused
    @ArchTest
    static final ArchRule clean_architecture_layers = layeredArchitecture().consideringOnlyDependenciesInLayers()
            .layer("Application").definedBy("..application..").layer("Domain").definedBy("..domain..")
            .layer("Infrastructure").definedBy("..infrastructure..").whereLayer("Application")
            .mayOnlyAccessLayers("Domain").whereLayer("Domain").mayNotAccessAnyLayer().whereLayer("Infrastructure")
            .mayOnlyAccessLayers("Application", "Domain");

    // noinspection unused
    @ArchTest
    static final ArchRule legacy_packages_must_not_exist = noClasses().should().resideInAnyPackage("..adapter..",
            "..port..", "..infrastructure.persistence..");

    // noinspection unused
    @ArchTest
    static final ArchRule adapter_suffix_must_not_exist = noClasses().that()
            .resideInAnyPackage("..application..", "..domain..", "..infrastructure..").should()
            .haveSimpleNameEndingWith("Adapter");

    // noinspection unused
    @ArchTest
    static final ArchRule port_suffix_must_not_exist = noClasses().that()
            .resideInAnyPackage("..application..", "..domain..", "..infrastructure..").should()
            .haveSimpleNameEndingWith("Port");

    // noinspection unused
    @ArchTest
    static final ArchRule assembler_classes_must_reside_in_application_assembler = classes().that()
            .haveSimpleNameEndingWith("Assembler").should().resideInAPackage("..application.assembler..");

    // noinspection unused
    @ArchTest
    static final ArchRule application_assembler_package_must_contain_only_assembler_classes = classes().that()
            .resideInAPackage("..application.assembler..").should().haveSimpleNameEndingWith("Assembler");

    @Test
    void arch_rules_are_declared() {
        assertNotNull(clean_architecture_layers);
        assertNotNull(legacy_packages_must_not_exist);
        assertNotNull(adapter_suffix_must_not_exist);
        assertNotNull(port_suffix_must_not_exist);
        assertNotNull(assembler_classes_must_reside_in_application_assembler);
        assertNotNull(application_assembler_package_must_contain_only_assembler_classes);
    }
}
