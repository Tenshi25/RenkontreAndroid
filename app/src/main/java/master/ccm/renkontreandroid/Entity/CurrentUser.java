package master.ccm.entity;

import java.util.ArrayList;

public class CurrentUser {

    private String id = "0";
    private String Name = "NULL";
    private Deck DeckPrincipale;
    private ArrayList<Deck> DeckList=new ArrayList<>();


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

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
        //Log.i("setName", Name);
    }
}
