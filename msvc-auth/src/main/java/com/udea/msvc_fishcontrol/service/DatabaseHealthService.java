package com.udea.msvc_fishcontrol.service;

import com.udea.msvc_fishcontrol.response.DatabaseStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DatabaseHealthService {

    @Autowired
    private DataSource dataSource;

    /**
     * Verifica el estado de la conexión a la base de datos
     * @return Respuesta con el estado de la conexión
     */
    public DatabaseStatusResponse checkDatabaseConnections() {
        Map<String, DatabaseStatusResponse.DatabaseConnectionStatus> connections = new HashMap<>();
        boolean allOk = true;

        // Verificar conexión a la base de datos
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(5)) { // Timeout de 5 segundos
                connections.put("main", DatabaseStatusResponse.DatabaseConnectionStatus.builder()
                        .connected(true)
                        .message("Conexión exitosa a la base de datos")
                        .build());
            } else {
                allOk = false;
                connections.put("main", DatabaseStatusResponse.DatabaseConnectionStatus.builder()
                        .connected(false)
                        .message("La conexión a la base de datos no es válida")
                        .build());
            }
        } catch (SQLException e) {
            allOk = false;
            log.error("Error al conectar a la base de datos: {}", e.getMessage());
            connections.put("main", DatabaseStatusResponse.DatabaseConnectionStatus.builder()
                    .connected(false)
                    .message("Error al conectar a la base de datos")
                    .details(e.getMessage())
                    .build());
        }

        return DatabaseStatusResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .allConnectionsOk(allOk)
                .connections(connections)
                .build();
    }
}