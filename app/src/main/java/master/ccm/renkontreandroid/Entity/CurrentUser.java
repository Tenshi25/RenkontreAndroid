package master.ccm.renkontreandroid.Entity;

import java.util.ArrayList;

public class CurrentUser {

    private String id = "0";
    private String lastName = null;
    private String name = null;
    private String mail = null;
    private String phone = null;
    private GeoLocationPosition geoLocationPosition = null;

    private static CurrentUser sui = null;

    public static synchronized CurrentUser getInstance(){
        if(null == sui){
            sui = new CurrentUser();
        }
        return sui;
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
        //Log.i("setId", id);
    }
    public CurrentUser() {

    }
    public CurrentUser(String id, String lastName, String name) {
        this.id = id;
        this.lastName = lastName;
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public GeoLocationPosition getGeoLocationPosition() {
        return geoLocationPosition;
    }

    public void setGeoLocationPosition(GeoLocationPosition geoLocationPosition) {
        this.geoLocationPosition = geoLocationPosition;
    }
}
