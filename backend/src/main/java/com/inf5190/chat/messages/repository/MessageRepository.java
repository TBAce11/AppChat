package com.inf5190.chat.messages.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.model.NewMessageRequest;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Classe qui gère la persistence des messages.
 * 
 * En mémoire pour le moment.
 */
@Repository
public class MessageRepository {
    private static final String COLLECTION_NAME = "messages";
    private final Firestore firestore = FirestoreClient.getFirestore();
    private final CollectionReference messagesCollection = firestore.collection(COLLECTION_NAME);

    public List<Message> getMessages(String fromId) {
        List<Message> retrievedMessages = new ArrayList<Message>();
        ApiFuture<QuerySnapshot> future;

        try {
            if (fromId != null) {
                DocumentSnapshot documentSnapshot = messagesCollection.document(fromId).get().get();
                future = messagesCollection.startAfter(documentSnapshot).get();
            } else {
                future = messagesCollection.limit(20).get();
            }

            // Résultats de la requête
            QuerySnapshot querySnapshot = future.get();

            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                // Conversion du document Firestore en objet Message
                Message message = documentToMessage(document);
                retrievedMessages.add(message);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return retrievedMessages;
    }

    public Message createMessage(NewMessageRequest newMessageRequest) {
        FirestoreMessage firestoreMessage = new FirestoreMessage();
        firestoreMessage.setUsername(newMessageRequest.username());
        firestoreMessage.setTimestamp(Timestamp.now());
        firestoreMessage.setText(newMessageRequest.text());
        firestoreMessage.setImageUrl(null);

        ApiFuture<DocumentReference> future = messagesCollection.add(firestoreMessage);

        try {
            DocumentReference createdMessageReference = future.get();

            ApiFuture<DocumentSnapshot> documentSnapshotFuture = createdMessageReference.get();
            Timestamp timestamp = documentSnapshotFuture.get().getUpdateTime();

            Message createdMessage = new Message(createdMessageReference.getId(),
                    newMessageRequest.text(),
                    newMessageRequest.username(),
                    timestamp.getSeconds(),
                    null);

            return createdMessage;

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Conversion du document Firestore en objet Message
    private Message documentToMessage(DocumentSnapshot document) {
        String id = document.getId();
        String text = document.getString("text");
        String username = document.getString("username");

        Timestamp timestamp = document.getTimestamp("timestamp");
        Long timestampSeconds = timestamp.getSeconds();

        String imageUrl = null; // document.getString("imageUrl");

        return new Message(id, text, username, timestampSeconds, imageUrl);
    }

}
