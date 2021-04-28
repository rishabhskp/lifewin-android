package app.lifewin.interfaces;

/**
 * Interface for getting the result from webservice.
 */
public interface IWebServiceResponse {
	/** Method for getting the webservice result */
	    void onResult(Object result);
}