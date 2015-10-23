package com.vmware.vchs.constant;

/**
 * Created by georgeliu on 14/11/3.
 */
public enum Error {

    INSTANCE_NOT_FOUND("Request resource can't be found"),
    RESOURCE_UNAVAILABLE("Resource is unavailable temporarily"),
    INVALID_PROPERTIES("Invalid properties"),
    INVALID_JSON("Convert the JsonString failed."),
    INCORRECT_PITR_TIME("PITR time is not right"),
    ALREADY_EXISTS("AlreadyExists"),
    ALREADY_IN_DESIRED_STATE("AlreadyInDesiredState"),
    FEATURE_IN_USE("FeatureInUse"),
    INTERNAL_SERVER_ERROR("InternalServerError"),
    INVALID_ARGUMENT("InvalidArgument"),
    INVALID_CONFIGURATION_CHANGE("InvalidConfigurationChange"),
    INVALID_ELEMENT_CONFIGURATION("InvalidElementConfiguration"),
    INVALID_ELEMENT_TYPE("InvalidElementType"),
    INVALID_REQUEST("Invalid Request"),
    INVALID_OPERATION("Operation is invalid"),
    RESOURCE_NOT_FOUND("ResourceNotFound"),
    OPERATION_NOT_ALLOWED("OperationNotAllowed"),
    RESOURCE_BUSY("ResourceBusy"),
    RESOURCE_IN_USE("ResourceInUse"),
    RESOURCE_INACCESSIBLE("ResourceInaccessible"),
    SERVICE_NOT_FOUND("ServiceNotFound"),
    SERVICE_UNAVAILABLE("ServiceUnavailable"),
    TIMED_OUT("TimedOut"),
    UNABLE_TO_ALLOCATE_RESOURCE("UnableToAllocateResource"),
    UNAUTHENTICATED("Unauthenticated"),
    UNAUTHORIZED("Unauthorized"),
    UNIMPLEMENTED("Unimplemented"),
    UNSUPPORTED("Unsupported"),
    NOT_SUPPORTED("is not supported"),
    //Portal related error
    INSTANCE_NOT_EXIST("Can't find instance"),
    INSTANCE_NOT_DELETE("Can't delete the instance"),
    SNAPSHOT_NOT_EXIST("can not be found"),
    REQUEST_RESOURCE_NOT_FOUND("Request resource can't be found");

    private String message;

    Error(String message) {
        this.message = message;
    }

    public String value() {
        return this.message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
