package master.ccm.renkontreandroid.Manager;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import master.ccm.renkontreandroid.Connexion_activity;
import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.User;
import master.ccm.renkontreandroid.Form_add_friends_enemy_activity;
import master.ccm.renkontreandroid.Inscription_activity;
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
    {{

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
                    if(result.get("friendsList")!=null){
                        Toast.makeText(context,result.get("friendsList").toString(),Toast.LENGTH_SHORT).show();
                        //CurrentUser.getInstance().setFriendslist(result.get("friendsList"));
                    }
                    if(result.get("enemyList")!=null){
                        //CurrentUser.getInstance().setEnemylist((ArrayList<User>) result.get("enemyList").);
                    }
                    context.ConnectSucess(result.getId(),result.get("mail").toString());
                } else {
                    context.ConnectionFailed();
                    Log.w("erreur affichage", "Error getting documents.", task.getException());
                }
            }
        });
    }}
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

    public void updateUserFriendsEnemy( final Form_add_friends_enemy_activity context){
        CurrentUser currentUser =CurrentUser.getInstance();

        DocumentReference userRef = database.collection("User").document(CurrentUser.getInstance().getId());
        Log.i("updateUser",""+CurrentUser.getInstance().getId()+" "+ currentUser.getName()+" / "+currentUser.getLastName()+" / "+currentUser.getPhone()+" / ");

        userRef.set(CurrentUser.getInstance()).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    context.updateUserFriendsEnemySuccess();
                }else{
                    context.updateUserFriendsEnemyFailed();
                }
            }

        });
    }
    public void selectBeforeUpdateUserFriendsEnemy(final User user, final String friendOrEnemy, final Form_add_friends_enemy_activity context) {
        database.collection("User").whereEqualTo("mail",user.getMail()).get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() >= 1) {
                    Log.i("task.getResult()",""+task.getResult().size());
                    Log.i("selectUtilisateur","L'utilisateurExite");

                    context.userExist(task.getResult().getDocuments().get(0).getId());
                    User newFriendOrEnemy =new User();
                    newFriendOrEnemy.setId(task.getResult().getDocuments().get(0).getId());
                    if (friendOrEnemy.equals("Enemy")){
                        CurrentUser.getInstance().getEnemylist().add(newFriendOrEnemy);
                    }else{
                        CurrentUser.getInstance().getFriendslist().add(newFriendOrEnemy);
                    }
                    updateUserFriendsEnemy(context);

                }else{
                    context.userNotExist();

                }

            }
        });



    }

}
