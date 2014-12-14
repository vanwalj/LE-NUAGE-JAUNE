package com.napalm.nuage;

/**
 * Created by Valentin on 14/12/14.
 */
public class SourceUsage {

    private String name;
    private Integer value;

    public SourceUsage(String name, Integer value)
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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
