package com.backKowDevelopment.backAtAll.service;

import com.backKowDevelopment.backAtAll.model.Cliente;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ClienteService {

    private static final String COLLECTION_NAME = "clientes";

    public List<Cliente> getAllClientes() throws ExecutionException, InterruptedException {
        System.out.println("DEBUG: Iniciando consulta a Firestore para clientes");
        Firestore db = FirestoreClient.getFirestore();
        System.out.println("DEBUG: Firestore obtenido: " + db);
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();

        System.out.println("DEBUG: Consulta enviada, esperando respuesta");
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        System.out.println("DEBUG: Documentos obtenidos: " + documents.size());
        List<Cliente> clientes = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Cliente cliente = document.toObject(Cliente.class);
            cliente.setId(document.getId());
            clientes.add(cliente);
        }
        System.out.println("DEBUG: Clientes procesados: " + clientes.size());
        return clientes;
    }

    public Cliente getClienteById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            Cliente cliente = document.toObject(Cliente.class);
            cliente.setId(document.getId());
            return cliente;
        } else {
            return null;
        }
    }

    public String createCliente(Cliente cliente) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> addedDocRef = db.collection(COLLECTION_NAME).add(cliente);
        return addedDocRef.get().getId();
    }

    public void updateCliente(String id, Cliente cliente) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).set(cliente);
        future.get();
    }

    public void deleteCliente(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }
}