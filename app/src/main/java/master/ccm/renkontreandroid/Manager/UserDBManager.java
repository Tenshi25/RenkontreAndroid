package master.ccm.renkontreandroid.Manager;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import master.ccm.renkontreandroid.Connexion_activity;
import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.User;
import master.ccm.renkontreandroid.Form_add_friends_enemy_activity;
import master.ccm.renkontreandroid.Inscription_activity;
import master.ccm.renkontreandroid.ListFriendsEnemy_activity;
import master.ccm.renkontreandroid.Profile_activity;

public class UserDBManager {
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private static Boolean userExist ;

    public void AddUser(final User newUser, final Inscription_activity context) {
        if(!userExist){
            String lastName = null;
            String name = null;
            String mail = null;
            String phone = null;

            if(newUser.getLastName()!=null){
                lastName=newUser.getLastName();
            }
            if(newUser.getName()!=null){
                name=newUser.getName();
            }
            if(newUser.getMail()!=null){
                mail=newUser.getMail();
            }
            if(newUser.getPhone()!=null){
                mail=newUser.getPhone();
            }
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("lastName", lastName );
            userMap.put("name", name);
            userMap.put("mail", mail);
            userMap.put("phone", phone);


            database.collection("User").add(userMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()){
                        Log.i("AddUser","L'utilisateur à été ajouter");
                        context.RegisterSucess(task.getResult().getId(),newUser.getName());
                    }
                }
            });
        }else{
            context.RegistertFail();
        }





    }
    public void VerifUserExistBeforeInsert(final User newUser, final Inscription_activity context) {
        userExist=false;
        database.collection("User").whereEqualTo("mail",newUser.getMail()).get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() >= 1) {
                    Log.i("task.getResult()",""+task.getResult().size());
                    Log.i("selectUtilisateur","Le nom existe déjà");
                    UserDBManager.setUserExist(true);
                    AddUser(newUser,context);

                }else{
                    UserDBManager.setUserExist(false);
                    AddUser(newUser,context);
                }

            }
        });



    }

    public void ConnectUser (final User user, final Connexion_activity context)
    {

        database.collection("User").whereEqualTo("mail",user.getMail()).get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() == 1) {
                    Log.d("affichage", ""+task.getResult().size());
                    DocumentSnapshot result = task.getResult().getDocuments().get(0);
                    Log.d("succes affichage", result.getId() + " => " + result.get("mail"));
                    CurrentUser.getInstance().setId(result.getId());
                    if(result.get("mail")!=null){
                        CurrentUser.getInstance().setMail(result.get("mail").toString());
                    }
                    if(result.get("name")!=null){
                        CurrentUser.getInstance().setName(result.get("name").toString());
                    }
                    if(result.get("lastName")!=null){
                        CurrentUser.getInstance().setLastName(result.get("lastName").toString());
                    }
                    if(result.get("phone")!=null){
                        CurrentUser.getInstance().setPhone(result.get("phone").toString());
                    }
                    selectAllFriendsEnemyID();
                    context.ConnectSucess(result.getId(),result.get("mail").toString());
                } else {
                    context.ConnectionFailed();
                    Log.w("erreur affichage", "Error getting documents.", task.getException());
                }
            }
        });
    }
    private static void setUserExist(boolean p_userExist) {
        UserDBManager.userExist = p_userExist;

    }
    public void updateUser(User newUser,final Profile_activity context){
            DocumentReference userRef = database.collection("User").document(CurrentUser.getInstance().getId());
            Log.i("updateUser",""+CurrentUser.getInstance().getId()+" "+ newUser.getName()+" / "+newUser.getLastName()+" / "+newUser.getPhone()+" / ");
            Map<String,Object> updates = new HashMap<>();

            updates.put("lastName", newUser.getLastName());
            updates.put("name", newUser.getName());
            updates.put("mail", CurrentUser.getInstance().getMail());
            updates.put("phone", newUser.getPhone());

            userRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                context.updateUserSuccess();
                            }else{
                                context.updateUserFailed();
                            }
                        }

                });
            }
    public void selectAllFriendsEnemy( final ListFriendsEnemy_activity context){



        // recuperation de tous les ennemis
        /*CurrentUser.getInstance().getEnemylist().clear();
        CurrentUser.getInstance().getFriendslist().clear();
        /*
        ArrayList<String> allIdUser = new ArrayList<>();
        allIdUser.addAll(CurrentUser.getInstance().getFriendsIdlist()) ;
        allIdUser.addAll(CurrentUser.getInstance().getEnemyIdlist()) ;
        */
        /*
        for (String idUser : allIdUser)
        {
            DocumentReference docRef = database.collection("User").document(idUser);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Log.d("User", "DocumentSnapshot data: " + document.getData());
                            User newUser = new User();
                            newUser.setId(task.getResult().getId());
                            if (task.getResult().get("name") != null) {

                                newUser.setName(task.getResult().get("name").toString());
                            }
                            if (task.getResult().get("lastName") != null) {

                                newUser.setLastName(task.getResult().get("lastName").toString());
                            }
                            if (task.getResult().get("mail") != null) {

                                newUser.setMail(task.getResult().get("mail").toString());
                            }
                            if (task.getResult().get("phone") != null) {

                                newUser.setPhone(task.getResult().get("phone").toString());
                            }
                            //context.RemplirListView();
                            Log.d("FriendUser","enemylist : "+CurrentUser.getInstance().getEnemylist().size() );

                        } else {
                            Log.d("User", "No such document");

                        }
                    } else {
                        Log.d("User", "get failed with ", task.getException());
                    }
                }
            });

        }
        Log.d("FriendUser","enemylist : "+CurrentUser.getInstance().getEnemylist().size() );
        context.SelectFriendsEnemyFinished();*/

        CurrentUser.getInstance().getEnemylist().clear();
        CurrentUser.getInstance().getFriendslist().clear();
/*
        final ArrayList<String> allIdUser = new ArrayList<>();
        allIdUser.addAll(CurrentUser.getInstance().getFriendsIdlist()) ;
        allIdUser.addAll(CurrentUser.getInstance().getEnemyIdlist()) ;*/

        database.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.i("onCompleteSelectAllItemInventaireFini","2 eme select");
                if (task.isSuccessful()) {
                    ArrayList<User> listUsersFriend = new ArrayList<>();
                    ArrayList<User> listUsersEnemy = new ArrayList<>();
                    List<DocumentSnapshot> result = task.getResult().getDocuments();
                    for (DocumentSnapshot document : result) {
                        for (String unIdUser : CurrentUser.getInstance().getFriendsIdlist()){
                                Log.i("unUser","unUser : "+unIdUser);
                                Log.i("unUser","documentgetIdUser : "+document.getId());
                                String iddoc =document.getId();
                                if(unIdUser.equals(iddoc)){
                                    Log.i("unUser","dans le if identique : ");
                                    User newUser = new User();
                                    newUser.setId(document.getId());
                                    if (document.get("name") != null) {

                                        newUser.setName(document.get("name").toString());
                                    }
                                    if (document.get("lastName") != null) {

                                        newUser.setLastName(document.get("lastName").toString());
                                    }
                                    if (document.get("mail") != null) {

                                        newUser.setMail(document.get("mail").toString());
                                    }
                                    if (document.get("phone") != null) {

                                        newUser.setPhone(document.get("phone").toString());
                                    }

                                    newUser.setFriendEnemy("Friend");
                                    listUsersFriend.add(newUser);
                                    Log.i("log", "User Mail:" + newUser.getMail());
                                    Log.d("log", document.getId() + " => " + document.getData());
                                    break;
                                }

                        }
                        for (String unIdUser : CurrentUser.getInstance().getEnemyIdlist()){
                            Log.i("unUser","unUser : "+unIdUser);
                            Log.i("unUser","documentgetIdUser : "+document.getId());
                            String iddoc =document.getId();
                            if(unIdUser.equals(iddoc)){
                                Log.i("unUser","dans le if identique : ");
                                User newUser = new User();
                                newUser.setId(document.getId());
                                if (document.get("name") != null) {

                                    newUser.setName(document.get("name").toString());
                                }
                                if (document.get("lastName") != null) {

                                    newUser.setLastName(document.get("lastName").toString());
                                }
                                if (document.get("mail") != null) {

                                    newUser.setMail(document.get("mail").toString());
                                }
                                if (document.get("phone") != null) {

                                    newUser.setPhone(document.get("phone").toString());
                                }

                                newUser.setFriendEnemy("Enemy");
                                listUsersEnemy.add(newUser);
                                Log.i("log", "User Mail:" + newUser.getMail());
                                Log.d("log", document.getId() + " => " + document.getData());
                                break;
                            }

                        }
                    }
                    CurrentUser.getInstance().getFriendslist().addAll(listUsersFriend);
                    CurrentUser.getInstance().getEnemylist().addAll(listUsersEnemy);

                    ArrayList<User> listUsers = new ArrayList<>();
                    listUsers.addAll(listUsersFriend);
                    listUsers.addAll(listUsersEnemy);
                    context.RemplirListView(listUsers);

                } else {
                    Log.w("selectAll", "Error getting documents.", task.getException());
                }
            }
        });


    }
    /*
    public void selectAllFriendsBeforeselectAllEnemy( final ListFriendsEnemy_activity context) {

        // recuperation de tous les amis
        CurrentUser.getInstance().getFriendslist().clear();
        Log.d("User", "listuser: " +CurrentUser.getInstance().getFriendsIdlist());
        for (String idUser : CurrentUser.getInstance().getFriendsIdlist())
        {
            Log.d("User", "iduser: " + idUser);
            DocumentReference docRef = database.collection("User").document(idUser);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Log.d("User", "DocumentSnapshot data: " + document.getData());
                            User newFriendUser = new User();
                            newFriendUser.setId(task.getResult().getId());
                            if(task.getResult().get("name")!=null){
                                newFriendUser.setName(task.getResult().get("name").toString());
                            }
                            if(task.getResult().get("lastName")!=null){
                                newFriendUser.setLastName(task.getResult().get("lastName").toString());
                            }
                            if(task.getResult().get("mail")!=null){
                                newFriendUser.setMail(task.getResult().get("mail").toString());
                            }
                            if(task.getResult().get("phone")!=null){
                                newFriendUser.setPhone(task.getResult().get("phone").toString());
                            }
                            CurrentUser.getInstance().getFriendslist().add(newFriendUser);
                            Log.d("FriendUser","friendlist : "+CurrentUser.getInstance().getFriendslist().size() );
                            //context.RemplirListView();

                        } else {
                            Log.d("User", "No such document");

                        }
                    } else {
                        Log.d("User", "get failed with ", task.getException());
                    }
                }
            });
        }
        Log.d("FriendUser","friendlist : "+CurrentUser.getInstance().getFriendslist().size() );
        selectAllEnemy(context);

    }*/
    public void BeforeAddUserLink(final String mailUserFriendEnemy, final String friendOrEnemy, final Form_add_friends_enemy_activity context) {
        database.collection("User").whereEqualTo("mail",mailUserFriendEnemy).get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() >= 1) {
                    Log.i("task.getResult()",""+task.getResult().size());
                    Log.i("selectUtilisateur","L'utilisateurExite");
                    verifUserLinkExists(task.getResult().getDocuments().get(0).getId(),friendOrEnemy,context);


                }else{
                    context.userNotExist();

                }

            }
        });
    }
    public void verifUserLinkExists(final String idUserFriendEnemy, final String friendOrEnemy, final Form_add_friends_enemy_activity context) {
        database.collection("LinkUser").whereEqualTo("idUserA",CurrentUser.getInstance().getId()).whereEqualTo("idUserB",idUserFriendEnemy).get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() >= 1) {
                    Log.i("task.getResult()",""+task.getResult().size());
                    Log.i("LinkUser","Le lien existe déja");

                }else{
                    AddUserLink(idUserFriendEnemy,friendOrEnemy,context);

                }

            }
        });
    }

    public void AddUserLink(String idUserFriendEnemy, String friendOrEnemy, final Form_add_friends_enemy_activity context) {



            Map<String, Object> linkMap = new HashMap<>();
            linkMap.put("idUserA", CurrentUser.getInstance().getId() );
            linkMap.put("idUserB", idUserFriendEnemy);
            linkMap.put("friendOrEnemy", friendOrEnemy);


            database.collection("LinkUser").add(linkMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()){
                        Log.i("AddUser","L'utilisateur à été ajouter");
                        selectAllFriendsEnemyID();
                        context.AddLinkSucess();
                    }
                }
            });
    }
    public void selectLinkBeforeDelete(final String idUserFriendEnemy, final ListFriendsEnemy_activity context) {
        database.collection("LinkUser").whereEqualTo("idUserA",CurrentUser.getInstance().getId()).whereEqualTo("idUserB",idUserFriendEnemy).get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() >= 1) {
                    Log.i("task.getResult()",""+task.getResult().size());
                    Log.i("LinkUser","Le lien existe déja");
                    for (int i=0 ; i<task.getResult().size();i++) {
                        deleteLink(task.getResult().getDocuments().get(i).getId());
                    }

                }else{


                }

            }
        });
    }
    public void selectAllFriendsEnemyID() {

        // recuperation de tous les id des amis et enemie

        database.collection("LinkUser")
                .whereEqualTo("idUserA", CurrentUser.getInstance().getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("linkUser : ", document.getId() + " => " + document.getData());
                                if(document.getData().get("friendOrEnemy").toString().equals("Friend")){
                                    CurrentUser.getInstance().getFriendsIdlist().add(document.getData().get("idUserB").toString());
                                }
                                if(document.getData().get("friendOrEnemy").toString().equals("Enemy")){
                                    CurrentUser.getInstance().getEnemyIdlist().add(document.getData().get("idUserB").toString());
                                }
                            }
                        } else {
                            Log.d("linkUser :", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    private void deleteLink (String idLink){
        database.collection("LinkUser").document(idLink)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("delete", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("delete", "Error deleting document", e);
                    }
                });

    }

}
