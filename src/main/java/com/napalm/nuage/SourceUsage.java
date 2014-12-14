package com.napalm.nuage;

/**
 * Created by Valentin on 14/12/14.
 */
public class SourceUsage {

    private String name;
    private Long value;

    public SourceUsage(String name, Long value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
