package HelperClasses;

public class ServiceType {

    private int serviceImage;
    private String serviceTitle;

    public ServiceType(int serviceImage, String serviceTitle) {
        this.serviceImage = serviceImage;
        this.serviceTitle = serviceTitle;
    }

    public int getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(int serviceImage) {
        this.serviceImage = serviceImage;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }
}
