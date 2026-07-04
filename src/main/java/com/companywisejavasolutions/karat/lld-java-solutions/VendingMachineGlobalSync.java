
package com.companywisejavasolutions.karat.lldJavaSolutions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VendingMachineGlobalSync {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Vending machines around the world sync every night. Design sync handling
     * that survives offline machines, duplicate uploads, and clock issues.
     *
     * Sample Input:
     * SyncEvent(eventId="e1", machineId="m1", itemId="chips", quantityDelta=-2) submitted twice
     *
     * Sample Output:
     * first submit updates inventory; second submit is ignored as duplicate.
     *
     * What is the problem really asking?
     * Global sync jobs fail in real life because machines go offline and retry.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A sync event should be safe to retry. If the same event arrives twice, it
     * should not double count inventory or sales.
     */

    /*
     * BASELINE DESIGN
     *
     * Run one global batch at 1 AM. This creates spikes and fails when machines
     * are offline.
     */

    /*
     * STRONGER DESIGN
     *
     * Each machine uploads idempotent events. Server deduplicates by event ID
     * and updates inventory/sales safely. Schedule sync by region with jitter.
     */

    /*
     * APPROACH AND WHY
     *
     * Approach:
     * Make sync event processing idempotent using event IDs and update inventory through a SyncService.
     *
     * Why this approach works:
     * Retries become safe because duplicate event IDs are ignored.
     */
    public static class SyncEvent {
        private final String eventId;
        private final String machineId;
        private final String itemId;
        private final int quantityDelta;

        public SyncEvent(String eventId, String machineId, String itemId, int quantityDelta) {
            // eventId makes the event idempotent; duplicates have the same id.
            this.eventId = eventId;

            // machineId tells us which vending machine changed inventory.
            this.machineId = machineId;

            // itemId tells us which product changed.
            this.itemId = itemId;

            // quantityDelta is positive for restock and negative for sales.
            this.quantityDelta = quantityDelta;
        }
    }

    public static class SyncService {
        private final Set<String> processedEventIds = new HashSet<>();
        private final Map<String, Integer> inventory = new HashMap<>();

        public boolean process(SyncEvent event) {
            // If we have already processed this event id, ignore it.
            // This is what makes retry safe.
            if (processedEventIds.contains(event.eventId)) {
                return false;
            }

            // Mark the id before applying the inventory update.
            processedEventIds.add(event.eventId);

            // Inventory is tracked per machine and item.
            String key = event.machineId + ":" + event.itemId;

            // Missing inventory starts at zero in this simplified example.
            int currentQuantity = inventory.getOrDefault(key, 0);

            // Apply the delta from the sync event.
            int updatedQuantity = currentQuantity + event.quantityDelta;

            // Save the new quantity.
            inventory.put(key, updatedQuantity);

            // true means the event changed state.
            return true;
        }

        public int quantity(String machineId, String itemId) {
            // Use the same machine:item key formula as process().
            return inventory.getOrDefault(machineId + ":" + itemId, 0);
        }
    }

    public static void main(String[] args) {
        SyncService service = new SyncService();

        runSample(service, "Sample 1 - first sale event",
                new SyncEvent("e1", "m1", "chips", -2), "m1", "chips");

        runSample(service, "Sample 2 - duplicate sale event ignored",
                new SyncEvent("e1", "m1", "chips", -2), "m1", "chips");

        runSample(service, "Sample 3 - restock event",
                new SyncEvent("e2", "m1", "chips", 10), "m1", "chips");
    }

    private static void runSample(SyncService service, String label,
            SyncEvent event, String machineId, String itemId) {
        boolean processed = service.process(event);
        System.out.println(label);
        System.out.println("processed: " + processed);
        System.out.println("quantity(" + machineId + ", " + itemId + "): "
                + service.quantity(machineId, itemId));
        System.out.println();
    }
}
