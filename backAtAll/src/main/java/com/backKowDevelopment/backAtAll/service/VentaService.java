package com.backKowDevelopment.backAtAll.service;

import com.backKowDevelopment.backAtAll.model.Venta;
import com.backKowDevelopment.backAtAll.model.VentaDetalle;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.FieldValue;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class VentaService {

    private static final String COLLECTION_NAME = "ventas";
    private static final String SUBCOLLECTION_NAME = "detalles";

    // Colecciones para referencias (ajusta si usas otros nombres)
    private static final String CLIENTES_COL = "clientes";
    private static final String PEDIDOS_COL = "pedidos";

    /* ====================== PUBLIC ====================== */

    public List<Venta> getAllVentas() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        QuerySnapshot snap = db.collection(COLLECTION_NAME).get().get();

        List<Venta> ventas = new ArrayList<>();
        for (QueryDocumentSnapshot doc : snap.getDocuments()) {
            Venta v = mapVentaDocToModel(doc);
            v.setDetalles(getVentaDetalles(db, v.getId()));
            ventas.add(v);
        }
        return ventas;
    }

    public Venta getVentaById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection(COLLECTION_NAME).document(id).get().get();
        if (!doc.exists()) return null;

        Venta v = mapVentaDocToModel(doc);
        v.setDetalles(getVentaDetalles(db, id));
        return v;
    }

    public String createVenta(Venta venta) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> data = mapVentaModelToDoc(db, venta, /*isUpdate*/ false);
        // Agregar createdAt con timestamp del servidor
        data.put("createdAt", FieldValue.serverTimestamp());

        DocumentReference ref = db.collection(COLLECTION_NAME).add(data).get();
        String ventaId = ref.getId();

        if (venta.getDetalles() != null) {
            for (VentaDetalle dt : venta.getDetalles()) {
                Map<String, Object> detalleDoc = mapDetalleModelToDoc(db, dt);
                db.collection(COLLECTION_NAME).document(ventaId)
                        .collection(SUBCOLLECTION_NAME).add(detalleDoc).get();
            }
        }
        return ventaId;
    }

    public void updateVenta(String id, Venta venta) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> data = mapVentaModelToDoc(db, venta, /*isUpdate*/ true);
        db.collection(COLLECTION_NAME).document(id).set(data, SetOptions.merge()).get();

        // Si envías detalles, reponemos la subcolección (simple y seguro)
        if (venta.getDetalles() != null) {
            deleteVentaDetalles(db, id);
            for (VentaDetalle dt : venta.getDetalles()) {
                Map<String, Object> detalleDoc = mapDetalleModelToDoc(db, dt);
                db.collection(COLLECTION_NAME).document(id)
                        .collection(SUBCOLLECTION_NAME).add(detalleDoc).get();
            }
        }
    }

    public void deleteVenta(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        deleteVentaDetalles(db, id);
        db.collection(COLLECTION_NAME).document(id).delete().get();
    }

    /* ====================== MAPPING ====================== */

    private Venta mapVentaDocToModel(DocumentSnapshot doc) {
        Venta v = new Venta();
        v.setId(doc.getId());

        // clienteId: DocumentReference o String
        Object cli = doc.get("clienteId");
        if (cli instanceof DocumentReference ref) v.setClienteId(ref.getId());
        else if (cli instanceof String s) v.setClienteId(s);

        // pedidoId: DocumentReference o String
        Object ped = doc.get("pedidoId");
        if (ped instanceof DocumentReference ref) v.setPedidoId(ref.getId());
        else if (ped instanceof String s) v.setPedidoId(s);

        v.setCreatedAt(doc.getDate("createdAt"));
        v.setNumero(doc.getString("numero"));
        v.setSerie(doc.getString("serie"));
        v.setTipoComprobante(doc.getString("tipoComprobante"));

        // totales como mapa -> POJO
        Map<String, Object> t = (Map<String, Object>) doc.get("totales");
        if (t != null) {
            com.backKowDevelopment.backAtAll.model.Totales tot = new com.backKowDevelopment.backAtAll.model.Totales();
            tot.setSubtotal(num(t.get("subtotal")));
            tot.setImpuestos(num(t.get("impuestos")));
            tot.setDescuento(num(t.get("descuento")));
            tot.setTotal(num(t.get("total")));
            v.setTotales(tot);
        }
        return v;
    }

    private List<VentaDetalle> getVentaDetalles(Firestore db, String ventaId)
            throws ExecutionException, InterruptedException {
        // Obtiene la subcolección "detalles" de la venta
        QuerySnapshot snap = db.collection(COLLECTION_NAME)
                .document(ventaId)
                .collection(SUBCOLLECTION_NAME)
                .get()
                .get();

        List<VentaDetalle> detalles = new ArrayList<>();

        for (QueryDocumentSnapshot d : snap.getDocuments()) {
            VentaDetalle dt = new VentaDetalle();
            dt.setId(d.getId());

            // Manejo seguro para cantidad
            Object cantidad = d.get("cantidad");
            dt.setCantidad(cantidad != null ? ((Number) cantidad).intValue() : 0);

            dt.setConcepto(d.getString("concepto"));
            dt.setPrecioUnitario(num(d.get("precioUnitario")));
            dt.setDescuento(num(d.get("descuento")));
            dt.setImpuestos(num(d.get("impuestos")));
            dt.setTotal(num(d.get("total")));

            // Manejo de categoría como referencia o string
            Object cat = d.get("categoriaId");
            if (cat instanceof DocumentReference ref) {
                dt.setCategoriaId(ref.getId());
            } else if (cat instanceof String s) {
                dt.setCategoriaId(s);
            }

            detalles.add(dt);
        }

        return detalles;
    }

    private Map<String, Object> mapVentaModelToDoc(Firestore db, Venta v, boolean isUpdate) {
        Map<String, Object> data = new HashMap<>();

        // Referencias: si vienen como String, guardamos como DocumentReference
        if (v.getClienteId() != null && !v.getClienteId().isBlank()) {
            data.put("clienteId", db.collection(CLIENTES_COL).document(v.getClienteId()));
        }
        if (v.getPedidoId() != null && !v.getPedidoId().isBlank()) {
            data.put("pedidoId", db.collection(PEDIDOS_COL).document(v.getPedidoId()));
        }

        // No incluir createdAt si tiene @ServerTimestamp, Firestore lo maneja automáticamente
        putIfNotNull(data, "numero", v.getNumero());
        putIfNotNull(data, "serie", v.getSerie());
        putIfNotNull(data, "tipoComprobante", v.getTipoComprobante());

        if (v.getTotales() != null) {
            Map<String, Object> t = new HashMap<>();
            t.put("subtotal", v.getTotales().getSubtotal());
            t.put("impuestos", v.getTotales().getImpuestos());
            t.put("descuento", v.getTotales().getDescuento());
            t.put("total", v.getTotales().getTotal());
            data.put("totales", t);
        }

        // Nunca guardamos "detalles" en el documento principal
        return data;
    }

    private Map<String, Object> mapDetalleModelToDoc(Firestore db, VentaDetalle dt) {
        Map<String, Object> m = new HashMap<>();
        m.put("cantidad", dt.getCantidad());
        m.put("concepto", dt.getConcepto());
        m.put("precioUnitario", dt.getPrecioUnitario());
        m.put("descuento", dt.getDescuento());
        m.put("impuestos", dt.getImpuestos());
        m.put("total", dt.getTotal());

        if (dt.getCategoriaId() != null && !dt.getCategoriaId().isBlank()) {
            m.put("categoriaId", db.collection("categorias").document(dt.getCategoriaId()));
        }
        return m;
    }

    /* ====================== UTILS ====================== */

    private void deleteVentaDetalles(Firestore db, String ventaId)
            throws ExecutionException, InterruptedException {
        QuerySnapshot snap = db.collection(COLLECTION_NAME).document(ventaId)
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

    public void patchVenta(String id, Map<String, Object> updates)
            throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);

        ApiFuture<WriteResult> future = docRef.update(updates);
        future.get();
    }


    //si estan los cambios
}