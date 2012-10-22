package play.deps;


import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.plugins.version.AbstractVersionMatcher;
import org.apache.ivy.plugins.version.VersionMatcher;

public class LatestVersionPatternMatcher extends AbstractVersionMatcher implements VersionMatcher {

    @Override
    public boolean isDynamic(ModuleRevisionId askedMrid) {
        return askedMrid.getRevision().endsWith("-dev");
    }

    @Override
    public boolean accept(ModuleRevisionId askedMrid, ModuleRevisionId foundMrid) {
        if (foundMrid.getRevision().contains("working@"))
            return true;
        
        String askedRevision = askedMrid.getRevision();
        String foundRevision = foundMrid.getRevision();
        if (askedRevision.contains("-dev")) {
            String askedVersion = askedRevision.substring(0, askedRevision.indexOf("-dev"));
            String foundVersion = foundRevision.contains("-dev") ? foundRevision.substring(0, askedRevision.indexOf("-dev")): foundRevision;
            return askedVersion.equals(foundVersion);
        }
        
        return true;
    }

    @Override
    public String getName() {
        return "LatestPatternVersionMatcher";
    }
}
