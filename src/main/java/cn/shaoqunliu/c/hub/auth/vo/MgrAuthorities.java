package cn.shaoqunliu.c.hub.auth.vo;

import cn.shaoqunliu.c.hub.auth.vo.authority.Ownership;

import java.util.List;

public class MgrAuthorities {

    private Ownership ownership;
    private List<String> readOnly;
    private List<String> writable;

    public Ownership getOwnership() {
        return ownership;
    }

    public void setOwnership(Ownership ownership) {
        this.ownership = ownership;
    }

    public List<String> getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(List<String> readOnly) {
        this.readOnly = readOnly;
    }

    public List<String> getWritable() {
        return writable;
    }

    public void setWritable(List<String> writable) {
        this.writable = writable;
    }
}
