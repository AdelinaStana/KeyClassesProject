package ranking.propertyValueCalculators.hits;

public class HitsValue {

    private Double authority;
    private Double hub;

    public HitsValue(Double authority, Double hub) {
        this.authority = authority;
        this.hub = hub;
    }

    public HitsValue() {
        authority = 1.0;
        hub = 1.0;
    }

    public Double getAuthority() {
        return authority;
    }

    public void setAuthority(Double authority) {
        this.authority = authority;
    }

    public Double getHub() {
        return hub;
    }

    public void setHub(Double hub) {
        this.hub = hub;
    }
}
