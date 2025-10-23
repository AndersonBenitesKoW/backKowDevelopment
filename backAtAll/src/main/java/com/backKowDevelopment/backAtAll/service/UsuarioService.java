package com.backKowDevelopment.backAtAll.service;

import com.backKowDevelopment.backAtAll.model.Usuario;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UsuarioService {

    private static final String COLLECTION_NAME = "usuarios";

    public List<Usuario> getAllUsuarios() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Usuario> usuarios = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Usuario usuario = document.toObject(Usuario.class);
            usuario.setId(document.getId());
            usuarios.add(usuario);
        }
        return usuarios;
    }

    public Usuario getUsuarioById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            Usuario usuario = document.toObject(Usuario.class);
            usuario.setId(document.getId());
            return usuario;
        } else {
            return null;
        }
    }

    public String createUsuario(Usuario usuario) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> addedDocRef = db.collection(COLLECTION_NAME).add(usuario);
        return addedDocRef.get().getId();
    }

    public void updateUsuario(String id, Usuario usuario) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).set(usuario);
        future.get();
    }

    public void deleteUsuario(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }
}