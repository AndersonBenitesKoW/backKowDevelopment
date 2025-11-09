package com.backKowDevelopment.backAtAll.service;

import com.backKowDevelopment.backAtAll.model.Categoria;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class CategoriaService {

    private static final String COLLECTION_NAME = "categorias";

    public List<Categoria> getAllCategorias() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Categoria> categorias = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Categoria categoria = document.toObject(Categoria.class);
            categoria.setId(document.getId());
            categorias.add(categoria);
        }
        return categorias;
    }

    public Categoria getCategoriaById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            Categoria categoria = document.toObject(Categoria.class);
            categoria.setId(document.getId());
            return categoria;
        } else {
            return null;
        }
    }

    public String createCategoria(Categoria categoria) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> addedDocRef = db.collection(COLLECTION_NAME).add(categoria);
        return addedDocRef.get().getId();
    }

    public void updateCategoria(String id, Categoria categoria) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).set(categoria);
        future.get();
    }

    public void deleteCategoria(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }
    public void updatePartialCategoria(String id, Map<String, Object> updates) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        // Seguridad: nunca permitir sobrescribir estos campos
        updates.remove("id");
        updates.remove("createdAt");

        // Si viene nombre, opcional: recalcular slug
        if (updates.containsKey("nombre")) {
            Object nombre = updates.get("nombre");
            if (nombre instanceof String s && !s.isBlank()) {
                updates.put("slug", toSlug(s));
            }
        }

        // Update parcial (no borra campos no enviados)
        db.collection(COLLECTION_NAME)
                .document(id)
                .update(updates)    // también acepta dot notation para anidados
                .get();
    }

    // helper opcional
    private String toSlug(String s) {
        String slug = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return slug;
    }


}