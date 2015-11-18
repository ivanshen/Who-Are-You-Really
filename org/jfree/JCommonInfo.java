package org.jfree;

import java.util.Arrays;
import java.util.ResourceBundle;
import org.jfree.base.BaseBoot;
import org.jfree.base.Library;
import org.jfree.ui.about.Contributor;
import org.jfree.ui.about.Licences;
import org.jfree.ui.about.ProjectInfo;
import org.jfree.util.ResourceBundleWrapper;

public class JCommonInfo extends ProjectInfo {
    private static JCommonInfo singleton;

    public static synchronized JCommonInfo getInstance() {
        JCommonInfo jCommonInfo;
        synchronized (JCommonInfo.class) {
            if (singleton == null) {
                singleton = new JCommonInfo();
            }
            jCommonInfo = singleton;
        }
        return jCommonInfo;
    }

    private JCommonInfo() {
        String baseResourceClass = "org.jfree.resources.JCommonResources";
        ResourceBundle resources = ResourceBundleWrapper.getBundle("org.jfree.resources.JCommonResources");
        setName(resources.getString("project.name"));
        setVersion(resources.getString("project.version"));
        setInfo(resources.getString("project.info"));
        setCopyright(resources.getString("project.copyright"));
        setLicenceName("LGPL");
        setLicenceText(Licences.getInstance().getLGPL());
        setContributors(Arrays.asList(new Contributor[]{new Contributor("Anthony Boulestreau", "-"), new Contributor("Jeremy Bowman", "-"), new Contributor("J. David Eisenberg", "-"), new Contributor("Paul English", "-"), new Contributor("David Gilbert", "david.gilbert@object-refinery.com"), new Contributor("Hans-Jurgen Greiner", "-"), new Contributor("Arik Levin", "-"), new Contributor("Achilleus Mantzios", "-"), new Contributor("Thomas Meier", "-"), new Contributor("Aaron Metzger", "-"), new Contributor("Thomas Morgner", "-"), new Contributor("Krzysztof Paz", "-"), new Contributor("Nabuo Tamemasa", "-"), new Contributor("Mark Watson", "-"), new Contributor("Matthew Wright", "-"), new Contributor("Hari", "-"), new Contributor("Sam (oldman)", "-")}));
        addOptionalLibrary(new Library("JUnit", "3.8", "IBM Public Licence", "http://www.junit.org/"));
        setBootClass(BaseBoot.class.getName());
    }
}
