package in.gw.main.Entity;

/**
 * Status of a rent payment.
 *
 * PAID                  = rent verified and confirmed by admin
 * PENDING               = rent is due but not yet paid
 * VERIFICATION_PENDING  = student uploaded proof, waiting for admin verification
 * REJECTED              = admin rejected the payment proof
 */
public enum PaymentStatus {
    PAID,
    PENDING,
    VERIFICATION_PENDING,
    REJECTED
}
