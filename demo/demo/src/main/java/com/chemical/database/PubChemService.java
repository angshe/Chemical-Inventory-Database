package com.chemical.database;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class PubChemService {

    private final HttpClient client = HttpClient.newHttpClient();
    
    public String getSdfByCid(String cid) throws Exception {
        URI uri = URI.create(
            "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/"
            + cid + "/SDF"
        );

        return fetch(uri);
    }

    public String getSdfByName(String name) throws Exception {
        // Encode the chemical name to handle spaces and special characters
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);

        URI uri = URI.create(
            "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/"
            + encodedName
            + "/SDF"
        );

    return fetch(uri);
}

    private String fetch(URI uri) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("PubChem request failed");
        }

        return response.body();
    }
}