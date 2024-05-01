package architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class VerifyArchitectureTest {

    @Test
    public void modelShouldOnlyDependOnModelApi() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages("model..", "spring..", "main");
        classes().that().resideInAPackage("model").should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("model.api", "model", "java..", "javax..",
                        "lombok..",
                        "jakarta..",
                        "dev.paseto..")
                .check(importedClasses);
    }
	
    @Test
    public void webPackagesOutSideModelShouldOnlyDependOnModelApi() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages("model..", "spring..", "main");
        classes().that().resideInAPackage("spring.web").should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("model.api", "spring.web", "java..",
                        "javax..",
                        "org.springframework..")
                .check(importedClasses);
    }
}
