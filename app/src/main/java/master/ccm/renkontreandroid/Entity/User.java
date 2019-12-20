package master.ccm.renkontreandroid.Entity;

public class User {
    private String id;
    private String lastName;
    private String name;
    private String mail;
    private String phone;
    private String FriendEnemy;
    private GeoLocationPosition geoLocationPosition;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String lastName, String name, String mail, String phone) {
        this.id = id;
        this.lastName = lastName;
        this.name = name;
        this.mail = mail;
        this.phone = phone;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFriendEnemy() {
        return FriendEnemy;
    }

    public void setFriendEnemy(String friendEnemy) {
        FriendEnemy = friendEnemy;
    }

    public GeoLocationPosition getGeoLocationPosition() {
        return geoLocationPosition;
    }

    public void setGeoLocationPosition(GeoLocationPosition geoLocationPosition) {
        this.geoLocationPosition = geoLocationPosition;
    }
}
