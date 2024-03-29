package master.ccm.renkontreandroid.Manager;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;


import java.util.ArrayList;

import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import master.ccm.renkontreandroid.Connexion_activity;
import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.GeoLocationPosition;
import master.ccm.renkontreandroid.Entity.User;
import master.ccm.renkontreandroid.Form_add_friends_enemy_activity;
import master.ccm.renkontreandroid.Inscription_activity;
import master.ccm.renkontreandroid.ListFriendsEnemy_activity;
import master.ccm.renkontreandroid.Profile_activity;
import master.ccm.renkontreandroid.services.NotificationPhoneService;
import master.ccm.renkontreandroid.services.RefreshMapUiService;

public class UserDBManager {
    private static FirebaseFirestore database = FirebaseFirestore.getInstance();
    private static Boolean userExist;
    private static List<ListenerRegistration> documentSnapshotListenerList;

    /**
     *
     * @param newUser,context
     * cette fonction permet d'ajouter un Utilisateur dans la base de données firebase
     * pour eviter les erreurs on set les variable à null et on ajoute ensuite
     */
    public void AddUser(final User newUser, final Inscription_activity context) {
        if (!userExist) {
            String lastName = null;
            String name = null;
            String mail = null;
            String phone = null;

            if (newUser.getLastName() != null) {
                lastName = newUser.getLastName();
            }
            if (newUser.getName() != null) {
                name = newUser.getName();
            }
            if (newUser.getMail() != null) {
                mail = newUser.getMail();
            }
            if (newUser.getPhone() != null) {
                mail = newUser.getPhone();
            }
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("lastName", lastName);
            userMap.put("name", name);
            userMap.put("mail", mail);
            userMap.put("phone", phone);


            database.collection("User").add(userMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Log.i("AddUser", "L'utilisateur à été ajouter");
                        context.RegisterSucess(task.getResult().getId(), newUser.getName());
                    }
                }
            });
        } else {
            context.RegistertFail();
        }


    }
    /**
     *
     * @param newUser,context
     * fonction qui permet de verifier si le login n'est pas déjà pris par un autre utilisateur
     */
    public void VerifUserExistBeforeInsert(final User newUser, final Inscription_activity context) {
        userExist = false;
        database.collection("User").whereEqualTo("mail", newUser.getMail()).get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() >= 1) {
                    Log.i("task.getResult()", "" + task.getResult().size());
                    Log.i("selectUtilisateur", "Le login existe déjà");
                    UserDBManager.setUserExist(true);
                    AddUser(newUser, context);

                } else {
                    UserDBManager.setUserExist(false);
                    AddUser(newUser, context);
                }

            }
        });


    }
    /**
     *
     * @param user,context
     * fonction qui permet de récupéré les données de l'utilisateur lorsqu'il se connecte
     */
    public void ConnectUser(final User user, final Connexion_activity context) {

        database.collection("User").whereEqualTo("mail", user.getMail()).get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() == 1) {
                    Log.d("affichage", "" + task.getResult().size());
                    DocumentSnapshot result = task.getResult().getDocuments().get(0);
                    Log.d("succes affichage", result.getId() + " => " + result.get("mail"));
                    CurrentUser.getInstance().setId(result.getId());
                    if (result.get("mail") != null) {
                        CurrentUser.getInstance().setMail(result.get("mail").toString());
                    }
                    if (result.get("name") != null) {
                        CurrentUser.getInstance().setName(result.get("name").toString());
                    }
                    if (result.get("lastName") != null) {
                        CurrentUser.getInstance().setLastName(result.get("lastName").toString());
                    }
                    if (result.get("phone") != null) {
                        CurrentUser.getInstance().setPhone(result.get("phone").toString());
                    }

                    selectAllFriendsEnemyID();

                    if (result.get("geolocation.longitude") != null && result.get("geolocation.latitude") != null) {
                        GeoLocationPosition geoLocationPosition = new GeoLocationPosition();
                        geoLocationPosition.setLatitude(Double.valueOf(String.valueOf(result.get("geolocation.latitude"))));
                        geoLocationPosition.setLongitude(Double.valueOf(String.valueOf(result.get("geolocation.longitude"))));
                        if (result.get("geolocation.date") != null) {
                            geoLocationPosition.setDateRegistration(new Date(String.valueOf(result.get("geolocation.date"))));
                        }
                        CurrentUser.getInstance().setGeoLocationPosition(geoLocationPosition);
                    }

                    context.ConnectSucess(result.getId(), result.get("mail").toString());

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
    /**
     *
     * @param newUser,context
     * cette fonction permet de mettre à jour l'utilisateur en base de données
     */
    public void updateUser(User newUser, final Profile_activity context) {
        DocumentReference userRef = database.collection("User").document(CurrentUser.getInstance().getId());
        Log.i("updateUser", "" + CurrentUser.getInstance().getId() + " " + newUser.getName() + " / " + newUser.getLastName() + " / " + newUser.getPhone() + " / ");
        Map<String, Object> updates = new HashMap<>();

        updates.put("lastName", newUser.getLastName());
        updates.put("name", newUser.getName());
        updates.put("mail", CurrentUser.getInstance().getMail());
        updates.put("phone", newUser.getPhone());

        userRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    context.updateUserSuccess();
                } else {
                    context.updateUserFailed();
                }
            }

        });
    }

    /**
     *
     * @param context
     * fonction qui permet de récupéré les amis et ennemies de l'utilisateur connecté
     */
    public void selectAllFriendsEnemy(final ListFriendsEnemy_activity context) {

        CurrentUser.getInstance().getEnemylist().clear();
        CurrentUser.getInstance().getFriendslist().clear();


        database.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.i("onCompleteSelectAllItemInventaireFini", "2 eme select");
                if (task.isSuccessful()) {
                    ArrayList<User> listUsersFriend = new ArrayList<>();
                    ArrayList<User> listUsersEnemy = new ArrayList<>();
                    List<DocumentSnapshot> result = task.getResult().getDocuments();
                    for (DocumentSnapshot document : result) {
                        for (String unIdUser : CurrentUser.getInstance().getFriendsIdlist()) {
                            Log.i("unUser", "unUser : " + unIdUser);
                            Log.i("unUser", "documentgetIdUser : " + document.getId());
                            String iddoc = document.getId();
                            if (unIdUser.equals(iddoc)) {
                                Log.i("unUser", "dans le if identique : ");
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

                                if (document.get("geolocation.longitude") != null && document.get("geolocation.latitude") != null) {
                                    GeoLocationPosition geoLocationPosition = new GeoLocationPosition();
                                    geoLocationPosition.setLatitude(Double.valueOf(String.valueOf(document.get("geolocation.latitude"))));
                                    geoLocationPosition.setLongitude(Double.valueOf(String.valueOf(document.get("geolocation.longitude"))));
                                    if (document.get("geolocation.date") != null) {
                                        geoLocationPosition.setDateRegistration(new Date(String.valueOf(document.get("geolocation.date"))));
                                    }
                                    newUser.setGeoLocationPosition(geoLocationPosition);
                                }

                                newUser.setFriendEnemy("Friend");
                                listUsersFriend.add(newUser);
                                Log.i("log", "User Mail:" + newUser.getMail());
                                Log.d("log", document.getId() + " => " + document.getData());
                                break;
                            }

                        }
                        for (String unIdUser : CurrentUser.getInstance().getEnemyIdlist()) {
                            Log.i("unUser", "unUser : " + unIdUser);
                            Log.i("unUser", "documentgetIdUser : " + document.getId());
                            String iddoc = document.getId();
                            if (unIdUser.equals(iddoc)) {
                                Log.i("unUser", "dans le if identique : ");
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

                                if (document.get("geolocation.longitude") != null && document.get("geolocation.latitude") != null) {
                                    GeoLocationPosition geoLocationPosition = new GeoLocationPosition();
                                    geoLocationPosition.setLatitude(Double.valueOf(String.valueOf(document.get("geolocation.latitude"))));
                                    geoLocationPosition.setLongitude(Double.valueOf(String.valueOf(document.get("geolocation.longitude"))));
                                    if (document.get("geolocation.date") != null) {
                                        geoLocationPosition.setDateRegistration(new Date(String.valueOf(document.get("geolocation.date"))));
                                    }
                                    newUser.setGeoLocationPosition(geoLocationPosition);
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

                    if (context != null) {
                        ArrayList<User> listUsers = new ArrayList<>();
                        listUsers.addAll(listUsersFriend);
                        listUsers.addAll(listUsersEnemy);
                        context.RemplirListView(listUsers);
                    }

                } else {
                    Log.w("selectAll", "Error getting documents.", task.getException());
                }

                addAllListener();
            }
        });



    }
    /**
     *
     * @param mailUserFriendEnemy,friendOrEnemy,context
     * cette fonction permet de verifier si l'utilisateur existe et de récupéré son id
     */
    public void BeforeAddUserLink(final String mailUserFriendEnemy, final String friendOrEnemy, final Form_add_friends_enemy_activity context) {
        database.collection("User").whereEqualTo("mail", mailUserFriendEnemy).get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() >= 1) {
                    Log.i("task.getResult()", "" + task.getResult().size());
                    Log.i("selectUtilisateur", "L'utilisateurExite");
                    verifUserLinkExists(task.getResult().getDocuments().get(0).getId(), friendOrEnemy, context);


                } else {
                    context.userNotExist();

                }

            }
        });
    }
    /**
     *
     * @param idUserFriendEnemy,friendOrEnemy,context
     * cette fonction permet de verifier que le lien n'existe pas avant de le rajouter
     */
    public void verifUserLinkExists(final String idUserFriendEnemy, final String friendOrEnemy, final Form_add_friends_enemy_activity context) {
        database.collection("LinkUser").whereEqualTo("idUserA", CurrentUser.getInstance().getId()).whereEqualTo("idUserB", idUserFriendEnemy).get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() >= 1) {
                    Log.i("task.getResult()", "" + task.getResult().size());
                    Log.i("LinkUser", "Le lien existe déja");

                } else {
                    AddUserLink(idUserFriendEnemy, friendOrEnemy, context);

                }

            }
        });
    }
    /**
     *
     * @param idUserFriendEnemy,friendOrEnemy,context
     * cette fonction permet d'ajouter le lien entre les deux utilisateur
     */
    public void AddUserLink(String idUserFriendEnemy, String friendOrEnemy, final Form_add_friends_enemy_activity context) {


        Map<String, Object> linkMap = new HashMap<>();
        linkMap.put("idUserA", CurrentUser.getInstance().getId());
        linkMap.put("idUserB", idUserFriendEnemy);
        linkMap.put("friendOrEnemy", friendOrEnemy);


        database.collection("LinkUser").add(linkMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.i("AddUser", "L'utilisateur à été ajouter");
                    selectAllFriendsEnemyID();
                    context.AddLinkSucess();
                }
            }
        });
    }
    /**
     *
     * @param idUserFriendEnemy,context
     * cette fonction permet de verifier si le lien existe avant de le supprimer
     */
    public void selectLinkBeforeDelete(final String idUserFriendEnemy, final ListFriendsEnemy_activity context) {
        database.collection("LinkUser").whereEqualTo("idUserA", CurrentUser.getInstance().getId()).whereEqualTo("idUserB", idUserFriendEnemy).get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() >= 1) {
                    Log.i("task.getResult()", "" + task.getResult().size());
                    Log.i("LinkUser", "Le lien existe déja");
                    for (int i = 0; i < task.getResult().size(); i++) {
                        deleteLink(task.getResult().getDocuments().get(i).getId());
                    }

                } else {


                }

            }
        });
    }
    /**
     *
     * cette fonction permet de récuperer tous les ids de tous les ennemies et amis
     */
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
                                if (document.getData().get("friendOrEnemy").toString().equals("Friend")) {
                                    CurrentUser.getInstance().getFriendsIdlist().add(document.getData().get("idUserB").toString());
                                }
                                if (document.getData().get("friendOrEnemy").toString().equals("Enemy")) {
                                    CurrentUser.getInstance().getEnemyIdlist().add(document.getData().get("idUserB").toString());
                                }
                            }

                            UserDBManager.this.selectAllFriendsEnemy(null);
                        } else {
                            Log.d("linkUser :", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    /**
     *
     * @param idLink
     * cette fonction de supprimer un lien avec l'id d'un lien
     */
    private void deleteLink(String idLink) {
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

    public void updateUserToAddPositionService(Location location) {
        final CurrentUser currentUser = CurrentUser.getInstance();

        final GeoLocationPosition geoLocationPosition = new GeoLocationPosition();
        geoLocationPosition.setDateRegistration(new Date());
        geoLocationPosition.setLatitude(location.getLatitude());
        geoLocationPosition.setLongitude(location.getLongitude());

        DocumentReference userRef = database.collection("User").document(currentUser.getId());
        Map<String, Object> updates = new HashMap<>();

        updates.put("lastName", currentUser.getLastName());
        updates.put("name", currentUser.getName());
        updates.put("mail", currentUser.getMail());
        updates.put("phone", currentUser.getPhone());
        updates.put("geolocation.latitude", geoLocationPosition.getLatitude());
        updates.put("geolocation.longitude", geoLocationPosition.getLongitude());
        updates.put("geolocation.date", geoLocationPosition.getDateRegistration());

        userRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    currentUser.setGeoLocationPosition(geoLocationPosition);
                } else {
                    // nothing
                }
            }

        });
    }

    public void addAllListener() {
        if (documentSnapshotListenerList == null) {
            documentSnapshotListenerList = new ArrayList<>();
        } else {
            documentSnapshotListenerList.forEach(ListenerRegistration::remove);
            documentSnapshotListenerList.clear();
        }

        addFriendListeners();
        addEnemyListeners();
        addCurrentUserListener();
    }

    private void addFriendListeners() {
        CurrentUser.getInstance().getFriendslist().forEach(user -> {
            ListenerRegistration listenerRegistration = database.collection("User").document(user.getId())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d("ListenerProblem :", "Error getting document: " + user.getId());
                            } else {
                                if (documentSnapshot.get("geolocation.longitude") != null && documentSnapshot.get("geolocation.latitude") != null) {
                                    GeoLocationPosition geoLocationPosition = new GeoLocationPosition();
                                    geoLocationPosition.setLatitude(Double.valueOf(String.valueOf(documentSnapshot.get("geolocation.latitude"))));
                                    geoLocationPosition.setLongitude(Double.valueOf(String.valueOf(documentSnapshot.get("geolocation.longitude"))));
                                    if (documentSnapshot.get("geolocation.date") != null) {
                                        geoLocationPosition.setDateRegistration(new Date(String.valueOf(documentSnapshot.get("geolocation.date"))));
                                    }
                                    user.setGeoLocationPosition(geoLocationPosition);
                                }

                                RefreshMapUiService.refreshMap();
                                NotificationPhoneService.notifyUserInProximity(user, true);
                            }
                        }
                    });
            documentSnapshotListenerList.add(listenerRegistration);
        });
    }

    private void addEnemyListeners() {
        CurrentUser.getInstance().getEnemylist().forEach(user -> {
            ListenerRegistration listenerRegistration = database.collection("User").document(user.getId())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d("ListenerProblem :", "Error getting document: " + user.getId());
                            } else {
                                if (documentSnapshot.get("geolocation.longitude") != null && documentSnapshot.get("geolocation.latitude") != null) {
                                    GeoLocationPosition geoLocationPosition = new GeoLocationPosition();
                                    geoLocationPosition.setLatitude(Double.valueOf(String.valueOf(documentSnapshot.get("geolocation.latitude"))));
                                    geoLocationPosition.setLongitude(Double.valueOf(String.valueOf(documentSnapshot.get("geolocation.longitude"))));
                                    if (documentSnapshot.get("geolocation.date") != null) {
                                        geoLocationPosition.setDateRegistration(new Date(String.valueOf(documentSnapshot.get("geolocation.date"))));
                                    }
                                    user.setGeoLocationPosition(geoLocationPosition);
                                }

                                RefreshMapUiService.refreshMap();
                                NotificationPhoneService.notifyUserInProximity(user, false);
                            }
                        }
                    });
            documentSnapshotListenerList.add(listenerRegistration);
        });
    }

    private void addCurrentUserListener() {
        ListenerRegistration documentSnapshotListener = database.collection("User").document(CurrentUser.getInstance().getId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("ListenerProblem :", "Error getting document: " + CurrentUser.getInstance().getId());
                        } else {
                            if (documentSnapshot.get("geolocation.longitude") != null && documentSnapshot.get("geolocation.latitude") != null) {
                                GeoLocationPosition geoLocationPosition = new GeoLocationPosition();
                                geoLocationPosition.setLatitude(Double.valueOf(String.valueOf(documentSnapshot.get("geolocation.latitude"))));
                                geoLocationPosition.setLongitude(Double.valueOf(String.valueOf(documentSnapshot.get("geolocation.longitude"))));
                                if (documentSnapshot.get("geolocation.date") != null) {
                                    geoLocationPosition.setDateRegistration(new Date(String.valueOf(documentSnapshot.get("geolocation.date"))));
                                }
                                CurrentUser.getInstance().setGeoLocationPosition(geoLocationPosition);
                            }

                            RefreshMapUiService.refreshMap();
                        }
                    }
                });
        documentSnapshotListenerList.add(documentSnapshotListener);
    }

}
