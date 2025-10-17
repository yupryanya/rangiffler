package guru.qa.rangiffler.jupiter.extension;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.api.core.RestClient;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;

public class AllureDockerServiceExtension extends RestClient implements SuiteExtension {

  private AllureDockerService service;

  private static final String API = Optional.ofNullable(System.getenv("ALLURE_DOCKER_API"))
      .orElse("http://allure:5050/");
  private static final String PROJECT = "default";
  private static final String RESULTS_PATH = "rangiffler-e-2-e-tests/build/allure-results";

  public AllureDockerServiceExtension() {
    super(API);
    this.service = retrofit.create(AllureDockerService.class);
  }

  @Override
  public void afterSuite() {
    if ("docker".equals(System.getProperty("test.env"))) {
      encodeResults().ifPresentOrElse(
          this::sendAndGenerate,
          () -> System.err.println("Log file not found: " + RESULTS_PATH)
      );
    }
  }

  @SneakyThrows
  private Optional<ResultsRequest> encodeResults() {
    File resultsDir = new File(RESULTS_PATH).getAbsoluteFile();
    if (!resultsDir.exists() || !resultsDir.isDirectory()) return Optional.empty();

    List<Result> results = new ArrayList<>();
    for (File file : Objects.requireNonNull(resultsDir.listFiles())) {
      if (file.isFile() && file.length() > 0) {
        byte[] content = Files.readAllBytes(file.toPath());
        String base64 = Base64.getEncoder().encodeToString(content);
        results.add(new Result(file.getName(), base64));
      }
    }

    if (results.isEmpty()) return Optional.empty();
    return Optional.of(new ResultsRequest(results));
  }

  @SneakyThrows
  private void sendAndGenerate(ResultsRequest request) {
    Call<ResponseBody> sendCall = service.sendResults(PROJECT, true, request);
    execute(sendCall, SC_OK);

    Call<ResponseBody> genCall = service.generateReport(PROJECT);
    execute(genCall, SC_OK);
  }

  interface AllureDockerService {
    @POST("allure-docker-service/send-results")
    Call<ResponseBody> sendResults(@Query("project_id") String projectId,
                                   @Query("force_project_creation") boolean forceProjectCreation,
                                   @Body ResultsRequest request);

    @GET("allure-docker-service/generate-report")
    Call<ResponseBody> generateReport(@Query("project_id") String projectId);
  }

  @AllArgsConstructor
  public static class ResultsRequest {
    @JsonProperty("results")
    public final List<Result> results;
  }

  @AllArgsConstructor
  public static class Result {
    @JsonProperty("file_name")
    public String fileName;

    @JsonProperty("content_base64")
    public String contentBase64;
  }
}