package investwell.client.flavourtypetwo.model;

public class InvestRouteTypeTwo {
    private String investRouteName, investRouteDesc;

    public String getInvestRouteName() {
        return investRouteName;
    }

    public void setInvestRouteName(String investRouteName) {
        this.investRouteName = investRouteName;
    }

    public String getInvestRouteDesc() {
        return investRouteDesc;
    }

    public void setInvestRouteDesc(String investRouteDesc) {
        this.investRouteDesc = investRouteDesc;
    }

    public int getIvRouteIcons() {
        return ivRouteIcons;
    }

    public void setIvRouteIcons(int ivRouteIcons) {
        this.ivRouteIcons = ivRouteIcons;
    }

    public InvestRouteTypeTwo(String investRouteName, String investRouteDesc, int ivRouteIcons) {
        this.investRouteName = investRouteName;
        this.investRouteDesc = investRouteDesc;
        this.ivRouteIcons = ivRouteIcons;
    }

    private int ivRouteIcons;
}
