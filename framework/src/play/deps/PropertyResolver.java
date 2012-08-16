package play.deps;

public class PropertyResolver {

    public String resolveEnv(String envVar) {
        return System.getenv(envVar);
    }
    
    public String resolveSystem(String sysVar) {
        return System.getProperty(sysVar);
    }
}
