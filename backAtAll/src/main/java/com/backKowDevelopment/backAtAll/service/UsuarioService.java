package com.backKowDevelopment.backAtAll.service;

import com.backKowDevelopment.backAtAll.model.Usuario;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class UsuarioService {

    private static final String COLLECTION_NAME = "usuarios";

    private Firestore db() {
        return FirestoreClient.getFirestore();
    }

    /* ====================== PUBLIC ====================== */

    public List<Usuario> getAllUsuarios() throws ExecutionException, InterruptedException {
        try {
            ApiFuture<QuerySnapshot> future = db()
                    .collection(COLLECTION_NAME)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Usuario> usuarios = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                usuarios.add(mapUsuarioDocToModel(document));
            }
            return usuarios;

        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            System.err.println("Firestore orderBy fallback: " +
                    (cause != null ? cause.getMessage() : ee.getMessage()));

            List<QueryDocumentSnapshot> docs = db()
                    .collection(COLLECTION_NAME)
                    .get().get().getDocuments();

            List<Usuario> usuarios = new ArrayList<>();
            for (QueryDocumentSnapshot d : docs) {
                usuarios.add(mapUsuarioDocToModel(d));
            }

            usuarios.sort(Comparator.comparing(
                    Usuario::getCreatedAt,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ).reversed());

            return usuarios;
        }
    }

    public Usuario getUsuarioById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db().collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();
        if (!document.exists()) return null;
        return mapUsuarioDocToModel(document);
    }

    public String createUsuario(Usuario usuario) throws ExecutionException, InterruptedException {
        Map<String, Object> data = mapUsuarioModelToDoc(usuario, false);
        data.put("createdAt", FieldValue.serverTimestamp());
        data.put("updatedAt", FieldValue.serverTimestamp());
        return db().collection(COLLECTION_NAME).add(data).get().getId();
    }

    public void updateUsuario(String id, Usuario usuario) throws ExecutionException, InterruptedException {
        Map<String, Object> data = mapUsuarioModelToDoc(usuario, true);
        data.remove("createdAt");
        data.put("updatedAt", FieldValue.serverTimestamp());
        db().collection(COLLECTION_NAME).document(id).set(data, SetOptions.merge()).get();
    }

    public void patchUsuario(String id, Map<String, Object> updates) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db().collection(COLLECTION_NAME).document(id);
        DocumentSnapshot snapshot = docRef.get().get();
        if (!snapshot.exists()) throw new RuntimeException("Usuario no encontrado con ID: " + id);

        updates.remove("createdAt");
        updates.put("updatedAt", FieldValue.serverTimestamp());
        docRef.set(updates, SetOptions.merge()).get();
    }

    public void deleteUsuario(String id) throws ExecutionException, InterruptedException {
        db().collection(COLLECTION_NAME).document(id).delete().get();
    }

    /* ====================== MAPPING ====================== */

    private Usuario mapUsuarioDocToModel(DocumentSnapshot doc) {
        Usuario u = new Usuario();
        u.setId(doc.getId());

        Boolean act = boolFlexible(doc.get("active"));
        u.setActive(Boolean.TRUE.equals(act));

        u.setCreatedAt(dateFlexible(doc.get("createdAt")));
        u.setNombres(doc.getString("nombres"));
        u.setApellidos(doc.getString("apellidos"));
        u.setContraseña(doc.getString("contraseña"));
        u.setEmail(doc.getString("email"));
        u.setRol(doc.getString("rol"));
        u.setUpdatedAt(dateFlexible(doc.get("updatedAt")));
        return u;
    }

    private Map<String, Object> mapUsuarioModelToDoc(Usuario u, boolean isUpdate) {
        Map<String, Object> data = new HashMap<>();
        putIfNotNull(data, "active", u.isActive());
        putIfNotNull(data, "nombres", u.getNombres());
        putIfNotNull(data, "apellidos", u.getApellidos());
        putIfNotNull(data, "contraseña", u.getContraseña());
        putIfNotNull(data, "email", u.getEmail());
        putIfNotNull(data, "rol", u.getRol());
        return data;
    }

    /* ====================== UTILS ====================== */

    private static void putIfNotNull(Map<String, Object> map, String k, Object v) {
        if (v != null) map.put(k, v);
    }

    private static Boolean boolFlexible(Object v) {
        if (v == null) return null;
        if (v instanceof Boolean b) return b;
        if (v instanceof String s) return Boolean.parseBoolean(s);
        if (v instanceof Number n) return n.intValue() != 0;
        return null;
    }

    private static Date dateFlexible(Object v) {
        if (v == null) return null;
        if (v instanceof Timestamp ts) return ts.toDate();
        if (v instanceof Date dt) return dt;
        if (v instanceof String s) {
            try { return Date.from(java.time.Instant.parse(s)); }
            catch (Exception ignore) { return null; }
        }
        return null;
    }

    public DocumentSnapshot findDocById(String id) throws ExecutionException, InterruptedException {
        return db().collection(COLLECTION_NAME).document(id).get().get();
    }

}
