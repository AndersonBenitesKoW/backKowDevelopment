package com.backKowDevelopment.backAtAll.service;

import com.backKowDevelopment.backAtAll.model.Pago;
import com.backKowDevelopment.backAtAll.model.PagoCreateDto;
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

    private Firestore db() {
        return FirestoreClient.getFirestore();
    }

    // Listar
    public List<Pago> getAllPagos() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db()
                .collection(COLLECTION_NAME)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();

        List<Pago> pagos = new ArrayList<>();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Pago p = document.toObject(Pago.class);
            p.setId(document.getId());
            pagos.add(p);
        }
        return pagos;
    }

    // Obtener por id
    public Pago getPagoById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db().collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();
        if (!document.exists()) return null;

        Pago p = document.toObject(Pago.class);
        if (p != null) p.setId(document.getId());
        return p;
    }

    // Crear desde DTO
    public String createPago(PagoCreateDto in) throws ExecutionException, InterruptedException {
        Pago p = new Pago();
        p.setEstado(in.getEstado());
        p.setMetodo(in.getMetodo());
        p.setMoneda(in.getMoneda());
        p.setMonto(in.getMonto());
        p.setProveedorId(in.getProveedorId());

        // mapear pedidoId (String) -> DocumentReference
        if (in.getPedidoId() != null && !in.getPedidoId().isBlank()) {
            p.setPedidoId(db().document("pedidos/" + in.getPedidoId()));
        } else {
            p.setPedidoId(null);
        }

        // id debe ser null para que Firestore lo genere
        p.setId(null);

        return db().collection(COLLECTION_NAME).add(p).get().getId();
    }

    // Actualizar (merge) desde DTO
    public void updatePago(String id, PagoCreateDto in) throws ExecutionException, InterruptedException {
        DocumentReference ref = db().collection(COLLECTION_NAME).document(id);

        // Construimos un map parcial para merge
        var data = new java.util.HashMap<String, Object>();
        if (in.getEstado() != null) data.put("estado", in.getEstado());
        if (in.getMetodo() != null) data.put("metodo", in.getMetodo());
        if (in.getMoneda() != null) data.put("moneda", in.getMoneda());
        // cuidado con primitivos: si quieres permitir 0.0 explícito, elimina este if
        data.put("monto", in.getMonto());
        if (in.getProveedorId() != null) data.put("proveedorId", in.getProveedorId());

        if (in.getPedidoId() != null) {
            data.put("pedidoId", db().document("pedidos/" + in.getPedidoId()));
        }

        ref.set(data, SetOptions.merge()).get();
    }

    // Borrar
    public void deletePago(String id) throws ExecutionException, InterruptedException {
        db().collection(COLLECTION_NAME).document(id).delete().get();
    }
}
