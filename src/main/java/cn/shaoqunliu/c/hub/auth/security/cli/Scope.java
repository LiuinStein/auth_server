package cn.shaoqunliu.c.hub.auth.security.cli;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;

public class Scope implements GrantedAuthority {

    private final String repository;
    private final String action;


    /**
     * @param scope repository:action, such as liuinstein/ubuntu:pull,push
     *              the action part can only be "pull", "push", "pull,push" or nothing
     */
    public Scope(String scope) throws BadCredentialsException {
        String[] s = scope.split(":");
        if (s.length != 3 || !s[0].toLowerCase().equals("repository")) {
            throw new BadCredentialsException("Bad scope");
        }
        repository = s[1];
        String sa = s[2];
        StringBuilder act = new StringBuilder();
        if (sa.contains("pull")) {
            act.append("pull");
        }
        if (sa.contains("push")) {
            act.append(act.length() == 0 ? "push" : ",push");
        }
        if (act.length() == 0) {
            throw new BadCredentialsException("Bad scope");
        }
        action = act.toString();
    }

    @Override
    public String getAuthority() {
        return repository + ':' + action;
    }

    public String getRepository() {
        return repository;
    }

    public String getAction() {
        return action;
    }

    public boolean contains(Scope b) {
        return repository.equals(b.repository) &&
                action.contains(b.action);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Scope &&
                ((Scope) obj).getAuthority().equals(getAuthority());
    }
}
