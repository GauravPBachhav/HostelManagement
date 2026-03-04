package in.gw.main.Entity;

/**
 * Status of a support query / complaint.
 *
 * OPEN     = student submitted query, waiting for admin reply
 * RESOLVED = admin has replied and resolved the query
 */
public enum QueryStatus {
    OPEN,
    RESOLVED
}
