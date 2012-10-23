package play.deps;


import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.plugins.version.AbstractVersionMatcher;
import org.apache.ivy.plugins.version.VersionMatcher;
import org.hibernate.hql.CollectionSubqueryFactory;

public class LatestVersionPatternMatcher extends AbstractVersionMatcher implements VersionMatcher {

    private static List<String> suffixes = Arrays.asList("dev", "RC");
    
    @Override
    public boolean isDynamic(final ModuleRevisionId askedMrid) {
        return CollectionUtils.countMatches(suffixes, new Predicate() {
            
            @Override
            public boolean evaluate(Object arg0) {
                String suffix = (String) arg0;
                return askedMrid.getRevision().endsWith(getSuffixPattern(suffix));
            }
        }) == 1;
    }

    @Override
    public boolean accept(ModuleRevisionId askedMrid, ModuleRevisionId foundMrid) {
        if (foundMrid.getRevision().contains("working@"))
            return true;
        
        String askedRevision = askedMrid.getRevision();
        String foundRevision = foundMrid.getRevision();
        if (hasLatestPatternMatcherSuffix(askedRevision)) {
            String suffixPattern = getLatestPatternMatcherSuffix(askedRevision);
            String askedVersion = askedRevision.substring(0, askedRevision.indexOf(suffixPattern));
            String foundVersion = foundRevision.contains(suffixPattern) ? foundRevision.substring(0, askedRevision.indexOf(suffixPattern)): foundRevision;
            return askedVersion.equals(foundVersion);
        }
        
        return false;
    }
    
    private static boolean hasLatestPatternMatcherSuffix(final String revision) {
        return CollectionUtils.countMatches(suffixes, new Predicate() {
            
            @Override
            public boolean evaluate(Object arg0) {
                String suffix = (String) arg0;
                return revision.contains(getSuffixPattern(suffix));
            }
        }) == 1;
    }

    private static String getLatestPatternMatcherSuffix(final String revision) {
        return getSuffixPattern(getLatestSuffix(revision));
    }

    private static String getLatestSuffix(final String revision) {
        return (String) CollectionUtils.find(suffixes, new Predicate() {
            
            @Override
            public boolean evaluate(Object arg0) {
                String suffix = (String) arg0;
                return revision.contains(getSuffixPattern(suffix));
            }
        });
    }
    
    private static String getSuffixPattern(String suffix) {
        return String.format("-%s", suffix);
    }

    @Override
    public String getName() {
        return "LatestPatternVersionMatcher";
    }
}
