package com.example.demo;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SentimentService {

    private static final Set<String> POSITIVE = Set.of(
            "good","great","excellent","awesome","amazing","love","like","happy","wonderful",
            "fantastic","nice","best","positive","success","beautiful","perfect","joy","glad",
            "hello","thanks","thank");

    private static final Set<String> NEGATIVE = Set.of(
            "bad","terrible","awful","hate","horrible","worst","sad","angry","poor","negative",
            "fail","failure","ugly","broken","slow","bug","error","disappointing","pain");

    public Map<String, Object> analyze(String text) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("text", text == null ? "" : text);
        if (text == null || text.isBlank()) {
            result.put("sentiment", "neutral");
            result.put("score", 0.0);
            return result;
        }
        String[] tokens = text.toLowerCase().replaceAll("[^a-z\\s]", " ").split("\\s+");
        int pos = 0, neg = 0;

        for (String t : tokens) {
            if (POSITIVE.contains(t)) pos++;
            if (NEGATIVE.contains(t)) neg++;
        }

        int total = pos + neg;
        double score = total == 0 ? 0.0 : (double) (pos - neg) / total;   // -1 .. +1
        String label = score > 0.1 ? "positive" : score < -0.1 ? "negative" : "neutral";
        result.put("sentiment", label);
        result.put("score", Math.round(score * 1000.0) / 1000.0);
        result.put("positiveHits", pos);
        result.put("negativeHits", neg);
        return result;
    }
}