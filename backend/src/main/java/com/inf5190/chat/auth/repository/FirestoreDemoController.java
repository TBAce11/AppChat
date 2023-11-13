package com.inf5190.chat.auth.repository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@RestController
public class FirestoreDemoController {
    private final String COLLECTION_NAME = "demoObjects";
    private final Firestore firestore = FirestoreClient.getFirestore();

    @GetMapping("/demo")
    public String getDemo() throws InterruptedException, ExecutionException {
        // Notez que pour permettre à la bibliothèque de Firestore de bien convertir les
        // objets Java en document et vice-versa,
        // la classe qui définit l'objet doit avoir un constructeur par défaut et des
        // getters et setters pour tous les champs.
        // Voir FirestoreDemoDocument ci-dessous.
        final FirestoreDemoDocument doc1 = new FirestoreDemoDocument("doc1", 1, Timestamp.of(new Date(0)));
        final FirestoreDemoDocument doc2 = new FirestoreDemoDocument("doc2", 2, Timestamp.of(new Date(12345678)));
        final FirestoreDemoDocument doc3 = new FirestoreDemoDocument("doc3", 3, Timestamp.now());

        // On récupère d'abord une référence vers la collection
        final CollectionReference collectionRef = firestore.collection(
                COLLECTION_NAME);

        // Pour écrire un document on a plusieurs options selon le cas.

        // On peut obtenir une référence à un document avec la méthode document(). Si on
        // ne spécifie pas l'id comme paramètre, Firestore génère un id unique.
        final DocumentReference docRef = collectionRef.document();

        // On peut récupérer l'id avec la méthode getId()
        // À noter qu'on n'a pas encore fait d'appel distant à ce moment-ci.
        final String doc1Id = collectionRef.document().getId();
        System.out.println("document 1 id: " + doc1Id);

        // Ensuite on peut créer le document avec create() et passer l'objet Java.
        // ApiFuture représente le fait que l'appel n'est pas encore complété.
        final ApiFuture<WriteResult> apiFuture1 = docRef.create(doc1);

        // On pourrait ajouter un listener à apiFuture1 qui serait exécuté lorsque
        // l'opération sera complétée.
        // Plus simplement, on peut appeler la méthode get(). Cette méthode bloque jusqu'à
        // ce que le résultat soit disponible et l'opération complétée.
        final WriteResult result1 = apiFuture1.get();
        System.out.println("Operation timestamp: " + result1.getUpdateTime().toDate().getTime());

        // On ajoute un autre document en chaînant les opérations. Ici, on ignore le
        // WriteResult.
        final ApiFuture<WriteResult> result2 = collectionRef.document().create(doc2);
        result2.get(); // on attend la complétion

        // Si on veut spécifier un id particulier, on peut le spécifier dans l'appel à
        // document()
        final String idDoc3 = "id_doc3";
        collectionRef.document(idDoc3).set(doc3).get();

        // Pour lire un document avec un id spécifique on utilise la méthode document(id).
        // Ceci nous retourne un ApiFuture de DocumentSnapshot.
        final ApiFuture<DocumentSnapshot> getFuture = collectionRef.document(idDoc3).get();
        final DocumentSnapshot doc3Snapshot = getFuture.get();

        // Avec le snapshot, on peut effectuer diverses opérations comme tester si le
        // document existe, obtenir une référence (pour update par exemple) et obtenir
        // l'id du document et le convertir en objet Java.
        doc3Snapshot.exists();
        doc3Snapshot.getReference().getId();
        final FirestoreDemoDocument docReadById = doc3Snapshot.toObject(FirestoreDemoDocument.class);
        System.out.println(docReadById);

        // Pour faire une requêtre, on utilise des Query
        // Ici, on ordonne par timestamp pour prendre les 2 dernières entrées.
        // Attention, chaque appel orderBy et limitToLast retourne une nouvelle Query!
        // Ce n'est pas un builder pattern qui modifie la query précédente.
        final Query lastTwoEntriesQuery = collectionRef.orderBy("timestamp").limitToLast(2);

        // Pour exécuter la query on applle get.
        ApiFuture<QuerySnapshot> lastTwoEntries = lastTwoEntriesQuery.get();
        // On attend la complétion.
        QuerySnapshot querySnapshot = lastTwoEntries.get();

        // On peut convertir le résultat en objets Java
        List<FirestoreDemoDocument> documents = querySnapshot.toObjects(FirestoreDemoDocument.class);

        documents.stream().forEach(d -> System.out.println(d));

        return "done";
    }

    private static final class FirestoreDemoDocument {
        private String text;
        private int nombre;
        private Timestamp timestamp;

        public FirestoreDemoDocument(String text, int nombre, Timestamp timestamp) {
            this.text = text;
            this.nombre = nombre;
            this.timestamp = timestamp;
        }

        public FirestoreDemoDocument() {
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getNombre() {
            return nombre;
        }

        public void setNombre(int nombre) {
            this.nombre = nombre;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "FirestoreDemoDocument [text=" + text + ", nombre=" + nombre + ", timestamp=" + timestamp + "]";
        }
    }

}
