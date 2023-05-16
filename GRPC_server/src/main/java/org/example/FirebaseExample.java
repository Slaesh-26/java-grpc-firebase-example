package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.example.utils.FirebaseURL;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Executor;

@Component
public class FirebaseExample {
    public void initialize() throws IOException {
        var dbConfigStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("secret.json");

        var dbURLStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("db-url.json");

        var objectMapper = new ObjectMapper();
        var dbURLData = objectMapper.readValue(dbURLStream, FirebaseURL.class);

        var credentials = GoogleCredentials.fromStream(dbConfigStream);

        var options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setDatabaseUrl(dbURLData.URL)
                .build();

        FirebaseApp.initializeApp(options);

        var db = FirebaseDatabase.getInstance();
        var listRoot = db.getReference("test/list");

        subscribeOnChildrenChanged(listRoot);
        writeDataAsync(listRoot);
    }

    private void writeDataAsync(DatabaseReference reference) {
        for (int i = 1; i < 50; i++) {
            var value = Integer.toString(i);
            var task = reference.child(value).setValueAsync(value);

            ApiFutures.addCallback(task, new ApiFutureCallback<Void>() {
                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println("Failed to write data");
                }

                @Override
                public void onSuccess(Void unused) {
                    System.out.println("Data written successfully");
                }
            });
        }
    }

    private void subscribeOnValueChanged(DatabaseReference reference) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DB error: " + databaseError.toString());
            }
        });
    }

    private void subscribeOnChildrenChanged(DatabaseReference reference) {
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("Child added: " + dataSnapshot.getValue());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("Child changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("Child removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                System.out.println("Child moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DB error: " + databaseError.toString());
            }
        });
    }
}
