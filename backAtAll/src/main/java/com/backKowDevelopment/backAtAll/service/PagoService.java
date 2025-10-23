package com.backKowDevelopment.backAtAll.service;

import com.backKowDevelopment.backAtAll.model.Pago;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PagoService {

    private static final String COLLECTION_NAME = "pagos";

    public List<Pago> getAllPagos() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Pago> pagos = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Pago pago = document.toObject(Pago.class);
            pago.setId(document.getId());
            pagos.add(pago);
        }
        return pagos;
    }

    public Pago getPagoById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            Pago pago = document.toObject(Pago.class);
            pago.setId(document.getId());
            return pago;
        } else {
            return null;
        }
    }

    public String createPago(Pago pago) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> addedDocRef = db.collection(COLLECTION_NAME).add(pago);
        return addedDocRef.get().getId();
    }

    public void updatePago(String id, Pago pago) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).set(pago);
        future.get();
    }

    public void deletePago(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }
}