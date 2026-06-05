import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PhotoStorageCapacityDesign {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Estimate photo storage and design a simple photo metadata/object-storage
     * split.
     *
     * Sample Input:
     * users=10_000_000, photosPerUser=100, averageMb=3, replicationFactor=3
     *
     * Sample Output:
     * raw storage about 2861 TB; replicated storage about 8583 TB.
     *
     * What is the problem really asking?
     * Photo bytes and photo metadata have different storage/query needs.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Photos are large binary objects. Store the binary in object storage and
     * searchable details in a database-like metadata store.
     */

    /*
     * BASELINE DESIGN
     *
     * Put image bytes directly in one database table. Simple, but expensive and
     * hard to scale.
     */

    /*
     * STRONGER DESIGN
     *
     * Object storage keeps bytes. Metadata keeps owner, key, size, visibility,
     * and timestamps. CDN can serve public-safe derived images.
     */

    /*
     * APPROACH AND WHY
     *
     * Approach:
     * Estimate capacity separately, store photo bytes in object storage, and store metadata in a metadata service.
     *
     * Why this approach works:
     * Object storage is better for large blobs; metadata store is better for search, ownership, and permissions.
     */
    public static class Estimate {
        private final double rawTb;
        private final double replicatedTb;

        public Estimate(double rawTb, double replicatedTb) {
            // Raw TB is the logical photo data before replication.
            this.rawTb = rawTb;

            // Replicated TB is what storage actually needs after copies.
            this.replicatedTb = replicatedTb;
        }

        public double getRawTb() {
            return rawTb;
        }

        public double getReplicatedTb() {
            return replicatedTb;
        }
    }

    public static class PhotoMetadata {
        private final String photoId;
        private final String ownerId;
        private final String objectKey;
        private final long sizeBytes;

        public PhotoMetadata(String photoId, String ownerId, String objectKey, long sizeBytes) {
            // photoId is the app-level id used by API callers.
            this.photoId = photoId;

            // ownerId tells us who owns the photo.
            this.ownerId = ownerId;

            // objectKey tells object storage where the binary bytes live.
            this.objectKey = objectKey;

            // sizeBytes supports quota, billing, and capacity tracking.
            this.sizeBytes = sizeBytes;
        }
    }

    public Estimate estimateStorage(long users, int photosPerUser, double averageMb, int replicationFactor) {
        // Total MB = users * photos per user * average photo size.
        double totalMb = users * photosPerUser * averageMb;

        // Convert MB to TB by dividing by 1024 twice: MB -> GB -> TB.
        double rawTb = totalMb / 1024.0 / 1024.0;

        // Replication multiplies physical storage because each copy consumes space.
        double replicatedTb = rawTb * replicationFactor;

        // Return both numbers so interview discussion can show the difference.
        return new Estimate(rawTb, replicatedTb);
    }

    public static class PhotoService {
        private final Map<String, PhotoMetadata> metadataById = new HashMap<>();

        public String registerUpload(String ownerId, long sizeBytes) {
            // Generate a unique app id for this uploaded photo.
            String photoId = UUID.randomUUID().toString();

            // Object key groups the photo under the owner for easier organization.
            String objectKey = ownerId + "/" + photoId;

            // Store only metadata here. The actual image bytes would go to object storage.
            metadataById.put(photoId, new PhotoMetadata(photoId, ownerId, objectKey, sizeBytes));

            // Return the id so the caller can fetch metadata later.
            return photoId;
        }

        public PhotoMetadata getMetadata(String photoId) {
            // Metadata lookup is by photo id.
            return metadataById.get(photoId);
        }
    }

    public static void main(String[] args) {
        PhotoStorageCapacityDesign design = new PhotoStorageCapacityDesign();

        runEstimate(design, "Sample 1 - large app", 10_000_000L, 100, 3.0, 3);
        runEstimate(design, "Sample 2 - smaller app", 100_000L, 50, 2.5, 2);
        runEstimate(design, "Sample 3 - high replication", 1_000_000L, 200, 4.0, 4);

        PhotoService service = new PhotoService();
        String photoId = service.registerUpload("user-1", 2_000_000L);
        PhotoMetadata metadata = service.getMetadata(photoId);
        System.out.println("Registered photo metadata: " + format(metadata));
    }

    private static void runEstimate(PhotoStorageCapacityDesign design, String label,
            long users, int photosPerUser, double averageMb, int replicationFactor) {
        Estimate estimate = design.estimateStorage(users, photosPerUser, averageMb, replicationFactor);
        System.out.println(label);
        System.out.println("users: " + users);
        System.out.println("photosPerUser: " + photosPerUser);
        System.out.println("averageMb: " + averageMb);
        System.out.println("replicationFactor: " + replicationFactor);
        System.out.printf("rawTb: %.2f%n", estimate.getRawTb());
        System.out.printf("replicatedTb: %.2f%n", estimate.getReplicatedTb());
        System.out.println();
    }

    private static String format(PhotoMetadata metadata) {
        if (metadata == null) {
            return "not found";
        }
        return "PhotoMetadata(photoId=" + metadata.photoId
                + ", ownerId=" + metadata.ownerId
                + ", objectKey=" + metadata.objectKey
                + ", sizeBytes=" + metadata.sizeBytes + ")";
    }
}
