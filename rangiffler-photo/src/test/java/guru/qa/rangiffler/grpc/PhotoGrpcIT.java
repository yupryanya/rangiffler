package guru.qa.rangiffler.grpc;

import guru.qa.rangiffler.*;
import guru.qa.rangiffler.grpc.config.GrpcTestStubsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(GrpcTestStubsConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PhotoGrpcIT {

  private final String defaultPhotoId = "11111111-1111-1111-1111-111111111111";
  private final String defaultUserId = "22222222-2222-2222-2222-222222222222";
  private final String defaultCountry = "es";
  private final String defaultDescription = "Vacation in Spain";
  private final String defaultImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8z8AARwMC/wN2pXwAAAAASUVORK5CYII=";

  @Autowired
  PhotoServiceGrpc.PhotoServiceBlockingStub photoServiceStub;

  @Test
  void shouldAddNewPhoto() {
    PhotoRequest request = PhotoRequest.newBuilder()
        .setUserId(defaultUserId)
        .setCountryCode(defaultCountry)
        .setDescription(defaultDescription)
        .setPhoto(defaultImage)
        .build();

    PhotoResponse updated = photoServiceStub.addPhoto(request);

    assertThat(updated.getId()).isNotBlank();
    assertThat(updated.getUserId()).isEqualTo(defaultUserId);
    assertThat(updated.getCountryCode()).isEqualTo(defaultCountry);
    assertThat(updated.getDescription()).isEqualTo(defaultDescription);
    assertThat(updated.getPhoto()).isEqualTo(defaultImage);
  }

  @Test
  void shouldAddNewPhotoWithEmptyDescription() {
    PhotoRequest request = PhotoRequest.newBuilder()
        .setUserId(defaultUserId)
        .setCountryCode(defaultCountry)
        .setPhoto(defaultImage)
        .build();

    PhotoResponse updated = photoServiceStub.addPhoto(request);

    assertThat(updated.getId()).isNotBlank();
    assertThat(updated.getUserId()).isEqualTo(defaultUserId);
    assertThat(updated.getCountryCode()).isEqualTo(defaultCountry);
    assertThat(updated.getDescription()).isEmpty();
    assertThat(updated.getPhoto()).isEqualTo(defaultImage);
  }

  @Sql(scripts = "/existingPhotosData.sql")
  @Test
  void shouldUpdateExistingPhotoFields() {
    String newCountryCode = "fr";
    String newDescription = "Updated description";
    String newImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAANwAAADcCAYAAAAbWs+BAAAAAXNSR0IArs4c6QAAEyhJREFUeF7tnd3Ld9kYx6/JGG8pg/KaHChCkSLhhIhCkXJCSsmBExwrzpRSKAeknHAuQo7EX0By4ERChtGMGXkZj8bL/b2f3/Wbde97799ee+91Xetaa31/9cxzz3PvvV6++/rs62WtvX/3CD8RFXhSMqh7Tz/rv6W/+09ynP78+Onf0t9FnOOQY7pnyFnXnzSgAUj3nYbyFBHBv6UwlRqlgoe/AeO/RUR/JpSlVM5sh8BlCrXzsDmwFLKdTRY9DcClIKJx/P+dor2wsbMCBK6sMQCwp5+afGbZpl1bU+jgDf/p2nPnnRG44xcY4SC8FkCzCAmPj/B4CwrgY/R+x8QkcNv1Uy+moG1voe0zNAxF2AkPyPBzw/UkcHliKWQth4l5M91+FABE2En4MrQjcMsiKWQ9h4oZJrLpEIXvb5vOGuhgAnf7YiNUfFpS/BjIHIpNlUWXBSkJ3F1hGDIWY+1WQ/R6iSSjA0fQ7ECbtqxeD+HmsAvuowJH0PxAmwMPRRb8GQ68EYFDpZHVxnrAac9DhpojAUfQ6kM2N4KhwBsBOJT1AVuvu0BiYrR9VEOA1zNwKO8/i6Btt/zKZwA8FFa63MPZI3DwZPcnj75Uth92v1MBgPdQb4WV3oBjnrbTuoOe1l2Y2Qtw9GpBiSk0rG68XQ/AoSiCXI2fvhXowtu1DBy9Wt+ALc0OxZRmd6u0ChwqkM8Z094461MhRcFrSpAWgWNhpCkTMx0sPF1TjwK1BBxCyOcOsq6W+/q73NfpmVp95cah1aOtPHneCnC9hpDp6wpgt6WfmlYg09dBpK/nq8xKse6bKai0AFwvIWT6KgJYWs13gaQg4ude3s8SPsSMDhzK/frauWK3Q6eGFDB010KekYLXshcMDV1k4JCvRXpp6hqnaXjYw7NeKYCt3fSgP/K6cJ+IwLW2vtZM/nDA+hQ+gNfKTTDk7pRowLUC2wiQLfHZ0ns5w0EXCThcyOcduAt7nKqv/u7y0ZEdAuKaoagVOeQMBV0U4KKX/fX5rOHewZEJYfR3xIR5xi4CcJFhg0d7pLdnsjIh2nNYZPBCpAG1gYsKW6gwZI/lVz4nai5efVdKTeAiwlb9glQGpXT3EcGreo1rARetQBImxi9t8UHai/Yip2oRTC3gIi1qh96ZEASYEsOIlt9Vga4GcFFgqxpalLDgRtuI9NQHbOBBTx29gYsCG72ap5Xd7iuSt3PdBuYJXISNyPRqdUGb9h7F27ndgL2Ai/CIjeudLJZdhx5NFG/nAp0HcLXL//RqoXk7Dy7C29fMobMGLgJs3b29tw1+do2ydohpfnO2Bq5mkQTbsgAbP20pUHux3LRyaQlczSIJYWsLsrliii6W15iJWb5vBVzNUNJMrBpXfuA+axdTHrZ474wVcC+sZCjmSW+leY3cba0Kt8lOFAvgauVtJnekkS090NxrQVc8WioNXK3SLmELRIfRUGpteMfLiIo94V8SuFqCEDYjCw/YbI3aQNHQsiRwNUJJwhaQCuMh1Yiiii0VlAKuRozNAomxZQduvll7KwFcDTdfPJkNbFwc2rwC3tAV2YVSAjjvUJKL2kQQCtRYpztse0eB8/ZuhydMW+1KgRrbwA7VDY4C5+ndiiWuXZkcJ+O94fmQHR4BzrtahEfh+SJWAjangPeS1O61uSPA4bXk6TdwWprCITduOTC2HUYBTwew28vtBc5zcszbwth0+IF4PqGyy8vtBc7Lu+2+k4Q3DQ7QQgHPfG6Xbe4BznP9g6GkhVn23aZn5XzzevAe4LweveFOkr7BsJydl1PYvM9yK3BeMTLzNktz7L9tz/W5TV5uC3CepVeGkv1DYT1DL3vdtOVrC3Be3o2hpLUpjtO+VzU928ttAc4jd9tV+RnHfjjTjQp4hZbZdpsLnNedgqHkRovi4asKeNluVmSWC5zHuhsLJau2wwN2KuCx5zfLy+UA53WHoHfbaU08bVUBr7W5VRvOAc6jWELvtmozPOCgAh5ebtWOc4DzKJas3hkOis3TqYDHMsFqWLkGnEc4mV1Spc1QgYMKeERrF4sna8B5FEv4nNtBK+Lp2QpU93KXgPNINOndsm2FBxZSwMPLLaZIl4DzGBi9WyErYjPZCnh4uUVHcgk463CS3i3bRnhgYQWsK5aLxZMl4DyKJaxMFrYiNpetgEe6NGvfNYF7IFseHkgFyitg7eVmq5VLwFkPZtf7IMprzhYHVsD6IdXZRfAl4KwXu1ksGdjSg0zdungym8fNAWedv7FYEsTiOAyxjuRu5XFzwFkvBzCcpKVHUcDaudzK4+aAs14OYLEkirlxHO5h5Rxwlvkbw0kaeTQFrMPKG/WKKXDWLpbhZDRz43isbf5GHjcFzjp/Y3WSBh5NAeuw8sbywBQ45m/RzIHj8VDAMqy8sTyQAmdNOvM3D9NhH3sUsAQO4zmHlSlw1rEs87c9psBzPBSw3ltZBTjmbx6mwz72KGAd3Z3X41IPZ1kwYTi5xwx4jqcClmHluXCSAmdZMCFwnqbDvvYoYLmZ2R045m97TIDneCpgmcedK5Wph7PcYcL8zdN02NceBazzuGsGFDjrCiX3T+4xAZ7jqYA1cNeVSg/gVl+O6akq+6ICFxSwLJxcVyoVOFYoaYdUQEyfj3MDLutrfHi1qUAABcwrlerhLJcEWKEMYEkcQpYC5pVKBY4VyqzrwYM6V4DAdX6BOb1YClhXKh/w8HBcErA3qm+LyIeSbu6ICHT/qYh8QUR+ZT+EbnqwjPaugbOkmksC2+wQF/tLIvJiEXmyiPxJRD4pIr9ZaUaB+6qI/ENEni0irxWR15/+/y0i8vNtQxn2aMt6xoMELo5dPVVEfiYiPxKRT5+G9WUReb+IvEZEUHxa+ihwLzhBqsd9XES+LiLfvQLufXGmGnok5sBZJoqrX8EaWnrfwQGOr139eb6I/PnUNTwVtgR9TkQ+vwM43FAB6l9F5CW+02m2N8vF74dxQQhcDNv4gYi8SkReOhnOL7Al6BQebvVwSBcAG0LTl8WYZvhRmANnuY+Sj+Xk29fvROT3IvLmySnfE5F3iAiu0/8WmlsKKT91ygm/cVVEgQflZ10BAreuURdHIPz+sYi8azKbb4nIh0Xk/pk87pUi8hERec/JO6Jo8i+5u0XpjSLyChH5rYi86VS17EIo40lYbnN8FCElPZzxFcxoHtfhvyLyQxF59+T4b15B89Gr4se0IILDUAj5zkz7qFT++ioMRZj6RRH5S8YYeMhdBcyBs9w/xn2U+WaMPO0nIvLOBQ8HQ0A+NvdZCinze+eRqoApD7izmnaARxJ4LbMUQOj3x1MomJ7wfRF5m4g8YyaHw1od/h2l/w+KyMuTCuffReTxrJ55UKqAKQ8MKeMYG4ojrzsteqej+qWIoPj0hpmhLoWUOPStJ48ZZ4ZtjMQ8pGQOF8MQPiYiqCZil8kfTkNC8QMl/c8urMPh968Wkc+IyNtF5ANJvoadJZcWy2PMOt4ozIHjOlyMi47rgJ0myOM+cRoSqo7vPbDTJMbM2hoFgWvreh0a7Yuuyv9fOXk5hPvwblhLy91LOVfJPDSgAU82X4ejhxvQqjjlRQXMgePTArQ+KvCEAuablwkczY0KEDjaABWoooC5h8OsTJ9yrSIbO6UC+xQwZcHjFQs3vuN4nwY8iwq4KGBZQMQEzu80sXSjfE2ei62wkwIKWG4CuX7diHo4y1IoNzAXsAQ24aKA5T7K62dDFTjLjviaBRdbYScFFLDcZeIGHN/cVcAS2ISLAuaplXo4y2SRwLnYCjspoIBlhfLG11VZLn5DB1YqC1gDmzBVwJqBG1/IaL0WR+BMbYWNF1DAskJ5vSSA/6RfOcxKZYGrxiaaVcCyYHIuHKbAWXbIPK5ZOxxm4JYFk/PrIlPgLJcGzi51mMvHibamgGXB5Lz5IwXOslLJwklr5jfWeK3zt1ngrKs0XAAfy4hbmq1lOgUdriuU06IJ/t8yjmUe15IJjjVWS7u/kU6lISV+YVmpZFg5lhG3NFvL/O3G92tMgbPO47iRuSUzHGOsbvnbXEjJPG4MI+Msn1DALX+bA846rGQeR1OPpoBlOHnL3qchJcSwXo/jA6nRTG7c8ViHk7e+H3EOOOs8jssD4xp4tJlbFwlvOZc54KzzOFYro5nduOOxDCdvLAeoxHPAWedxaJ/VynGNPMrM3cPJpaKJRx7H4kkUsxt3HNaL3bO1iiUPx7ByXEMcYebW3g0anrdzpYIuAYdjrO8ALJ6MYNox52hdLJnN3y6FlB5hJYsnMY2x91F5RG+3lgPWiib4fdWB9X7VOb9qCljvLFkMJ9c8nEe1EsUTJJd3qsnPjkdSwMOJXEyVLuVwuBAeyeWi+x3JEjhXFwU8creLO6nWgPO4IzCXc7G14Tux3kGlAs9WJ3NyOD3G467AiuXwPJgL4GHHq9HamoeDCl53Br670tzmhu3Ay4ZXN+bnAIew8n4Ruc/4cnH3ibHAAzfv4d0g7/XLXi99coDzKp6gn9U7xNqE+HsqMFHAo/CXbbu5wHkVT+jlyEtpBax3TOl4V70bDswFDsd6LBiiHz5JUNrkxm3P+mFqVXa1WLKlSqnHeno5LoaPC0mpmXsVSjDei0sB6YS2eDhPL8fQspTZjduOV6Ek27ttDSlxvJeXQ1+bJjKuXXHmMwp4hZKbvNse4HCOV9UHfXFtjjxtVcAzlNxcb9gaUqqXg7uGt7P+MLS0Vri/9r2qklAuqzJ5JIfTcz29HEPL/qCwmpFX3obx71oz3uPh1Mt57D7RC7NrclZXle2GVMAzlNwdee0FzjuXwwQf0q/8CXm5OaiaCngW8w7VFo4Ah449XTihq2nSsfv2tMNDT7YcBc7TjeOS73blse2FozuggCdsh7wbTj4KnLeXQ38sohywzs5O9YbtkHcrBZx3/Ixxb17/6MzQOB2/vb2p1ofXhUt4OO8CigpA6MbFznMnSVF7KwWc10OqUxMjdONB5103gMKHQ0m9TKWAQ3s1Qku+Zm8s4GrABoWznwZYuxwlgasVWgI6eDoUU/jpV4FasB3O29JLUho4tO31oGo6D0AH4AAeP/0pUAu2YqGkRUipbSK09NrczJyuP7imM6pRIMEYTNZ8LTwcBlvrjoS+WUjpB8Ia0ZKqVzSUtPRw2natOxP65+J4+9B5L2qnipnZj5WHw+BrhpYaEnDDc3vg1VpiUqVMQkkPD6fQ4YHAWh9ueK6l/L5+a8OGUZuEkl7AoR/Ph1XnLjMrmPuM3/usmnm/ad6WCmkZUqb91MzndBwspngjlNcfvBrsAzfmmh8X+/ACDkJGgI47U2qa9O2+4dVQifR4P86lmZsVSaadegIHUXEXA3i1Py53s9qTDN5/hBswJCq+uH1Jd0/gMI4ISbHqwYJKHSIj2YArbJDbGziFrtZOlKmJsaDiC10Ur4ZZm5b/l2StAVw06FR87sW0gy9KrlY9uqkFnEJXc41uzrxYVCkLXe3ND0uzMV1ri5TDTccSYe2F4JWFLFquPp1dNdhq5XBTAWovjF8yNw0z4fn4WVcgyppaOM+mA6oZUqai1HhafN187h4B2O6cnkIgePOqRVryCQtbFA+nAkWN99MLCI/32AnAXGB7Pi66R9MbZpgv+Izi4VqCTi8i4MOf0byeejOkArV3iKzdzMKttUYDLnrCPXeBsXiq8K0ZQKu/V8hQ5LqvkUm4L2rn6BIROIUuyjawHB3TXA8XWvO+3HMjHtciZKpjSNii5XBTo2shEb8EikKnHjAiVNPCFTyY5mXRx7s0PreNyHsEiurh0rlE2g60R2M9JwUwggcEWPeeQsSWQsVL1yD8pvQWgIPAvUCXGosWW7DkgI9FKKpFDfVc+nf0YseeG1vVBe3cAbcCnOZ1UTY95+q797i08qk/P540lv5e4YG3Up3Sv/eOoZXzmtqO1xJwakStFVNaMdwWxxk+hJyK2hpwOv5ou89bNNaWx9yUV0uFbhU4eruWcTk29rAl/5xptQyczq/HgkrOtRvxmOZCyF5Cyuk8Ij22PyII1nOGV3ukh210PXg4vditL5RbG22r7Tfv1XrJ4ZYMiN6uVbRujrvZwsgl+XvycNN5opKJ/K6VzbZ9YHJ8Fl2CprL0DJzOUdftetxdcdy847QwxBvURgAuXUJo4RmuOAj4jGQI0EbycKnZsLDiA1FOL0OBNipwrGjmoGB/TFeVxy1yjRJSXqpocm/mFos5dixAG/G1FGfVRgdu6vGY4x0Dau7sIUPHJRkJ3E1lkOPpw5i1v6+svOn7tjjCu142K0rgliVT+AAe1/LyTIvebEUnApdnSC29Gi5vRuWOUsiGzs1y5SRwuUo9cRw93923kgEwhI36iojtSg54BoE7dtF7fBHPpcIHATtmL1W+kPHgkEOf3kvRRT0YxEYpn59CCtDDFRJyoRkFEL9G4UVfS2fba37r6ZvD8DM8GF5WNNrr2/MVO3gkgTso4M7TNRTF3woifrbaYA2A0ndhMjTceeGOnkbgjipoc34K3vT1d+gx/b16o+nr9OilbK7NoVb/D8+E54iS1LOxAAAAAElFTkSuQmCC";

    UpdatePhotoRequest updateRequest = UpdatePhotoRequest.newBuilder()
        .setId(defaultPhotoId)
        .setCountryCode(newCountryCode)
        .setDescription(newDescription)
        .setPhoto(newImage)
        .build();

    PhotoResponse updated = photoServiceStub.updatePhoto(updateRequest);

    assertThat(updated.getId()).isEqualTo(defaultPhotoId);
    assertThat(updated.getUserId()).isEqualTo(defaultUserId);
    assertThat(updated.getCountryCode()).isEqualTo(newCountryCode);
    assertThat(updated.getDescription()).isEqualTo(newDescription);
    assertThat(updated.getPhoto()).isEqualTo(newImage);
  }

  @Sql(scripts = "/existingPhotosData.sql")
  @Test
  void shouldUpdateExistingPhotoImage() {
    String newImage = "data:image/png;base64,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";

    UpdatePhotoRequest updateRequest = UpdatePhotoRequest.newBuilder()
        .setId(defaultPhotoId)
        .setPhoto(newImage)
        .build();

    PhotoResponse updated = photoServiceStub.updatePhoto(updateRequest);

    assertThat(updated.getId()).isEqualTo(defaultPhotoId);
    assertThat(updated.getUserId()).isEqualTo(defaultUserId);
    assertThat(updated.getCountryCode()).isEqualTo(defaultCountry);
    assertThat(updated.getDescription()).isEqualTo(defaultDescription);
    assertThat(updated.getPhoto()).isEqualTo(newImage);
  }

  @Sql(scripts = "/existingPhotosData.sql")
  @Test
  void shouldUpdateExistingPhotoCountry() {
    String newCountryCode = "fr";

    UpdatePhotoRequest updateRequest = UpdatePhotoRequest.newBuilder()
        .setId(defaultPhotoId)
        .setCountryCode(newCountryCode)
        .build();

    PhotoResponse updated = photoServiceStub.updatePhoto(updateRequest);

    assertThat(updated.getId()).isEqualTo(defaultPhotoId);
    assertThat(updated.getUserId()).isEqualTo(defaultUserId);
    assertThat(updated.getCountryCode()).isEqualTo(newCountryCode);
    assertThat(updated.getDescription()).isEqualTo(defaultDescription);
    assertThat(updated.getPhoto()).isEqualTo(defaultImage);
  }

  @Sql(scripts = "/existingPhotosData.sql")
  @Test
  void shouldUpdateExistingPhotoDescription() {
    String newDescription = "Updated description";

    UpdatePhotoRequest updateRequest = UpdatePhotoRequest.newBuilder()
        .setId(defaultPhotoId)
        .setDescription(newDescription)
        .build();

    PhotoResponse updated = photoServiceStub.updatePhoto(updateRequest);

    assertThat(updated.getId()).isEqualTo(defaultPhotoId);
    assertThat(updated.getUserId()).isEqualTo(defaultUserId);
    assertThat(updated.getCountryCode()).isEqualTo(defaultCountry);
    assertThat(updated.getDescription()).isEqualTo(newDescription);
    assertThat(updated.getPhoto()).isEqualTo(defaultImage);
  }

  @Sql(scripts = "/existingPhotosData.sql")
  @Test
  void shouldNotChangeAnyFieldsWhenUpdatingExistingPhoto() {
    UpdatePhotoRequest updateRequest = UpdatePhotoRequest.newBuilder()
        .setId(defaultPhotoId)
        .build();

    PhotoResponse updated = photoServiceStub.updatePhoto(updateRequest);

    assertThat(updated.getId()).isEqualTo(defaultPhotoId);
    assertThat(updated.getUserId()).isEqualTo(defaultUserId);
    assertThat(updated.getCountryCode()).isEqualTo(defaultCountry);
    assertThat(updated.getDescription()).isEqualTo(defaultDescription);
    assertThat(updated.getPhoto()).isEqualTo(defaultImage);
  }

  @Sql(scripts = "/existingPhotosData.sql")
  @Test
  void shouldRemoveExistingPhotoDescription() {
    UpdatePhotoRequest updateRequest = UpdatePhotoRequest.newBuilder()
        .setId(defaultPhotoId)
        .setDescription("")
        .build();

    PhotoResponse updated = photoServiceStub.updatePhoto(updateRequest);

    assertThat(updated.getId()).isEqualTo(defaultPhotoId);
    assertThat(updated.getUserId()).isEqualTo(defaultUserId);
    assertThat(updated.getCountryCode()).isEqualTo(defaultCountry);
    assertThat(updated.getPhoto()).isEqualTo(defaultImage);

    assertThat(updated.getDescription()).isEqualTo("");
  }

  @Sql(scripts = "/existingPhotosData.sql")
  @Test
  void shouldDeletePhoto() {
    DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
        .setId(defaultPhotoId)
        .build();

    DeletePhotoResponse response = photoServiceStub.deletePhoto(request);
    assertThat(response.getSuccess()).isTrue();
  }

  @Test
  void shouldFailToAddPhotoWithEmptyUserId() {
    PhotoRequest request = PhotoRequest.newBuilder()
        .setUserId("")
        .setCountryCode(defaultCountry)
        .setPhoto(defaultImage)
        .build();

    assertThatThrownBy(() -> photoServiceStub.addPhoto(request))
        .isInstanceOf(io.grpc.StatusRuntimeException.class)
        .hasMessageContaining("INVALID_ARGUMENT");
  }

  @Test
  void shouldFailToAddPhotoWithEmptyPhoto() {
    PhotoRequest request = PhotoRequest.newBuilder()
        .setUserId(defaultUserId)
        .setCountryCode(defaultCountry)
        .setPhoto("")
        .build();

    assertThatThrownBy(() -> photoServiceStub.addPhoto(request))
        .isInstanceOf(io.grpc.StatusRuntimeException.class)
        .hasMessageContaining("INVALID_ARGUMENT");
  }

  @Sql(scripts = "/existingPhotosData.sql")
  @Test
  void shouldFailToUpdatePhotoWhenIdDoesNotExist() {
    UpdatePhotoRequest updateRequest = UpdatePhotoRequest.newBuilder()
        .setId("44444444-1111-1111-1111-111111111111")
        .setDescription("New description")
        .build();

    assertThatThrownBy(() -> photoServiceStub.updatePhoto(updateRequest))
        .isInstanceOf(io.grpc.StatusRuntimeException.class)
        .hasMessageContaining("NOT_FOUND");
  }
}
