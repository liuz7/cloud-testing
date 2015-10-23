package com.vmware.vchs.constant;

/**
 * Created by georgeliu on 14/11/14.
 */
public class PortalErrorMap {

    public interface PortalMessage {
        public static final String ALREADY_EXISTS = "AlreadyExists";
        public static final String ALREADY_IN_DESIRED_STATE = "AlreadyInDesiredState";
        public static final String FEATURE_IN_USE = "FeatureInUse";
        public static final String INTERNAL_SERVER_ERROR = "InternalServerError";
        public static final String INVALID_ARGUMENT = "InvalidArgument";
        public static final String INVALID_CONFIGURATION_CHANGE = "InvalidConfigurationChange";
        public static final String INVALID_ELEMENT_CONFIGURATION = "InvalidElementConfiguration";
        public static final String INVALID_ELEMENT_TYPE = "InvalidElementType";
        public static final String INVALID_REQUEST = "InvalidRequest";
        public static final String RESOURCE_NOT_FOUND = "ResourceNotFound";
        public static final String OPERATION_NOT_ALLOWED = "OperationNotAllowed";
        public static final String RESOURCE_BUSY = "ResourceBusy";
        public static final String RESOURCE_IN_USE = "ResourceInUse";
        public static final String RESOURCE_INACCESSIBLE = "ResourceInaccessible";
        public static final String SERVICE_NOT_FOUND = "ServiceNotFound";
        public static final String SERVICE_UNAVAILABLE = "ServiceUnavailable";
        public static final String TIMED_OUT = "TimedOut";
        public static final String UNABLE_TO_ALLOCATE_RESOURCE = "UnableToAllocateResource";
        public static final String UNAUTHENTICATED = "Unauthenticated";
        public static final String UNAUTHORIZED = "Unauthorized";
        public static final String UNIMPLEMENTED = "Unimplemented";
        public static final String UNSUPPORTED = "Unsupported";
        public static final String CONFLICT = "Conflict";
        public static final String CROSS_COMPONENT_ERROR = "CrossComponentError";
        public static final String UNSUPPORTEDRESPFORMAT = "UnsupportedResponseFormat";
    }

    public enum PortalStatus {

        /**
         *
         */
        INVALID_REQUEST(400, "PS100", PortalMessage.INVALID_REQUEST),
        /**
         *
         */
        INVALID_ARGUMENT(400, "400", PortalMessage.INVALID_ARGUMENT),
        /**
         *
         */
        INVALID_CONFIGURATION_CHANGE(400, "PS102", PortalMessage.INVALID_CONFIGURATION_CHANGE),
        /**
         *
         */
        INVALID_ELEMENT_CONFIGURATION(400, "PS103", PortalMessage.INVALID_ELEMENT_CONFIGURATION),
        /**
         *
         */
        INVALID_ELEMENT_TYPE(400, "PS104", PortalMessage.INVALID_ELEMENT_TYPE),
        /**
         *
         */
        OPERATION_NOT_ALLOWED(400, "PS400", PortalMessage.OPERATION_NOT_ALLOWED),
        /**
         *
         */
        UNAUTHENTICATED(401, "PS401", PortalMessage.UNAUTHENTICATED),
        /**
         *
         */
        RESOURCE_INACCESSIBLE(400, "PS402", PortalMessage.RESOURCE_INACCESSIBLE),
        /**
         *
         */
        UNAUTHORIZED(403, "PS403", PortalMessage.UNAUTHORIZED),
        /**
         *
         */
        RESOURCE_NOT_FOUND(404, "PS404", PortalMessage.RESOURCE_NOT_FOUND),
        /**
         *
         */
        SERVICE_NOT_FOUND(404, "PS405", PortalMessage.SERVICE_NOT_FOUND),
        /**
         *
         */
        RESOURCE_BUSY(400, "400", PortalMessage.RESOURCE_BUSY),
        /**
         *
         */
        ALREADY_EXISTS(400, "PS407", PortalMessage.ALREADY_EXISTS),
        /**
         *
         */
        ALREADY_IN_DESIRED_STATE(400, "PS408", PortalMessage.ALREADY_IN_DESIRED_STATE),
        /**
         *
         */
        CONFLICT(409, "PS409", PortalMessage.CONFLICT),
        /**
         *
         */
        FEATURE_IN_USE(400, "PS410", PortalMessage.FEATURE_IN_USE),
        /**
         *
         */
        UNSUPPORTED(400, "PS411", PortalMessage.UNSUPPORTED),
        /**
         *
         */
        RESOURCE_IN_USE(400, "PS412", PortalMessage.RESOURCE_IN_USE),
        /**
         *
         */
        INTERNAL_SERVER_ERROR(500, "PS500", PortalMessage.INTERNAL_SERVER_ERROR),
        /**
         *
         */
        UNIMPLEMENTED(501, "PS501", PortalMessage.UNIMPLEMENTED),
        /**
         *
         */
        UNABLE_TO_ALLOCATE_RESOURCE(500, "500", PortalMessage.UNABLE_TO_ALLOCATE_RESOURCE),
        /**
         *
         */
        SERVICE_UNAVAILABLE(503, "PS503", PortalMessage.SERVICE_UNAVAILABLE),
        /**
         *
         */
        TIMED_OUT(504, "PS504", PortalMessage.TIMED_OUT),
        /**
         *
         */
        CROSS_COMPONENT_ERROR(500, "PS600", PortalMessage.CROSS_COMPONENT_ERROR),
        /**
         *
         */
        UNSUPPORTEDRESPFORMAT(500, "PS601", PortalMessage.UNSUPPORTEDRESPFORMAT);

        private final int httpStatus;
        private final String code;
        private final String title;

        PortalStatus(int httpStatus, String code, String title) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.title = title;
        }

        public String getCode() {
            return code;
        }

        public int getHttpStatus() {
            return httpStatus;
        }

        public String getTitle() {
            return title;
        }

    }


}
