import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InternationalUniversityApp {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * A university app works in the US. Now it must support international users.
     * Design the core LLD objects that keep region, language, timezone, and data
     * residency decisions out of random business code.
     *
     * Sample Flow:
     * user country = "DE"
     * router chooses EU region
     * profile uses German locale and Europe/Berlin timezone
     *
     * Why? International rollout is not only translation. It affects routing,
     * privacy, time, payments, support, and feature rollout.
     *
     * Sample Input:
     * UserProfile(userId="u1", countryCode="DE", locale=GERMAN, zoneId=Europe/Berlin)
     *
     * Sample Output:
     * UniversityAppConfig(region=EU, regionalStorageRequired=true, greeting="Willkommen")
     *
     * What is the problem really asking?
     * The problem is to prevent international rules from being scattered across controllers and services. Country, locale, timezone, and storage policy should be decided in one place.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Do not sprinkle if-country-is-DE checks everywhere. Put international
     * rules behind small services: RegionRouter, LocalizationService, and
     * CompliancePolicy.
     */

    /*
     * BASELINE DESIGN
     *
     * Keep one US deployment and add a country field on user. This is simple,
     * but latency, timezone bugs, and data residency rules leak into the app.
     */

    /*
     * STRONGER DESIGN
     *
     * Route each user to a region. Store profile locale/timezone. Put privacy
     * rules in CompliancePolicy. Use feature flags for country rollout.
     */

    /*
     * APPROACH AND WHY
     *
     * Approach:
     * Create small services: RegionRouter decides deployment region, CompliancePolicy decides data residency, LocalizationService returns local text, and ConfigService combines them.
     *
     * Why this approach works:
     * Each class has one job. When a new country launches, we update routing/compliance/localization rules without touching unrelated product logic.
     */
    public static class UserProfile {
        private final String userId;
        private final String countryCode;
        private final Locale locale;
        private final ZoneId zoneId;

        public UserProfile(String userId, String countryCode, Locale locale, ZoneId zoneId) {
            // userId identifies the student/teacher/account.
            this.userId = userId;

            // countryCode drives routing and compliance decisions.
            this.countryCode = countryCode;

            // locale drives language and formatting.
            this.locale = locale;

            // zoneId drives deadline, class time, and notification behavior.
            this.zoneId = zoneId;
        }

        public String getUserId() {
            return userId;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public Locale getLocale() {
            return locale;
        }

        public ZoneId getZoneId() {
            return zoneId;
        }
    }

    public enum Region {
        US, EU, APAC
    }

    public static class RegionRouter {
        public Region route(String countryCode) {
            // EU countries should be served from EU infrastructure.
            if ("DE".equals(countryCode) || "FR".equals(countryCode) || "NL".equals(countryCode)) {
                return Region.EU;
            }

            // APAC users should be closer to APAC infrastructure.
            if ("IN".equals(countryCode) || "SG".equals(countryCode) || "JP".equals(countryCode)) {
                return Region.APAC;
            }

            // Default to US for existing/default market.
            return Region.US;
        }
    }

    public static class CompliancePolicy {
        public boolean requiresRegionalStorage(String countryCode) {
            // In this simplified model, EU countries require regional storage.
            return "DE".equals(countryCode) || "FR".equals(countryCode) || "NL".equals(countryCode);
        }
    }

    public static class LocalizationService {
        private final Map<String, String> greetings = new HashMap<>();

        public LocalizationService() {
            // English fallback for existing/default users.
            greetings.put("en", "Welcome");

            // German greeting for German locale users.
            greetings.put("de", "Willkommen");

            // French greeting for French locale users.
            greetings.put("fr", "Bienvenue");
        }

        public String greetingFor(UserProfile profile) {
            // Locale language gives values like "en", "de", or "fr".
            String language = profile.getLocale().getLanguage();

            // If we do not support the language yet, return English.
            return greetings.getOrDefault(language, greetings.get("en"));
        }
    }

    public static class UniversityAppConfig {
        private final Region region;
        private final boolean regionalStorageRequired;
        private final String greeting;

        public UniversityAppConfig(Region region, boolean regionalStorageRequired, String greeting) {
            // Region tells the app/backend where this user should be served.
            this.region = region;

            // This says whether storage must stay in that user's legal region.
            this.regionalStorageRequired = regionalStorageRequired;

            // Greeting is a tiny visible proof that localization is applied.
            this.greeting = greeting;
        }

        public Region getRegion() {
            return region;
        }

        public boolean isRegionalStorageRequired() {
            return regionalStorageRequired;
        }

        public String getGreeting() {
            return greeting;
        }
    }

    public static class ConfigService {
        private final RegionRouter router = new RegionRouter();
        private final CompliancePolicy compliancePolicy = new CompliancePolicy();
        private final LocalizationService localizationService = new LocalizationService();

        public UniversityAppConfig configFor(UserProfile profile) {
            // Decide which deployment region should serve this user.
            Region region = router.route(profile.getCountryCode());

            // Keep privacy/data-residency decision separate from routing decision.
            boolean storageRequired = compliancePolicy.requiresRegionalStorage(profile.getCountryCode());

            // Choose user-facing text from locale instead of hardcoding English.
            String greeting = localizationService.greetingFor(profile);

            // Combine all international decisions into one config object.
            return new UniversityAppConfig(region, storageRequired, greeting);
        }
    }

    public static void main(String[] args) {
        ConfigService service = new ConfigService();

        UserProfile germanUser = new UserProfile("u1", "DE", Locale.GERMAN, ZoneId.of("Europe/Berlin"));
        UserProfile indiaUser = new UserProfile("u2", "IN", Locale.forLanguageTag("en-IN"), ZoneId.of("Asia/Kolkata"));
        UserProfile usUser = new UserProfile("u3", "US", Locale.ENGLISH, ZoneId.of("America/New_York"));

        runSample(service, "Sample 1 - Germany", germanUser);
        runSample(service, "Sample 2 - India", indiaUser);
        runSample(service, "Sample 3 - United States", usUser);
    }

    private static void runSample(ConfigService service, String label, UserProfile profile) {
        UniversityAppConfig config = service.configFor(profile);
        System.out.println(label);
        System.out.println("userId: " + profile.getUserId());
        System.out.println("country: " + profile.getCountryCode());
        System.out.println("timezone: " + profile.getZoneId());
        System.out.println("region: " + config.getRegion());
        System.out.println("regionalStorageRequired: " + config.isRegionalStorageRequired());
        System.out.println("greeting: " + config.getGreeting());
        System.out.println();
    }
}
