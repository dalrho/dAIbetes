package org.example.daibetes.modules.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import org.example.daibetes.modules.ai.dto.AIResponseDTO;

import java.io.File;

public class AIInferenceService {

    private static final String API_URL =
            "http://127.0.0.1:8000/predict/dr";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIResponseDTO analyzeImage(File imageFile) throws Exception {

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpPost post = new HttpPost(API_URL);

            post.setEntity(
                    MultipartEntityBuilder.create()
                            .addBinaryBody(
                                    "file",
                                    imageFile,
                                    ContentType.DEFAULT_BINARY,
                                    imageFile.getName()
                            )
                            .build()
            );

            String responseJson = client.execute(post, response ->
                    EntityUtils.toString(response.getEntity())
            );

            return objectMapper.readValue(
                    responseJson,
                    AIResponseDTO.class
            );
        }
    }
}