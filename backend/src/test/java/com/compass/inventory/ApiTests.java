package com.compass.inventory;

import com.compass.inventory.entity.Vehicle;
import com.compass.inventory.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void loginAndRefresh() throws Exception {
        String loginJson = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andReturn().getResponse().getContentAsString();

        String refreshToken = loginResponse.split("\"refreshToken\":\"")[1].split("\"")[0];
        String refreshJson = "{\"refreshToken\":\"" + refreshToken + "\"}";

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void staffCannotPost() throws Exception {
        String loginJson = "{\"username\":\"staff\",\"password\":\"staff123\"}";
        String token = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andReturn().getResponse().getContentAsString()
            .split("\"accessToken\":\"")[1].split("\"")[0];

        mockMvc.perform(post("/api/packages")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"TestPkg\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void orderingWorks() throws Exception {
        Vehicle v1 = new Vehicle();
        v1.setId(UUID.randomUUID());
        v1.setVin("TESTVIN1");
        v1.setManufacture("Ford");
        v1.setModel("Focus");
        v1.setYear(2018);
        v1.setColor("Blue");
        v1.setBodyType("Sedan");
        v1.setMileage(10000);
        v1.setConditionGrade("A");
        v1.setTitleStatus("Clean");
        v1.setStatus("IN_STOCK");
        v1.setLocation("Lot");
        v1.setPurchasePrice(BigDecimal.valueOf(10000));
        v1.setListPrice(BigDecimal.valueOf(12000));
        vehicleRepository.save(v1);

        Vehicle v2 = new Vehicle();
        v2.setId(UUID.randomUUID());
        v2.setVin("TESTVIN2");
        v2.setManufacture("Ford");
        v2.setModel("Fusion");
        v2.setYear(2021);
        v2.setColor("Red");
        v2.setBodyType("Sedan");
        v2.setMileage(5000);
        v2.setConditionGrade("A");
        v2.setTitleStatus("Clean");
        v2.setStatus("IN_STOCK");
        v2.setLocation("Lot");
        v2.setPurchasePrice(BigDecimal.valueOf(15000));
        v2.setListPrice(BigDecimal.valueOf(19000));
        vehicleRepository.save(v2);

        String loginJson = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        String token = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andReturn().getResponse().getContentAsString()
            .split("\"accessToken\":\"")[1].split("\"")[0];

        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .param("sort", "year,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].year").value(2021));
    }
}
