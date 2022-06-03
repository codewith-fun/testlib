package investwell.client.flavourtypetwo.model;

public class ServicesTypeTwo {
    private String serviceName;
    private int ivServices;

    public ServicesTypeTwo(int ivServices, String serviceName) {
        this.serviceName = serviceName;
        this.ivServices = ivServices;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getIvServices() {
        return ivServices;
    }

    public void setIvServices(int ivServices) {
        this.ivServices = ivServices;
    }
}
