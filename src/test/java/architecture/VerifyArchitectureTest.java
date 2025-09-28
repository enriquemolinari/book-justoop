package architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class VerifyArchitectureTest {

    @Test
    public void appShouldOnlyDependOnAppApi() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages("app..", "spring..", "main");
        classes().that().resideInAPackage("app").should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("app.api", "app", "java..", "javax..",
                        "lombok..",
                        "jakarta..",
                        "dev.paseto..")
                .check(importedClasses);
    }

    @Test
    public void webPackagesOutSideAppShouldOnlyDependOnAppApi() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages("app..", "spring..", "main");
        classes().that().resideInAPackage("spring.web").should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("app.api", "spring.web", "java..",
                        "javax..",
                        "org.springframework..")
                .check(importedClasses);
    }
}
