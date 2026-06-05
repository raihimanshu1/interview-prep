package wellsfargo.solutions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LedgerReconciliation {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given internal ledger transactions and external settlement transactions,
     * return records that do not have a matching transaction on the other side.
     *
     * INPUT
     * internal is the bank-side ledger list.
     * external is the network/partner settlement list.
     *
     * A transaction matches when reference, amount, currency, and settlement
     * date all match.
     *
     * OUTPUT
     * A list of mismatch labels:
     * MISSING_EXTERNAL:ref means internal record has no external match.
     * EXTRA_EXTERNAL:ref means external record has no internal match.
     *
     * EXAMPLE
     * internal = [R1 $10 USD 2026-05-01, R2 $20 USD 2026-05-01]
     * external = [R1 $10 USD 2026-05-01]
     * Output: [MISSING_EXTERNAL:R2]
     *
     * WHAT IT MEANS
     * Reconciliation is matching two sources of truth and explaining what did
     * not line up.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * In banking, matching by reference alone is dangerous.
     *
     * A reference can be duplicated, corrected, or replayed. A safer matching
     * key includes the business facts that define the transaction:
     *
     * reference + amount + currency + settlement date.
     *
     * We store duplicate counts, not just booleans, because the same key can
     * legitimately appear more than once.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1 - Perfect match
     * internal = [R1 10 USD D1]
     * external = [R1 10 USD D1]
     * Output: []
     *
     * Example 2 - Missing external
     * internal = [R1 10 USD D1]
     * external = []
     * Output: [MISSING_EXTERNAL:R1]
     *
     * Example 3 - Extra external
     * internal = []
     * external = [R1 10 USD D1]
     * Output: [EXTRA_EXTERNAL:R1]
     *
     * Edge case - Duplicate matching records should consume one count at a time.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * For every internal transaction, scan every external transaction until we
     * find one unmatched record with the same business key.
     *
     * Time Complexity: O(internal * external)
     * Space Complexity: O(external) for matched flags
     */
    public List<String> bruteForce(Transaction[] internal, Transaction[] external) {
        boolean[] matchedExternal = new boolean[external.length];
        List<String> mismatches = new ArrayList<>();

        for (Transaction internalRecord : internal) {
            int matchIndex = -1;

            for (int i = 0; i < external.length; i++) {
                // matchedExternal prevents one external row from satisfying
                // two internal rows.
                if (!matchedExternal[i] && internalRecord.sameBusinessKey(external[i])) {
                    matchIndex = i;
                    break;
                }
            }

            if (matchIndex == -1) {
                mismatches.add("MISSING_EXTERNAL:" + internalRecord.reference);
            } else {
                matchedExternal[matchIndex] = true;
            }
        }

        for (int i = 0; i < external.length; i++) {
            if (!matchedExternal[i]) {
                mismatches.add("EXTRA_EXTERNAL:" + external[i].reference);
            }
        }

        return mismatches;
    }

    /*
     * OPTIMIZED APPROACH
     *
     * The repeated work in brute force is scanning external rows again and again.
     *
     * Instead, count external rows by business key.
     * Then each internal row consumes one matching count.
     *
     * Time Complexity: O(internal + external)
     * Space Complexity: O(external)
     */
    public List<String> optimized(Transaction[] internal, Transaction[] external) {
        Map<String, Integer> externalCounts = new HashMap<>();

        for (Transaction transaction : external) {
            String key = transaction.key();
            externalCounts.put(key, externalCounts.getOrDefault(key, 0) + 1);
        }

        List<String> mismatches = new ArrayList<>();

        for (Transaction transaction : internal) {
            String key = transaction.key();
            int count = externalCounts.getOrDefault(key, 0);

            if (count == 0) {
                mismatches.add("MISSING_EXTERNAL:" + transaction.reference);
            } else {
                // Consume exactly one external match for this internal record.
                externalCounts.put(key, count - 1);
            }
        }

        for (Map.Entry<String, Integer> entry : externalCounts.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                mismatches.add("EXTRA_EXTERNAL:" + entry.getKey());
            }
        }

        return mismatches;
    }

    public static class Transaction {
        String reference;
        BigDecimal amount;
        String currency;
        String settlementDate;

        public Transaction(String reference, BigDecimal amount, String currency, String settlementDate) {
            this.reference = reference;
            // Normalize scale so 10.0 and 10.00 compare as the same money value.
            this.amount = amount.stripTrailingZeros();
            this.currency = currency;
            this.settlementDate = settlementDate;
        }

        boolean sameBusinessKey(Transaction other) {
            return key().equals(other.key());
        }

        String key() {
            return reference + "|" + amount.toPlainString() + "|" + currency + "|" + settlementDate;
        }
    }

    public static void main(String[] args) {
        LedgerReconciliation solver = new LedgerReconciliation();

        Transaction[] internal = {
                new Transaction("R1", new BigDecimal("10.00"), "USD", "2026-05-01"),
                new Transaction("R2", new BigDecimal("20.00"), "USD", "2026-05-01")
        };
        Transaction[] external = {
                new Transaction("R1", new BigDecimal("10.0"), "USD", "2026-05-01")
        };

        System.out.println("Brute force: " + solver.bruteForce(internal, external));
        System.out.println("Optimized: " + solver.optimized(internal, external));

        System.out.println("Extra external: " + solver.optimized(new Transaction[0], external));
    }
}
