package com.inf5190.chat.auth.repository;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Repository
public class UserAccountRepository {
    private static final String COLLECTION_NAME = "userAccounts";
    private final Firestore firestore = FirestoreClient.getFirestore();

    public FirestoreUserAccount getUserAccount(String username) throws InterruptedException, ExecutionException {
        //lecture de Firestore pour récupérer le compte utilisateur depuis la collection
        DocumentSnapshot account = this.firestore.collection(COLLECTION_NAME).document(username).get().get();
        if (!account.exists()) {
            return null;
        }
        return account.toObject(FirestoreUserAccount.class);
    }

    public void createUserAccount(FirestoreUserAccount userAccount) throws InterruptedException, ExecutionException {
        ApiFuture<WriteResult> future = this.firestore.collection(COLLECTION_NAME).document(userAccount.getUsername())
                .create(userAccount);
        future.get();
    }
}
