package com.backKowDevelopment.backAtAll.service;

import com.backKowDevelopment.backAtAll.model.Cliente;
import com.backKowDevelopment.backAtAll.model.Direccion;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ClienteService {

    private static final String COLLECTION_NAME = "clientes";

    public List<Cliente> getAllClientes() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> docs = db.collection(COLLECTION_NAME).get().get().getDocuments();

        List<Cliente> clientes = new ArrayList<>();
        for (QueryDocumentSnapshot d : docs) {
            try {
                Cliente c = new Cliente();
                c.setId(d.getId());
                c.setApellidos(d.getString("apellidos"));
                c.setNombres(d.getString("nombres"));
                c.setEmpresa(d.getString("empresa"));
                c.setDocumentoIdentidad(d.getString("documentoIdentidad"));
                c.setEmail(d.getString("email"));
                Object tel = d.get("telefono");
                c.setTelefono(tel == null ? null : String.valueOf(tel));
                c.setCreatedAt(d.getDate("createdAt"));

                Map<String, Object> dir = (Map<String, Object>) d.get("direccion");
                if (dir != null) {
                    Direccion dd = new Direccion(
                            (String) dir.get("ciudad"),
                            (String) dir.get("distrito"),
                            (String) dir.get("linea"),
                            (String) dir.get("pais")
                    );
                    c.setDireccion(dd);
                }

                clientes.add(c);
            } catch (Exception ex) {
                System.err.println("ERROR mapeando doc " + d.getId() + ": " + ex.getMessage());
                ex.printStackTrace();
                // opcional: seguir sin reventar todo
            }
        }
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

    // ClienteService.java
    public void updatePartialCliente(String id, Map<String, Object> updates) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        // esto hace update parcial (no borra lo que no mandas)
        db.collection("clientes").document(id).update(updates).get();
    }

    public Cliente getClienteByDocumentoIdentidad(String documento) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Query query = db.collection(COLLECTION_NAME).whereEqualTo("documentoIdentidad", documento);
        ApiFuture<QuerySnapshot> future = query.get();
        QuerySnapshot querySnapshot = future.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        if (!documents.isEmpty()) {
            DocumentSnapshot document = documents.get(0);
            Cliente cliente = document.toObject(Cliente.class);
            cliente.setId(document.getId());
            return cliente;
        } else {
            return null;
        }
    }

}