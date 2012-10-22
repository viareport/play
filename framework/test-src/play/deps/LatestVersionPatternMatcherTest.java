package play.deps;

import static org.junit.Assert.*;

import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.junit.Test;

public class LatestVersionPatternMatcherTest {

    private LatestVersionPatternMatcher vm = new LatestVersionPatternMatcher();

    @Test
    public void testAcceptLatestVersionPatternsWithSameVersion() {
        assertAccept("1.0.0-dev", "1.0.0-dev");
        assertAccept("1.0.0-dev", "1.0.0-dev200");
        assertAccept("1.0.0-dev", "1.0.0");
        assertAccept("1.0.0-dev", "working@invativLocal");
    }
    
    @Test
    public void testAcceptLatestVersionPatternsWithDifferentVersion() {
        assertReject("1.0.1-dev", "1.0.0-dev");
        assertReject("1.0.1-dev", "1.0.0");
    }
    
    @Test
    public void testMatcherDetectDynamicRevisionOnSuffix() {
        assertDynamic("1.0.0-dev", true);
        assertDynamic("1.0.0-dev262", false);
        assertDynamic("1.0.0", false);
    }
        
    // assertion helper methods

    private void assertDynamic(String askedVersion, boolean b) {
        assertEquals(b, vm.isDynamic(ModuleRevisionId.newInstance("org", "name", askedVersion)));
    }

    private void assertAccept(String askedVersion, String depVersion) {
        assertTrue(String.format("Expected accept %s ==> %s, but was rejected.", askedVersion, depVersion), vm.accept(ModuleRevisionId.newInstance("org", "name", askedVersion),
            ModuleRevisionId.newInstance("org", "name", depVersion)));
    }
    
    private void assertReject(String askedVersion, String depVersion) {
        assertFalse(String.format("Expected reject %s ==> %s, but was accepted.", askedVersion, depVersion), vm.accept(ModuleRevisionId.newInstance("org", "name", askedVersion),
            ModuleRevisionId.newInstance("org", "name", depVersion)));
    }
    
    // assertion helper methods
    private void assertNeed(String askedVersion, String foundRevision, boolean b) {
        assertEquals(b, vm.needModuleDescriptor(ModuleRevisionId.newInstance("org", "name", askedVersion), ModuleRevisionId.newInstance("org", "name", foundRevision)));
    }
}
