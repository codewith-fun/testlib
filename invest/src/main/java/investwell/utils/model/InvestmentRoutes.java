package investwell.utils.model;

public class InvestmentRoutes {
    private int routeIcon;
    private String routeName;
    private String routeMiniHeader;
    private String routeDesc;



    public InvestmentRoutes(int routeIcon, String routeName, String routeMiniHeader, String routeDesc) {
        this.routeIcon = routeIcon;
        this.routeName = routeName;
        this.routeMiniHeader = routeMiniHeader;
        this.routeDesc = routeDesc;
    }

    public int  getRouteIcon() {
        return routeIcon;
    }

    public void setRouteIcon(int routeIcon) {
        this.routeIcon = routeIcon;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteMiniHeader() {
        return routeMiniHeader;
    }

    public void setRouteMiniHeader(String routeMiniHeader) {
        this.routeMiniHeader = routeMiniHeader;
    }

    public String getRouteDesc() {
        return routeDesc;
    }

    public void setRouteDesc(String routeDesc) {
        this.routeDesc = routeDesc;
    }
}
