package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;
import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.User;
import master.ccm.renkontreandroid.Manager.UserDBManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe de l'activité qui permet de voir ses amis et ennemis
 */
public class ListFriendsEnemy_activity extends AppCompatActivity {

    private ListView listView;
    private List<User> userList;
    private List<String> tableauChaines = new ArrayList<String>();
    private User selectedUser;
    private User[] tabUser;


    /**
     * Instancie les variable de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_friends_enemy);

        //on récupére le recyclerView
        listView = (ListView) findViewById(R.id.id_listView_friend_enemy);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedUser = userList.get(position);
                listView.getChildAt(position).setBackgroundColor(Color.RED);
                OnUserClick(selectedUser);

            }

        });

        UserDBManager userDBManager =new UserDBManager();
        userDBManager.selectAllFriendsEnemy(this);
    }

    /**
     * Rempli la liste avec les amis et ennemis possédés
     */
    public void RemplirListView(ArrayList<User> p_userList) {

        Log.i("logNomTailleListeUser", "taille : " + p_userList.size());
        Log.i("logNomTailleListeUser", "tailleE : " +CurrentUser.getInstance().getEnemylist().size());
        Log.i("logNomTailleListeUser", "tailleF : " +CurrentUser.getInstance().getFriendslist().size());
        userList = p_userList;
        int cpt = 0;
        tabUser = new User[p_userList.size()];
        Log.i("logNomTailleTabUser", "taille : " + tabUser.length);

        for (User oneUser : userList) {
            tabUser[cpt] = oneUser;
            Log.i("logNomFor", cpt + ". mail : " + oneUser.getMail() + " name : " + oneUser.getName());
            cpt++;
        }

        ArrayAdapter<User> monArrayAdapter = new ArrayAdapter<User>(this, R.layout.friends_enemy_item_list, tabUser){
            private int vraiPosition=0;
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.i("logNomPos", "pos : "+ position);

                User aUser = tabUser[position];

                if(convertView == null){
                    convertView = getLayoutInflater()
                            .inflate(R.layout.friends_enemy_item_list, parent, false);
                }
                TextView mailUser = (TextView) convertView.findViewById(R.id.tv_mail_list_friend_enemy);
                mailUser.setText(aUser.getMail());
                ImageView imageFriendEnemy = (ImageView) convertView.findViewById(R.id.iv_friend_enemy);
                if (aUser.getFriendEnemy().equals("Enemy")){
                    imageFriendEnemy.setImageResource(R.drawable.ennemi);
                }
                if (aUser.getFriendEnemy().equals("Friend")){
                    imageFriendEnemy.setImageResource(R.drawable.amis);
                }
                Log.i("logNomUser","logNameUser : "+aUser.getName()+" mail : "+aUser.getMail());
                //
                vraiPosition++;
                return convertView;
            }
        };
        listView.setAdapter(monArrayAdapter);

    }

    /**
     * lancer l'activité qui permet l'ajout d'amis et d'ennemis
     */
    public void onClickAddFriendsEnemy(View view) {
        Intent intent = new Intent(this, Form_add_friends_enemy_activity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Retour à l'activité précédente
     */
    public void onClickReturn(View view) {
        Intent intent = new Intent(this, Accueil_activity.class);
        startActivity(intent);
        finish();
    }

    public void OnUserClick(User p_user) {
        Toast.makeText(this,"user : " + p_user.getMail(),Toast.LENGTH_SHORT).show();
    }

    public void SelectFriendsEnemyFinished() {
        ArrayList<User> userList = new ArrayList<User>();
        Log.i("List", "enemyList : "+ CurrentUser.getInstance().getEnemylist());

        Log.i("List", "friendList : "+ CurrentUser.getInstance().getFriendslist());
        if (CurrentUser.getInstance().getFriendslist().size() !=0)
        {

            userList.addAll(CurrentUser.getInstance().getFriendslist());
        }

        if (CurrentUser.getInstance().getEnemylist().size() !=0)
        {
            userList.addAll(CurrentUser.getInstance().getEnemylist());
        }
        if (CurrentUser.getInstance().getFriendslist().size() !=0 || CurrentUser.getInstance().getEnemylist().size() !=0)
        {
            RemplirListView(userList);

        }

    }

    /**
     * Supprimer lien avec l'utilisateur ami/ennemi
     */
    public void onClickDeleteLink(View view) {
        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        User userASupprimer = userList.get(position);
        UserDBManager userDBManager =  new UserDBManager();
        userDBManager.selectLinkBeforeDelete(userASupprimer.getId(),this);
        userList.remove(userASupprimer);
        if (CurrentUser.getInstance().getFriendsIdlist().contains(userASupprimer.getId())){
            CurrentUser.getInstance().getFriendsIdlist().remove(userASupprimer.getId());
        }else{
            if(CurrentUser.getInstance().getEnemyIdlist().contains(userASupprimer.getId())){
                CurrentUser.getInstance().getEnemyIdlist().remove(userASupprimer.getId());
            }
        }

        userDBManager.selectAllFriendsEnemy(this);
    }

    public void addFriend(User newEnemyUser) {
        CurrentUser.getInstance().getEnemylist().add(newEnemyUser);
    }
    public void addEnemy(User newEnemyUser) {
        CurrentUser.getInstance().getFriendslist().add(newEnemyUser);
    }

}
