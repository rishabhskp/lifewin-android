package app.lifewin.parse.interfaces;

/**
 * Interface for result of ParseQuery.
 */
public interface IParseQueryResult {
    /**
     * Method for parse query successfully execute
     */
    void onParseQuerySuccess(Object obj);

    /**
     * Method for parse if query failed.
     */
    void onParseQueryFail(Object obj);
}
