package com.backKowDevelopment.backAtAll.service;

import com.backKowDevelopment.backAtAll.model.Pedido;
import com.backKowDevelopment.backAtAll.model.PedidoItem;
import com.backKowDevelopment.backAtAll.model.Totales;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class PedidoService {

    private static final String COLLECTION_NAME = "pedidos";
    private static final String SUBCOLLECTION_NAME = "items";

    // Colecciones para referencias (ajusta si usas otros nombres)
    private static final String CLIENTES_COL = "clientes";
    private static final String USUARIOS_COL = "usuarios";
    private static final String CATEGORIAS_COL = "categorias";

    /* ====================== PUBLIC ====================== */

    public List<Pedido> getAllPedidos() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        QuerySnapshot snap = db.collection(COLLECTION_NAME).get().get();

        List<Pedido> pedidos = new ArrayList<>();
        for (QueryDocumentSnapshot doc : snap.getDocuments()) {
            Pedido p = mapPedidoDocToModel(doc);
            p.setItems(getPedidoItems(db, p.getId()));
            pedidos.add(p);
        }
        return pedidos;
    }

    public Pedido getPedidoById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection(COLLECTION_NAME).document(id).get().get();
        if (!doc.exists()) return null;

        Pedido p = mapPedidoDocToModel(doc);
        p.setItems(getPedidoItems(db, id));
        return p;
    }

    public String createPedido(Pedido pedido) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> data = mapPedidoModelToDoc(db, pedido, /*isUpdate*/ false);
        DocumentReference ref = db.collection(COLLECTION_NAME).add(data).get();
        String pedidoId = ref.getId();

        if (pedido.getItems() != null) {
            for (PedidoItem it : pedido.getItems()) {
                Map<String, Object> itemDoc = mapItemModelToDoc(db, it);
                db.collection(COLLECTION_NAME).document(pedidoId)
                        .collection(SUBCOLLECTION_NAME).add(itemDoc).get();
            }
        }
        return pedidoId;
    }

    public void updatePedido(String id, Pedido pedido) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> data = mapPedidoModelToDoc(db, pedido, /*isUpdate*/ true);
        db.collection(COLLECTION_NAME).document(id).set(data, SetOptions.merge()).get();

        // Si envías items, reponemos la subcolección (simple y seguro)
        if (pedido.getItems() != null) {
            deletePedidoItems(db, id);
            for (PedidoItem it : pedido.getItems()) {
                Map<String, Object> itemDoc = mapItemModelToDoc(db, it);
                db.collection(COLLECTION_NAME).document(id)
                        .collection(SUBCOLLECTION_NAME).add(itemDoc).get();
            }
        }
    }

    public void deletePedido(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        deletePedidoItems(db, id);
        db.collection(COLLECTION_NAME).document(id).delete().get();
    }

    /* ====================== MAPPING ====================== */

    private Pedido mapPedidoDocToModel(DocumentSnapshot doc) {
        Pedido p = new Pedido();
        p.setId(doc.getId());

        // clienteId: DocumentReference o String
        Object cli = doc.get("clienteId");
        if (cli instanceof DocumentReference ref) p.setClienteId(ref.getId());
        else if (cli instanceof String s) p.setClienteId(s);

        // createdBy: DocumentReference o String
        Object cby = doc.get("createdBy");
        if (cby instanceof DocumentReference ref) p.setCreatedBy(ref.getId());
        else if (cby instanceof String s) p.setCreatedBy(s);

        p.setCreatedAt(doc.getDate("createdAt"));
        p.setUpdatedAt(doc.getDate("updatedAt"));
        p.setEstado(doc.getString("estado"));
        p.setMoneda(doc.getString("moneda"));

        // totales como mapa -> POJO
        Map<String, Object> t = (Map<String, Object>) doc.get("totales");
        if (t != null) {
            Totales tot = new Totales();
            tot.setSubtotal(num(t.get("subtotal")));
            tot.setImpuestos(num(t.get("impuestos")));
            tot.setDescuento(num(t.get("descuento")));
            tot.setTotal(num(t.get("total")));
            p.setTotales(tot);
        }
        return p;
    }
    private List<PedidoItem> getPedidoItems(Firestore db, String pedidoId)
            throws ExecutionException, InterruptedException {
        // Obtiene la subcolección "items" del pedido
        QuerySnapshot snap = db.collection(COLLECTION_NAME)
                .document(pedidoId)
                .collection(SUBCOLLECTION_NAME)
                .get()
                .get();

        List<PedidoItem> items = new ArrayList<>();

        for (QueryDocumentSnapshot d : snap.getDocuments()) {
            PedidoItem it = new PedidoItem();
            it.setId(d.getId());

            // Manejo seguro para cantidad
            Object cantidad = d.get("cantidad");
            it.setCantidad(cantidad != null ? ((Number) cantidad).intValue() : 0);

            it.setConcepto(d.getString("concepto"));
            it.setPrecioUnitario(num(d.get("precioUnitario")));
            it.setDescuento(num(d.get("descuento")));
            it.setImpuestos(num(d.get("impuestos")));
            it.setTotal(num(d.get("total")));

            // Manejo de categoría como referencia o string
            Object cat = d.get("categoriaId");
            if (cat instanceof DocumentReference ref) {
                it.setCategoriaId(ref.getId());
            } else if (cat instanceof String s) {
                it.setCategoriaId(s);
            }

            items.add(it);
        }

        return items;
    }

    private Map<String, Object> mapPedidoModelToDoc(Firestore db, Pedido p, boolean isUpdate) {
        Map<String, Object> data = new HashMap<>();

        // Referencias: si vienen como String, guardamos como DocumentReference
        if (p.getClienteId() != null && !p.getClienteId().isBlank()) {
            data.put("clienteId", db.collection(CLIENTES_COL).document(p.getClienteId()));
        }
        if (p.getCreatedBy() != null && !p.getCreatedBy().isBlank()) {
            data.put("createdBy", db.collection(USUARIOS_COL).document(p.getCreatedBy()));
        }

        putIfNotNull(data, "createdAt", p.getCreatedAt());
        putIfNotNull(data, "updatedAt", p.getUpdatedAt());
        putIfNotNull(data, "estado", p.getEstado());
        putIfNotNull(data, "moneda", p.getMoneda());

        if (p.getTotales() != null) {
            Map<String, Object> t = new HashMap<>();
            t.put("subtotal", p.getTotales().getSubtotal());
            t.put("impuestos", p.getTotales().getImpuestos());
            t.put("descuento", p.getTotales().getDescuento());
            t.put("total", p.getTotales().getTotal());
            data.put("totales", t);
        }

        // Nunca guardamos "items" en el documento principal
        return data;
    }

    private Map<String, Object> mapItemModelToDoc(Firestore db, PedidoItem it) {
        Map<String, Object> m = new HashMap<>();
        m.put("cantidad", it.getCantidad());
        m.put("concepto", it.getConcepto());
        m.put("precioUnitario", it.getPrecioUnitario());
        m.put("descuento", it.getDescuento());
        m.put("impuestos", it.getImpuestos());
        m.put("total", it.getTotal());

        if (it.getCategoriaId() != null && !it.getCategoriaId().isBlank()) {
            m.put("categoriaId", db.collection(CATEGORIAS_COL).document(it.getCategoriaId()));
        }
        return m;
    }

    /* ====================== UTILS ====================== */

    private void deletePedidoItems(Firestore db, String pedidoId)
            throws ExecutionException, InterruptedException {
        QuerySnapshot snap = db.collection(COLLECTION_NAME).document(pedidoId)
                .collection(SUBCOLLECTION_NAME).get().get();
        for (QueryDocumentSnapshot d : snap.getDocuments()) {
            d.getReference().delete().get();
        }
    }

    private static Double num(Object o) {
        return o == null ? 0.0 : ((Number) o).doubleValue();
    }

    private static void putIfNotNull(Map<String, Object> map, String k, Object v) {
        if (v != null) map.put(k, v);
    }

    public void patchPedido(String id, Map<String, Object> updates)
            throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);

        ApiFuture<WriteResult> future = docRef.update(updates);
        future.get();
    }





}
