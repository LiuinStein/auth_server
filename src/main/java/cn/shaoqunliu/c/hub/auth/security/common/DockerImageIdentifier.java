package cn.shaoqunliu.c.hub.auth.security.common;

public class DockerImageIdentifier {

    private String namespace;
    private String repository;
    private String tag;

    public DockerImageIdentifier(String identifier) {
        int a, b;
        a = identifier.indexOf('/');
        if (a != -1) {
            namespace = identifier.substring(0, a);
        }
        b = identifier.indexOf(':');
        if (b == -1 && a + 1 != identifier.length()) {
            repository = identifier.substring(a + 1);
        } else if (b > a + 1) {
            repository = identifier.substring(a + 1, b);
            tag = identifier.substring(b);
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public String getRepository() {
        return repository;
    }

    public String getTag() {
        return tag;
    }

    public String getFullRepositoryName() {
        return namespace + "/" + repository;
    }

    public String getFullIdentifier() {
        return getFullRepositoryName() + ":" +tag;
    }
}
