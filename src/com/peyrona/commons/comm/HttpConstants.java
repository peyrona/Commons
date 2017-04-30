package com.peyrona.commons.comm;

public class HttpConstants
{
    public static enum Method { GET,      // requests a representation of the specified resource
                                POST,     // used to submit an entity to the specified resource
                                HEAD,     // asks for a response identical to that of a GET request, but without the response body
                                PUT,      // replaces all current representations of the target resource with the request payload
                                OPTIONS,  // used to describe the communication options for the target resource
                                DELETE,   // deletes the specified resource
                                CONNECT,  // establishes a tunnel to the server identified by the target resource
                                TRACE,    // performs a message loop-back test along the path to the target resource
                                PATCH };  // used to apply partial modifications to a resource

    // These are copy-pasted from the Jetty codebase
    public final static int
        _100_Continue = 100,
        _101_Switching_Protocols = 101,
        _102_Processing = 102,
        _200_OK = 200,
        _201_Created = 201,
        _202_Accepted = 202,
        _203_Non_Authoritative_Information = 203,
        _204_No_Content = 204,
        _205_Reset_Content = 205,
        _206_Partial_Content = 206,
        _207_Multi_Status = 207,
        _300_Multiple_Choices = 300,
        _301_Moved_Permanently = 301,
        _302_Moved_Temporarily = 302,
        _302_Found = 302,
        _303_See_Other = 303,
        _304_Not_Modified = 304,
        _305_Use_Proxy = 305,
        _400_Bad_Request = 400,
        _401_Unauthorized = 401,
        _402_Payment_Required = 402,
        _403_Forbidden = 403,
        _404_Not_Found = 404,
        _405_Method_Not_Allowed = 405,
        _406_Not_Acceptable = 406,
        _407_Proxy_Authentication_Required = 407,
        _408_Request_Timeout = 408,
        _409_Conflict = 409,
        _410_Gone = 410,
        _411_Length_Required = 411,
        _412_Precondition_Failed = 412,
        _413_Request_Entity_Too_Large = 413,
        _414_Request_URI_Too_Large = 414,
        _415_Unsupported_Media_Type = 415,
        _416_Requested_Range_Not_Satisfiable = 416,
        _417_Expectation_Failed = 417,
        _422_Unprocessable_Entity = 422,
        _423_Locked = 423,
        _424_Failed_Dependency = 424,
        _500_Internal_Server_Error = 500,
        _501_Not_Implemented = 501,
        _502_Bad_Gateway = 502,
        _503_Service_Unavailable = 503,
        _504_Gateway_Timeout = 504,
        _505_HTTP_Version_Not_Supported = 505,
        _507_Insufficient_Storage = 507,
        _999_Unknown = 999;

    public final static String
        MIMETYPE_TEXT_HTML         = "text/html",
        MIMETYPE_TEXT_PLAIN        = "text/plain",
        MIMETYPE_TEXT_XML          = "text/xml",
        MIMETYPE_TEXT_HTML_8859_1  = "text/html; charset=iso-8859-1",
        MIMETYPE_TEXT_PLAIN_8859_1 = "text/plain; charset=iso-8859-1",
        MIMETYPE_TEXT_XML_8859_1   = "text/xml; charset=iso-8859-1",
        MIMETYPE_TEXT_HTML_UTF_8   = "text/html; charset=utf-8",
        MIMETYPE_TEXT_PLAIN_UTF_8  = "text/plain; charset=utf-8",
        MIMETYPE_TEXT_XML_UTF_8    = "text/xml; charset=utf-8";
}
