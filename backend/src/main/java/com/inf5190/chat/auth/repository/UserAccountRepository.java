package com.inf5190.chat.auth.repository;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

@Repository
public class UserAccountRepository {
    private static final String COLLECTION_NAME = "userAccounts";
    private final Firestore firestore = FirestoreClient.getFirestore();

    public FirestoreUserAccount getUserAccount(String username) throws InterruptedException, ExecutionException {
        DocumentSnapshot documentSnapshot = firestore.collection(COLLECTION_NAME).document(username).get().get();

        if (documentSnapshot.exists()) {
            return documentSnapshot.toObject(FirestoreUserAccount.class);
        } else {
            return null;
        }
    }

    public void setUserAccount(FirestoreUserAccount userAccount) throws InterruptedException, ExecutionException {
        firestore.collection(COLLECTION_NAME).document(userAccount.getUsername()).set(userAccount);
    }
}
