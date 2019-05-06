package cn.shaoqunliu.c.hub.auth.security.mgr.token;

import cn.shaoqunliu.c.hub.auth.vo.MgrAccessDetails;

public class MgrSecondAuthenticationToken extends MgrAbstractAuthenticationToken {

    public enum ResourceType {
        NAMESPACE(0, "namespace"),
        REPOSITORY(1, "repository"),
        IMAGE(2, "image");

        private int val;
        private String str;

        ResourceType(int v, String s) {
            val = v;
            str = s;
        }

        public int value() {
            return val;
        }

        public static ResourceType valueOf(int v) {
            if (v < 0 || v > 2) {
                return null;
            }
            ResourceType[] mapper = {NAMESPACE, REPOSITORY, IMAGE};
            return mapper[v];
        }

        // case insensitive
        public static ResourceType resolve(String s) {
            switch (s.toLowerCase()) {
                case "namespace":
                    return NAMESPACE;
                case "repository":
                    return REPOSITORY;
                case "image":
                    return IMAGE;
                default:
                    return null;
            }
        }

        @Override
        public String toString() {
            return str;
        }
    }

    private ResourceType resourceType;
    private String identifier;

    public MgrSecondAuthenticationToken(MgrAccessDetails accessDetails,
                                        ResourceType resourceType,
                                        String identifier) {
        super(null);
        this.accessDetails = accessDetails;
        this.resourceType = resourceType;
        this.identifier = identifier;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return accessDetails.getUsername();
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
