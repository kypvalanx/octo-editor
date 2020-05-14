import java.util.List;

public class Dependency {
    private final String name;
    private final String type;

    public Dependency(String group) {
        String[] tokens = group.split("=");

        name = tokens[1];
        type = tokens[0];
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<PccFile> resolvedPCC(){
        if("INCLUDESBOOKTYPE".equals(type) || "BOOKTYPE".equals(type)){
            return Context.getPCCByType(name);
        }
        if("INCLUDES".equals(type)){
            return Context.getPCCByCampaign(name);
        }
        throw new IllegalStateException(this + " PCC cannot be resolved");
    }
}
