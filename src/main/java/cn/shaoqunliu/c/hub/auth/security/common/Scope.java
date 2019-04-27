package cn.shaoqunliu.c.hub.auth.security.common;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;

public final class Scope implements GrantedAuthority {

    public enum Action {
        NULL(0, ""),
        PULL(1, "pull"),
        PUSH(2, "push"),
        BOTH(3, "pull,push");

        private int val;
        private String str;

        Action(int v, String s) {
            val = v;
            str = s;
        }

        public int value() {
            return val;
        }

        public static Action valueOf(int v) {
            if (v < 0 || v > 3) {
                throw new BadCredentialsException("Bad action");
            }
            Action[] mapper = {Action.NULL, Action.PULL, Action.PUSH, Action.BOTH};
            return mapper[v];
        }

        @Override
        public String toString() {
            return str;
        }
    }

    private final String repository;
    private final Action action;

    public Scope(String repository, Action action) {
        this.repository = repository;
        this.action = action;
    }

    public Scope(String repository, int action) throws BadCredentialsException {
        if (action < 0 || action > 3) {
            throw new BadCredentialsException("Bad scope");
        }
        this.repository = repository;
        this.action = Action.valueOf(action);
    }

    /**
     * @param scope repository:image:action, such as repository:liuinstein/ubuntu:pull,push
     *              the action part can only be "pull", "push", "pull,push" or nothing
     */
    public Scope(String scope) throws BadCredentialsException {
        String[] s = scope.split(":");
        if (s.length != 3 || !s[0].toLowerCase().equals("repository")) {
            throw new BadCredentialsException("Bad scope");
        }
        repository = s[1];
        String sa = s[2];
        int act = 0;
        if (sa.contains("pull")) {
            act += 1;
        }
        if (sa.contains("push")) {
            act += 2;
        }
        action = Action.valueOf(act);
    }

    @Override
    public String getAuthority() {
        return repository + ':' + action.toString();
    }

    public String getRepository() {
        return repository;
    }

    public Action getAction() {
        return action;
    }

    public boolean contains(Scope b) {
        return repository.equals(b.repository) &&
                action.value() >= b.action.value();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Scope &&
                ((Scope) obj).getAuthority().equals(getAuthority());
    }
}
