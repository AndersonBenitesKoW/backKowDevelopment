package com.backKowDevelopment.backAtAll.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Profile("!test") // No cargar Firebase en pruebas
public class FirebaseConfig {

    @Bean
    public Firestore firestore() throws Exception {
        // 1) Intentar ADC (Cloud Run usa la service account del servicio)
        var credentials = GoogleCredentials.getApplicationDefault();

        // 2) Si quieres fallback local (opcional):
        String keyPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_KEY_PATH");
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);
        }
        return FirestoreClient.getFirestore();
    }
}


/*
@Bean
public Firestore firestore() throws IOException {
    // Cambia esta ruta por la ubicación real de tu archivo de servicio de Firebase
    String firebaseKeyPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_KEY_PATH");
    if (firebaseKeyPath == null || firebaseKeyPath.isEmpty()) {
        firebaseKeyPath = "src/main/resources/firebase-service-account.json"; // Ruta por defecto
    }

    FileInputStream serviceAccount = new FileInputStream(firebaseKeyPath);

    FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

    if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
    }

    return FirestoreClient.getFirestore();
}
*/

