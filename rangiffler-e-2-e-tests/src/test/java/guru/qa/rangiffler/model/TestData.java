package guru.qa.rangiffler.model;

import java.util.ArrayList;
import java.util.List;

public record TestData(
    String password,
    List<PhotoJson> photos,
    List<UserJson> friends,
    List<UserJson> outgoingRequests,
    List<UserJson> incomingRequests
) {
  public static TestData emptyTestData() {
    return new TestData(
        null,
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>()
    );
  }

  public TestData withPhotos(List<PhotoJson> photos) {
    return new TestData(
        this.password,
        photos,
        this.friends,
        this.outgoingRequests,
        this.incomingRequests
    );
  }

  public TestData withPassword(String password) {
    return new TestData(
        password,
        this.photos,
        this.friends,
        this.outgoingRequests,
        this.incomingRequests
    );
  }

  public TestData withFriends(List<UserJson> friends) {
    return new TestData(
        this.password,
        this.photos,
        friends,
        this.outgoingRequests,
        this.incomingRequests
    );
  }

  public TestData withOutgoingRequests(List<UserJson> outgoingRequests) {
    return new TestData(
        this.password,
        this.photos,
        this.friends,
        outgoingRequests,
        this.incomingRequests
    );
  }

  public TestData withIncomingRequests(List<UserJson> incomingRequests) {
    return new TestData(
        this.password,
        this.photos,
        this.friends,
        this.outgoingRequests,
        incomingRequests
    );
  }
}