package com.inf5190.chat.messages.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Bucket.BlobTargetOption;
import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.firebase.cloud.StorageClient;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.model.NewMessageRequest;

import io.jsonwebtoken.io.Decoders;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

/**
 * Classe qui gère la persistence des messages.
 * 
 * En mémoire pour le moment.
 */
@Repository
public class MessageRepository {
    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(ChatApplication.class);
    private static final String COLLECTION_NAME = "messages";
    private static final String BUCKET_NAME = "inf5190-chat-51bc0.appspot.com";
    private static final int DEFAULT_LIMIT = 20;

    private final Firestore firestore = FirestoreClient.getFirestore();

    public List<Message> getMessages(String fromId) throws InterruptedException, ExecutionException {
        Query messageQuery = this.firestore.collection(COLLECTION_NAME).orderBy("timestamp");

        if (fromId != null) {
            DocumentSnapshot fromIdDocument = this.firestore.document(fromId).get().get();
            if (!fromIdDocument.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message with id " + fromId + " not found.");
            }
            messageQuery = messageQuery.startAfter(fromIdDocument);
        } else {
            messageQuery = messageQuery.limitToLast(DEFAULT_LIMIT);
        }

        return messageQuery.get().get().toObjects(FirestoreMessage.class).stream().map(message -> {
            return this.toMessage(message.getId(), message);
        }).toList();
    }

    public Message createMessage(NewMessageRequest message) throws InterruptedException, ExecutionException {
        DocumentReference ref = this.firestore.collection(COLLECTION_NAME).document();

        String imageUrl = null;

        // Entreposage de l'image dans Cloud Storage
        if (message.imageData() != null) {
            Bucket b = StorageClient.getInstance().bucket(BUCKET_NAME);
            String path = String.format("images/%s.%s", ref.getId(), message.imageData().type());
            b.create(path, Decoders.BASE64.decode(message.imageData().data()),
                    BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ));
            imageUrl = String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, path);
        }

        FirestoreMessage firestoreMessage = new FirestoreMessage(
                message.username(),
                Timestamp.now(),
                message.text(),
                imageUrl);

        ref.create(firestoreMessage).get();
        return this.toMessage(ref.getId(), firestoreMessage);

    }

    // Conversion du message Firestore en objet Message
    private Message toMessage(String id, FirestoreMessage firestoreMessage) {
        return new Message(id, firestoreMessage.getText(), firestoreMessage.getUsername(),
                firestoreMessage.getTimestamp().toDate().getTime(), firestoreMessage.getImageUrl());
    }
}
