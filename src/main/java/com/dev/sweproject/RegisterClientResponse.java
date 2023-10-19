package com.dev.sweproject;

/**
 * Registers a client's response.
 */
public class RegisterClientResponse {

    /**
     * Contains network id.
     */
    public String network_id;

    /**
     * Constructs a RegisterClientResponse object and initializes the network identifier.
     *
     * @param _network_id The network identifier associated with the client registration.
     */
    public RegisterClientResponse(String _network_id) {
        this.network_id = _network_id;
    }
}
