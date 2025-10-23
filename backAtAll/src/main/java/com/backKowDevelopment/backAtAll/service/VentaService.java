package com.backKowDevelopment.backAtAll.service;

import com.backKowDevelopment.backAtAll.model.Venta;
import com.backKowDevelopment.backAtAll.model.VentaDetalle;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class VentaService {

    private static final String COLLECTION_NAME = "ventas";
    private static final String SUBCOLLECTION_NAME = "detalles";

    public List<Venta> getAllVentas() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Venta> ventas = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Venta venta = document.toObject(Venta.class);
            venta.setId(document.getId());
            // Load subcollection detalles
            venta.setDetalles(getVentaDetalles(venta.getId()));
            ventas.add(venta);
        }
        return ventas;
    }

    public Venta getVentaById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            Venta venta = document.toObject(Venta.class);
            venta.setId(document.getId());
            // Load subcollection detalles
            venta.setDetalles(getVentaDetalles(id));
            return venta;
        } else {
            return null;
        }
    }

    public String createVenta(Venta venta) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> addedDocRef = db.collection(COLLECTION_NAME).add(venta);
        String ventaId = addedDocRef.get().getId();
        // Add detalles to subcollection
        if (venta.getDetalles() != null) {
            for (VentaDetalle detalle : venta.getDetalles()) {
                db.collection(COLLECTION_NAME).document(ventaId).collection(SUBCOLLECTION_NAME).add(detalle);
            }
        }
        return ventaId;
    }

    public void updateVenta(String id, Venta venta) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).set(venta);
        future.get();
        // Update detalles subcollection if needed
        if (venta.getDetalles() != null) {
            // Delete existing detalles
            deleteVentaDetalles(id);
            // Add new detalles
            for (VentaDetalle detalle : venta.getDetalles()) {
                db.collection(COLLECTION_NAME).document(id).collection(SUBCOLLECTION_NAME).add(detalle);
            }
        }
    }

    public void deleteVenta(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Delete subcollection detalles first
        deleteVentaDetalles(id);
        // Delete main document
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }

    private List<VentaDetalle> getVentaDetalles(String ventaId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).document(ventaId).collection(SUBCOLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<VentaDetalle> detalles = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            VentaDetalle detalle = document.toObject(VentaDetalle.class);
            detalle.setId(document.getId());
            detalles.add(detalle);
        }
        return detalles;
    }

    private void deleteVentaDetalles(String ventaId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).document(ventaId).collection(SUBCOLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            db.collection(COLLECTION_NAME).document(ventaId).collection(SUBCOLLECTION_NAME).document(document.getId()).delete();
        }
    }
}