package guru.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KafkaUser(
    @JsonProperty("username")
    String username
) {
}