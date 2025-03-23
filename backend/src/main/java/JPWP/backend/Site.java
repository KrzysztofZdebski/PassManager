package JPWP.backend;

public class Site {
    private String nameSite;
    public  Site(String nameSite){
        this.nameSite = nameSite;
    }
    public Site(){

    }
    public String getNameSite(){
        return nameSite;
    }
    public void setNameSite(String nameSite){
        this.nameSite = nameSite;
    }
}
