package ui.model;

public enum SortType {

    NAME_ASC("az"),
    NAME_DESC("za"),
    PRICE_ASC("lohi"),
    PRICE_DESC("hilo");

    private final String value;

    SortType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
