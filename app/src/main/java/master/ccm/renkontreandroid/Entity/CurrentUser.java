package master.ccm.renkontreandroid.Entity;

import java.util.ArrayList;

public class CurrentUser {

    private String id = "0";
    private String lastName = null;
    private String name = null;
    private String mail = null;
    private String phone = null;

    private ArrayList<User> friendslist = new ArrayList<User>();
    private ArrayList<User> enemylist = new ArrayList<User>();




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

    public CurrentUser(String id, String lastName, String name, String mail, String phone, ArrayList<User> friendslist, ArrayList<User> enemylist) {
        this.id = id;
        this.lastName = lastName;
        this.name = name;
        this.mail = mail;
        this.phone = phone;
        this.friendslist = friendslist;
        this.enemylist = enemylist;
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

    public ArrayList<User> getFriendslist() {
        return friendslist;
    }

    public void setFriendslist(ArrayList<User> friendslist) {
        this.friendslist = friendslist;
    }

    public ArrayList<User> getEnemylist() {
        return enemylist;
    }

    public void setEnemylist(ArrayList<User> enemylist) {
        this.enemylist = enemylist;
    }
}
