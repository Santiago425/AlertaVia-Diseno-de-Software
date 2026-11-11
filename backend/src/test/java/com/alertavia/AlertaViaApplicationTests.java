package com.alertavia;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AlertaViaApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void citizenReportsAndAdminTracksTheProblem() throws Exception {
        String citizenToken = token(mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Laura Gómez","email":"laura@correo.com","password":"clave123","phone":"3105550000"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("CITIZEN"))
                .andReturn().getResponse().getContentAsString());

        MockMultipartFile report = new MockMultipartFile("report", "", MediaType.APPLICATION_JSON_VALUE, """
                {"description":"Semáforo apagado en la intersección","type":"BROKEN_TRAFFIC_LIGHT",
                 "latitude":1.2136,"longitude":-77.2811}
                """.getBytes());
        MockMultipartFile photo = new MockMultipartFile("photos", "semaforo.jpg", "image/jpeg", new byte[]{1, 2, 3});

        String created = mockMvc.perform(multipart("/api/reports").file(report).file(photo)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + citizenToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REPORTED"))
                .andExpect(jsonPath("$.photos", hasSize(1)))
                .andExpect(jsonPath("$.address").value("1.213600, -77.281100"))
                .andReturn().getResponse().getContentAsString();
        long reportId = objectMapper.readTree(created).get("id").asLong();
        String photoUrl = objectMapper.readTree(created).get("photos").get(0).asText();

        mockMvc.perform(get(photoUrl.replace("http://localhost:8080", "")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/reports").header(HttpHeaders.AUTHORIZATION, "Bearer " + citizenToken))
                .andExpect(status().isForbidden());

        String adminToken = token(mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@alertavia.com","password":"admin123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn().getResponse().getContentAsString());

        mockMvc.perform(put("/api/admin/reports/" + reportId + "/status")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"IN_REVIEW","comment":"Reporte verificado"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_REVIEW"))
                .andExpect(jsonPath("$.history", hasSize(2)));

        String entities = mockMvc.perform(get("/api/admin/entities")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long entityId = objectMapper.readTree(entities).get(0).get("id").asLong();

        mockMvc.perform(put("/api/admin/reports/" + reportId + "/entity/" + entityId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobilityEntity").isNotEmpty());

        mockMvc.perform(put("/api/admin/reports/" + reportId + "/status")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"RESOLVED"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/admin/statistics").header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));

        String notifications = mockMvc.perform(get("/api/notifications")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + citizenToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn().getResponse().getContentAsString();
        long notificationId = objectMapper.readTree(notifications).get(0).get("id").asLong();

        mockMvc.perform(patch("/api/notifications/" + notificationId + "/read")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + citizenToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));

        mockMvc.perform(get("/api/reports/mine").header(HttpHeaders.AUTHORIZATION, "Bearer " + citizenToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void rejectsWrongCredentialsAndMissingToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@alertavia.com","password":"incorrecta"}
                                """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/reports/mine")).andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ciudadano@alertavia.com","password":"ciudadano123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("CITIZEN"));
    }

    private String token(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        return node.get("token").asText();
    }
}
