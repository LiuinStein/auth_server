package cn.shaoqunliu.c.hub.auth.vo;

import cn.shaoqunliu.c.hub.auth.security.common.Scope;

import java.util.ArrayList;
import java.util.List;

public class DockerAccessDetails {

    private String type;
    private String name;
    private List<String> actions;

    public DockerAccessDetails(Scope scope) {
        type = "repository";
        name = scope.getRepository();
        actions = new ArrayList<>();
        setActions(scope.getAction().toString());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public void setActions(String actions) {
        if (actions.contains("pull")) {
            this.actions.add("pull");
        }
        if (actions.contains("push")) {
            this.actions.add("push");
        }
    }
}
