package com.example.prospera.Resources;

import com.example.prospera.DTO.ConnectionCodeRecord;
import com.example.prospera.DTO.ConnectionRequestRecord;
import com.example.prospera.DTO.UserConnectionRecord;
import com.example.prospera.Services.ConnectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/connections")
public class ConnectionResource {
    private final ConnectionService service;

    public ConnectionResource(ConnectionService service) {
        this.service = service;
    }

    @GetMapping("/code")
    public ResponseEntity<ConnectionCodeRecord> getCode() {
        return ResponseEntity.ok(service.getAuthenticatedUserCode());
    }

    @PostMapping("/requests")
    public ResponseEntity<UserConnectionRecord> createRequest(@RequestBody ConnectionRequestRecord record) {
        UserConnectionRecord connection = service.createRequest(record);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(connection.id())
                .toUri();
        return ResponseEntity.created(uri).body(connection);
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<List<UserConnectionRecord>> findPendingRequests() {
        return ResponseEntity.ok(service.findPendingRequests());
    }

    @PostMapping("/requests/{id}/accept")
    public ResponseEntity<UserConnectionRecord> accept(@PathVariable Integer id) {
        return ResponseEntity.ok(service.accept(id));
    }

    @PostMapping("/requests/{id}/decline")
    public ResponseEntity<UserConnectionRecord> decline(@PathVariable Integer id) {
        return ResponseEntity.ok(service.decline(id));
    }

    @GetMapping
    public ResponseEntity<List<UserConnectionRecord>> findAcceptedConnections() {
        return ResponseEntity.ok(service.findAcceptedConnections());
    }
}
