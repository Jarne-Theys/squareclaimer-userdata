package com.square_claimer.user_data.service;

/**
 * Exceptions throws by the Service should be using this and usually points to the service dying for no reason.
 *
 */
public class ServiceException extends RuntimeException {

    private String action;

    /**
     *
     * @param message Error code; example 'club.list.empty'. We're using this to translate stuff
     * @param action General category of the error, defaults to DEFAULT. Useful for maps
     */
    public ServiceException(String message, String action) {
        super(message);
        this.action = action;
    }

    public ServiceException(String message){
        super(message);
        this.action = "DEFAULT"; //if you see this, it's probably because it's a legacy thing
    }

    public ServiceException(Exception e){
        super(e);
        this.action = "DEFAULT";
    }

    public ServiceException(Exception e, String message){
        super(message, e);
        this.action = "DEFAULT";
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
