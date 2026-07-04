
package com.companywisejavasolutions.wellsFargo.solutions;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IdempotentPaymentRequests {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given payment requests with idempotency keys and request hashes, decide
     * whether each request should be processed, replayed, or rejected.
     *
     * INPUT
     * Each PaymentRequest has:
     * idempotencyKey - client-generated key for retry safety.
     * requestHash - hash of the request body.
     *
     * OUTPUT
     * PROCESS key - first time this key is seen.
     * REPLAY key  - same key and same request hash already processed.
     * REJECT key  - same key but different request hash.
     *
     * EXAMPLE
     * requests = [
     *   ("k1", "hashA"),
     *   ("k1", "hashA"),
     *   ("k1", "hashB")
     * ]
     * Output:
     * PROCESS k1
     * REPLAY k1
     * REJECT k1
     *
     * WHAT IT MEANS
     * A retry should not move money twice. But the same key with a different
     * body is suspicious and should not replay the old response.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * Idempotency is a safety feature for unreliable networks.
     *
     * A customer may click Pay, the server may succeed, but the client may time
     * out before receiving the response. The client retries. The bank must not
     * create a second payment.
     *
     * The idempotency key says:
     * "This retry belongs to the same logical operation."
     *
     * The request hash says:
     * "The body is still the same operation."
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1 - Safe retry:
     * ("pay-1", "A"), ("pay-1", "A")
     * Output: PROCESS pay-1, REPLAY pay-1
     *
     * Example 2 - Bad reuse:
     * ("pay-1", "A"), ("pay-1", "B")
     * Output: PROCESS pay-1, REJECT pay-1
     *
     * Example 3 - Independent keys:
     * ("pay-1", "A"), ("pay-2", "A")
     * Output: PROCESS pay-1, PROCESS pay-2
     *
     * Edge case - Empty request list returns an empty string.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * For each request, scan all earlier requests looking for the same
     * idempotency key.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1) besides output
     */
    public String bruteForce(PaymentRequest[] requests) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < requests.length; i++) {
            PaymentRequest firstSeen = null;

            // Brute force asks history directly:
            // "Have I ever seen this idempotency key before?"
            for (int previous = 0; previous < i; previous++) {
                if (requests[previous].idempotencyKey.equals(requests[i].idempotencyKey)) {
                    firstSeen = requests[previous];
                    break;
                }
            }

            appendDecision(result, requests[i], firstSeen);
        }

        return result.toString();
    }

    /*
     * OPTIMIZED APPROACH
     *
     * Store the first decision for each idempotency key in a map.
     * This removes the repeated scan through history.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public String optimized(PaymentRequest[] requests) {
        Map<String, StoredDecision> keyToDecision = new HashMap<>();
        StringBuilder result = new StringBuilder();

        for (PaymentRequest request : requests) {
            StoredDecision existing = keyToDecision.get(request.idempotencyKey);

            if (existing == null) {
                // In a real service, this should be an atomic database insert
                // before performing the payment side effect.
                keyToDecision.put(request.idempotencyKey,
                        new StoredDecision(request.requestHash, "SUCCEEDED"));
                result.append("PROCESS ").append(request.idempotencyKey).append('\n');
                continue;
            }

            if (existing.requestHash.equals(request.requestHash)) {
                result.append("REPLAY ").append(request.idempotencyKey).append('\n');
            } else {
                result.append("REJECT ").append(request.idempotencyKey).append('\n');
            }
        }

        return result.toString();
    }

    private void appendDecision(StringBuilder result, PaymentRequest current, PaymentRequest firstSeen) {
        if (firstSeen == null) {
            result.append("PROCESS ").append(current.idempotencyKey).append('\n');
        } else if (firstSeen.requestHash.equals(current.requestHash)) {
            result.append("REPLAY ").append(current.idempotencyKey).append('\n');
        } else {
            result.append("REJECT ").append(current.idempotencyKey).append('\n');
        }
    }

    public static class PaymentRequest {
        String idempotencyKey;
        String requestHash;

        public PaymentRequest(String idempotencyKey, String requestHash) {
            this.idempotencyKey = Objects.requireNonNull(idempotencyKey);
            this.requestHash = Objects.requireNonNull(requestHash);
        }
    }

    private static class StoredDecision {
        String requestHash;
        String status;

        StoredDecision(String requestHash, String status) {
            this.requestHash = requestHash;
            this.status = status;
        }
    }

    public static void main(String[] args) {
        IdempotentPaymentRequests solver = new IdempotentPaymentRequests();

        PaymentRequest[] requests = {
                new PaymentRequest("pay-1", "hashA"),
                new PaymentRequest("pay-1", "hashA"),
                new PaymentRequest("pay-1", "hashB"),
                new PaymentRequest("pay-2", "hashA")
        };

        System.out.println("Brute force:\n" + solver.bruteForce(requests));
        System.out.println("Optimized:\n" + solver.optimized(requests));
    }
}
