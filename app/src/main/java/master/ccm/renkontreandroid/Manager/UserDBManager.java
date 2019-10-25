package master.ccm.renkontreandroid.Manager;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import master.ccm.renkontreandroid.Connexion_activity;
import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.User;
import master.ccm.renkontreandroid.Inscription_activity;

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
                    CurrentUser.getInstance().setMail(result.get("mail").toString());
                    CurrentUser.getInstance().setName(result.get("name").toString());
                    CurrentUser.getInstance().setLastName(result.get("lastName").toString());
                    CurrentUser.getInstance().setPhone(result.get("phone").toString());
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
}
