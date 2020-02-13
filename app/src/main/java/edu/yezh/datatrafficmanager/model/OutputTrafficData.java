package edu.yezh.datatrafficmanager.model;

public class OutputTrafficData {
    private String value;
    private String type;

    public OutputTrafficData() {
        super();
    }

    public OutputTrafficData(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "OutputTrafficData{" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
