package software.plusminus.authorization.service;

import software.plusminus.authorization.model.AuthorizationResult;

public interface Authorizer {
    
    AuthorizationResult authorize();

}
