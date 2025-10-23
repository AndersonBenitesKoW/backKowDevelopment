package com.backKowDevelopment.backAtAll.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@Profile("!test")
public class FirebaseConfig {

    @Bean
    public Firestore firestore() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream credsStream = loadCreds();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credsStream))
                    .build();
            FirebaseApp.initializeApp(options);
        }
        return FirestoreClient.getFirestore();
    }

    private InputStream loadCreds() throws IOException {
        // 1) ENV con JSON completo
        String rawJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
        if (rawJson != null && !rawJson.isBlank()) {
            return new ByteArrayInputStream(rawJson.getBytes(StandardCharsets.UTF_8));
        }

        // 2) ENV con ruta a archivo
        String keyPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_KEY_PATH"); // <-- este nombre usa tu código
        if (keyPath != null && !keyPath.isBlank()) {
            return new FileInputStream(keyPath);
        }

        // 3) Fallback a resources
        return new ClassPathResource("firebase-service-account.json").getInputStream();
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

