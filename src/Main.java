import java.time.LocalDate;
import java.util.*;

enum ApplicationStatus { DRAFT, SUBMITTED, INTERVIEW, OFFER, REJECTED }

abstract class Opportunity {
    private final String title;
    private final LocalDate deadline;
    private final boolean sponsorshipAvailable;
    protected Opportunity(String title, LocalDate deadline, boolean sponsorshipAvailable) {
        this.title = Objects.requireNonNull(title);
        this.deadline = Objects.requireNonNull(deadline);
        this.sponsorshipAvailable = sponsorshipAvailable;
    }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public boolean hasSponsorship() { return sponsorshipAvailable; }
    public abstract String getCategory();
}
class IndustryInternship extends Opportunity {
    private final String technology;
    IndustryInternship(String title, LocalDate deadline, boolean sponsorship, String technology) {
        super(title, deadline, sponsorship); this.technology = technology;
    }
    public String getCategory() { return "Industry / " + technology; }
}
class ResearchInternship extends Opportunity {
    private final String researchField;
    ResearchInternship(String title, LocalDate deadline, boolean sponsorship, String researchField) {
        super(title, deadline, sponsorship); this.researchField = researchField;
    }
    public String getCategory() { return "Research / " + researchField; }
}
class Applicant {
    private final String name;
    private final boolean needsVisaSponsorship;
    Applicant(String name, boolean needsVisaSponsorship) {
        this.name = Objects.requireNonNull(name); this.needsVisaSponsorship = needsVisaSponsorship;
    }
    public String getName() { return name; }
    public boolean needsVisaSponsorship() { return needsVisaSponsorship; }
}
class Organization {
    private final String name;
    private final String country;
    private final List<Opportunity> opportunities = new ArrayList<>();
    Organization(String name, String country) { this.name = name; this.country = country; }
    public void addOpportunity(Opportunity opportunity) { opportunities.add(Objects.requireNonNull(opportunity)); }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public List<Opportunity> getOpportunities() { return Collections.unmodifiableList(opportunities); }
}
class Application {
    private final Applicant applicant;
    private final Opportunity opportunity;
    private ApplicationStatus status = ApplicationStatus.DRAFT;
    private final List<String> history = new ArrayList<>();
    Application(Applicant applicant, Opportunity opportunity) {
        this.applicant = applicant; this.opportunity = opportunity;
        history.add("Created as DRAFT");
    }
    public void transitionTo(ApplicationStatus next) {
        boolean allowed = switch (status) {
            case DRAFT -> next == ApplicationStatus.SUBMITTED;
            case SUBMITTED -> next == ApplicationStatus.INTERVIEW || next == ApplicationStatus.REJECTED || next == ApplicationStatus.OFFER;
            case INTERVIEW -> next == ApplicationStatus.OFFER || next == ApplicationStatus.REJECTED;
            case OFFER, REJECTED -> false;
        };
        if (!allowed) throw new IllegalStateException("Invalid transition: " + status + " -> " + next);
        status = next; history.add("Changed to " + next);
    }
    public Applicant getApplicant() { return applicant; }
    public Opportunity getOpportunity() { return opportunity; }
    public ApplicationStatus getStatus() { return status; }
    public List<String> getHistory() { return Collections.unmodifiableList(history); }
}
class EligibilityChecker {
    public boolean canApply(Applicant applicant, Opportunity opportunity, LocalDate today) {
        return !today.isAfter(opportunity.getDeadline()) &&
            (!applicant.needsVisaSponsorship() || opportunity.hasSponsorship());
    }
}
class ApplicationTracker {
    private final Applicant owner;
    private final EligibilityChecker checker = new EligibilityChecker();
    private final List<Application> applications = new ArrayList<>();
    ApplicationTracker(Applicant owner) { this.owner = owner; }
    public Application apply(Opportunity opportunity, LocalDate today) {
        if (!checker.canApply(owner, opportunity, today))
            throw new IllegalArgumentException("Deadline passed or visa sponsorship unavailable");
        for (Application existing : applications)
            if (existing.getOpportunity() == opportunity)
                throw new IllegalArgumentException("Duplicate application");
        Application application = new Application(owner, opportunity);
        applications.add(application);
        return application;
    }
    public List<Application> getApplications() { return Collections.unmodifiableList(applications); }
    public long countByStatus(ApplicationStatus status) {
        return applications.stream().filter(a -> a.getStatus() == status).count();
    }
}
public class Main {
    public static void main(String[] args) {
        LocalDate today = LocalDate.of(2026, 11, 24);
        Applicant applicant = new Applicant("Demo Software Engineering Student", true);
        Organization lab = new Organization("Northstar Research Lab (fictional)", "Germany");
        Opportunity research = new ResearchInternship("Summer AI Research", LocalDate.of(2027, 2, 15), true, "Machine Learning");
        Opportunity industry = new IndustryInternship("Backend Engineering", LocalDate.of(2027, 1, 10), false, "Java");
        lab.addOpportunity(research);
        lab.addOpportunity(industry);
        ApplicationTracker tracker = new ApplicationTracker(applicant);
        Application application = tracker.apply(research, today);
        application.transitionTo(ApplicationStatus.SUBMITTED);
        application.transitionTo(ApplicationStatus.INTERVIEW);
        System.out.println("GlobalPath | " + applicant.getName());
        System.out.println(research.getTitle() + " -> " + application.getStatus());
        System.out.println("History: " + application.getHistory());
        try { tracker.apply(industry, today); }
        catch (IllegalArgumentException e) { System.out.println("Blocked: " + e.getMessage()); }
        try { tracker.apply(research, today); }
        catch (IllegalArgumentException e) { System.out.println("Blocked: " + e.getMessage()); }
        System.out.println("Interviews: " + tracker.countByStatus(ApplicationStatus.INTERVIEW));
    }
}
