package play.deps;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.IvyNode;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.resolver.DependencyResolver;
import org.apache.ivy.plugins.resolver.FileSystemResolver;
import org.junit.Test;

import play.Play;
import play.PlayBuilder;

public class LocalDepsTest {
    
    @Test
    public void testLocalDepsResolveSnapshots() throws Exception {
        new PlayBuilder().build();
        // Paths
        File application = new File("test-resources/application-test");
        System.setProperty("application.path", application.getAbsolutePath());
        File framework = Play.frameworkPath;
        File userHome  = new File(System.getProperty("user.home"));

        DependenciesManager deps = new DependenciesManager(application, framework, userHome);
        
        ResolveReport report = deps.resolve(false);
        IvyNode dependency = (IvyNode) report.getDependencies().get(0);
        assertFalse("Expected resolved dependency, found " + dependency.getProblemMessage(), dependency.hasProblem());
    }
    
    @Test
    public void testEnvironmentVariableSupport() throws Exception {
        IvySettings ivySettings = new IvySettings();
        File application = new File("test-resources/application-envvar-test");
        new SettingsParser(null, new PropertyResolverStub()).parse(ivySettings, new File(application, "conf/dependencies.yml"));
        DependencyResolver resolver = ivySettings.getResolver("inativLocal");
        if (resolver instanceof FileSystemResolver) {
            FileSystemResolver fsResolver = (FileSystemResolver) resolver;
            String artifactPattern = (String) fsResolver.getArtifactPatterns().get(0);
            assertNotNull(artifactPattern);
            assertTrue(artifactPattern + " should contain " + PropertyResolverStub.ENV_RESOLVED_VALUE, artifactPattern.contains(PropertyResolverStub.ENV_RESOLVED_VALUE));
        }
    }
    
    public static class PropertyResolverStub extends PropertyResolver {

        public static final String SYSTEM_RESOLVED_VALUE = "/SystemResolvedValue";
        public static final String ENV_RESOLVED_VALUE = "/EnvResolvedValue";

        @Override
        public String resolveEnv(String envVar) {
            return ENV_RESOLVED_VALUE;
        }

        @Override
        public String resolveSystem(String sysVar) {
            return SYSTEM_RESOLVED_VALUE;
        }
        
    }
}
