import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Context {
    private static Multimap<String, PccFile> pccsByCampaign = HashMultimap.create();
    private static Multimap<String, PccFile> pccsByType = HashMultimap.create();
    private static boolean initialized = false;

    public static List<PccFile> getPCCByCampaign(String name) {
        return Lists.newArrayList(pccsByCampaign.get(name));
    }

    public static List<PccFile> getPCCByType(String type) {
        return Lists.newArrayList(pccsByType.get(type));
    }

    public static void initiateContext(File project) {
        if(initialized){
            return;
        }
        initialized = true;

        File cursor = project;

        while (!"data".equals(cursor.getParentFile().getName())) {
            cursor = cursor.getParentFile();
        }

        List<File> pccFiles = findPCCFiles(List.of(cursor.listFiles()));

        pccFiles.forEach(f -> {
            PccFile pccFile = new PccFile(f, PCCType.DEPENDENCY);
            if (pccFile.getBookType() != null) {
                pccsByType.put(pccFile.getBookType(), pccFile);
            }
            if (pccFile.getCampaign() != null) {
                pccsByCampaign.put(pccFile.getCampaign(), pccFile);
            }
        });
    }

    private static List<File> findPCCFiles(List<File> listFiles) {
        List<File> pccFiles = new ArrayList<>();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                pccFiles.addAll(findPCCFiles(List.of(file.listFiles())));
            }
            if (file.getName().endsWith(".pcc")) {
                pccFiles.add(file);
            }
        }
        return pccFiles;
    }
}
