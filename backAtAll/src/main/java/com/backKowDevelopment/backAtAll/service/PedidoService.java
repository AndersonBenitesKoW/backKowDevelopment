package com.backKowDevelopment.backAtAll.service;

import com.backKowDevelopment.backAtAll.model.Pedido;
import com.backKowDevelopment.backAtAll.model.PedidoItem;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PedidoService {

    private static final String COLLECTION_NAME = "pedidos";
    private static final String SUBCOLLECTION_NAME = "items";

    public List<Pedido> getAllPedidos() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Pedido> pedidos = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Pedido pedido = document.toObject(Pedido.class);
            pedido.setId(document.getId());
            // Load subcollection items
            pedido.setItems(getPedidoItems(pedido.getId()));
            pedidos.add(pedido);
        }
        return pedidos;
    }

    public Pedido getPedidoById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            Pedido pedido = document.toObject(Pedido.class);
            pedido.setId(document.getId());
            // Load subcollection items
            pedido.setItems(getPedidoItems(id));
            return pedido;
        } else {
            return null;
        }
    }

    public String createPedido(Pedido pedido) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> addedDocRef = db.collection(COLLECTION_NAME).add(pedido);
        String pedidoId = addedDocRef.get().getId();
        // Add items to subcollection
        if (pedido.getItems() != null) {
            for (PedidoItem item : pedido.getItems()) {
                db.collection(COLLECTION_NAME).document(pedidoId).collection(SUBCOLLECTION_NAME).add(item);
            }
        }
        return pedidoId;
    }

    public void updatePedido(String id, Pedido pedido) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).set(pedido);
        future.get();
        // Update items subcollection if needed
        if (pedido.getItems() != null) {
            // Delete existing items
            deletePedidoItems(id);
            // Add new items
            for (PedidoItem item : pedido.getItems()) {
                db.collection(COLLECTION_NAME).document(id).collection(SUBCOLLECTION_NAME).add(item);
            }
        }
    }

    public void deletePedido(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Delete subcollection items first
        deletePedidoItems(id);
        // Delete main document
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }

    private List<PedidoItem> getPedidoItems(String pedidoId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).document(pedidoId).collection(SUBCOLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<PedidoItem> items = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            PedidoItem item = document.toObject(PedidoItem.class);
            item.setId(document.getId());
            items.add(item);
        }
        return items;
    }

    private void deletePedidoItems(String pedidoId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).document(pedidoId).collection(SUBCOLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            db.collection(COLLECTION_NAME).document(pedidoId).collection(SUBCOLLECTION_NAME).document(document.getId()).delete();
        }
    }
}