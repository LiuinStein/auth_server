package cn.shaoqunliu.c.hub.auth.vo.authority;

import java.util.List;

public class Ownership {

    private List<String> namespace;
    private List<String> repository;

    public List<String> getNamespace() {
        return namespace;
    }

    public void setNamespace(List<String> namespace) {
        this.namespace = namespace;
    }

    public List<String> getRepository() {
        return repository;
    }

    public void setRepository(List<String> repository) {
        this.repository = repository;
    }

    public void addNamespace(String namespace) {
        if (!this.namespace.contains(namespace)) {
            this.namespace.add(namespace);
        }
    }

    public void addRepository(String repository) {
        if (!this.repository.contains(repository)) {
            this.repository.add(repository);
        }
    }
}
