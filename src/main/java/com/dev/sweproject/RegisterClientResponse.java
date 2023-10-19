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
     * Create instance of the RegisterClientResponse and initializes network_id
     */
    public RegisterClientResponse(String _network_id) {
        this.network_id = _network_id;
    }
}
