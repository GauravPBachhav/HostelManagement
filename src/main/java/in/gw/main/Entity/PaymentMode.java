package in.gw.main.Entity;

/**
 * How the rent was paid.
 *
 * CASH   = paid in cash at hostel office
 * UPI    = paid via UPI (Google Pay, PhonePe, etc.)
 * ONLINE = paid via net banking or card
 */
public enum PaymentMode {
    CASH,
    UPI,
    ONLINE
}
