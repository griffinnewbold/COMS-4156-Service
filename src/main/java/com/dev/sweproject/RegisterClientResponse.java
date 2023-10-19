package com.dev.sweproject;

/**
 * Registers a client's response.
 */
public class RegisterClientResponse {

  /**
   * Contains network id.
   */
  public String networkId;

  /**
   * Constructs a RegisterClientResponse object and initializes the network identifier.
   *
   * @param networkId The network identifier associated with the client registration.
   */
  public RegisterClientResponse(String networkId) {
    this.networkId = networkId;
  }
}
