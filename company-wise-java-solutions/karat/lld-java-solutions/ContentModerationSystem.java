import java.util.ArrayDeque;
import java.util.Queue;

public class ContentModerationSystem {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Design a moderation system for user-generated content. Some content can
     * be auto-approved, some auto-blocked, and uncertain content goes to review.
     *
     * Sample Input:
     * Content text contains "review"
     *
     * Sample Output:
     * ModerationDecision.NEEDS_REVIEW and item enters review queue.
     *
     * What is the problem really asking?
     * Moderation needs fast decisions for low/high risk content and human review for uncertain content.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Moderation is a pipeline. Score risk, decide action, and keep an audit
     * trail so decisions can be reviewed.
     */

    /*
     * BASELINE DESIGN
     *
     * Send everything to human review. Accurate but slow and expensive.
     */

    /*
     * STRONGER DESIGN
     *
     * Use automated risk scoring. Low risk is approved, high risk is blocked,
     * medium risk goes to a human queue.
     */

    /*
     * APPROACH AND WHY
     *
     * Approach:
     * Use RiskScorer and ModerationService. Score content, approve low risk, block high risk, queue medium risk.
     *
     * Why this approach works:
     * The pipeline is simple, auditable, and easy to extend with real ML/rules later.
     */
    public enum ModerationDecision {
        APPROVE, BLOCK, NEEDS_REVIEW
    }

    public static class Content {
        final String contentId;
        final String text;

        public Content(String contentId, String text) {
            // Store a stable id so audits and review queues can point to the same content later.
            this.contentId = contentId;

            // Store the actual submitted text that the moderation logic will inspect.
            this.text = text;
        }
    }

    public static class RiskScorer {
        public int score(Content content) {
            // Normalize to lowercase so "BLOCKED", "Blocked", and "blocked" behave the same.
            String text = content.text.toLowerCase();

            // In this teaching version, the word "blocked" means very high risk.
            if (text.contains("blocked")) {
                return 95;
            }

            // The word "review" represents unclear content that should go to a person.
            if (text.contains("review")) {
                return 60;
            }

            // If no risky keyword appears, treat it as low-risk content.
            return 10;
        }
    }

    public static class ModerationService {
        private final RiskScorer scorer = new RiskScorer();
        private final Queue<Content> reviewQueue = new ArrayDeque<>();

        public ModerationDecision moderate(Content content) {
            // First get a numeric risk score from the scoring component.
            int score = scorer.score(content);

            // Very high risk can be blocked automatically.
            if (score >= 90) {
                return ModerationDecision.BLOCK;
            }

            // Medium risk should not be auto-approved or auto-blocked.
            // Put it into the human review queue.
            if (score >= 50) {
                reviewQueue.offer(content);
                return ModerationDecision.NEEDS_REVIEW;
            }

            // Low risk can be approved immediately.
            return ModerationDecision.APPROVE;
        }

        public Content nextReviewItem() {
            // poll() returns the next item or null if the queue is empty.
            return reviewQueue.poll();
        }
    }

    public static void main(String[] args) {
        ModerationService service = new ModerationService();

        Content sample1 = new Content("c1", "Nice campus photo");
        Content sample2 = new Content("c2", "Please review this borderline post");
        Content sample3 = new Content("c3", "This contains blocked content");

        runSample(service, "Sample 1 - low risk", sample1);
        runSample(service, "Sample 2 - needs review", sample2);
        runSample(service, "Sample 3 - blocked", sample3);

        Content reviewItem = service.nextReviewItem();
        System.out.println("Next review item: " + (reviewItem == null ? "none" : reviewItem.contentId));
    }

    private static void runSample(ModerationService service, String label, Content content) {
        System.out.println(label);
        System.out.println("contentId: " + content.contentId);
        System.out.println("decision: " + service.moderate(content));
        System.out.println();
    }
}
