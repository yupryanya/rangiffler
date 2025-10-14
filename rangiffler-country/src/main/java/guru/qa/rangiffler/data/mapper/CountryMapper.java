package guru.qa.rangiffler.data.mapper;


import guru.qa.rangiffler.CountryResponse;
import guru.qa.rangiffler.data.CountryEntity;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CountryMapper {
  public CountryResponse toGrpcResponse(CountryEntity entity) {
    return CountryResponse.newBuilder()
        .setId(entity.getId().toString())
        .setName(entity.getName())
        .setCode(entity.getCode())
        .setFlag(new String(entity.getFlag(), StandardCharsets.UTF_8))
        .build();
  }

  public List<CountryResponse> toGrpcResponseList(List<CountryEntity> entities) {
    return entities.stream()
        .map(this::toGrpcResponse)
        .toList();
  }
}