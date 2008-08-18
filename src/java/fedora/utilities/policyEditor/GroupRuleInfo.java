/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Apache License, Version 2.0 (the "License"); you may not use 
 * this file except in compliance with the License. You may obtain a copy of 
 * the License at <a href="http://www.fedora-commons.org/licenses">
 * http://www.fedora-commons.org/licenses.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.</p>
 * <p>Copyright &copy; 2008 Fedora Commons, Inc.<br />
 * <p>Copyright &copy; 2002-2007 The Rector and Visitors of the University of 
 * Virginia and Cornell University<br /> 
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.utilities.policyEditor;

import java.io.PrintWriter;

import java.util.Vector;

/**
 * @author Robert Haschart
 */
public class GroupRuleInfo {

    String name;

    boolean accept;

    String description;

    String subject;

    String condition;

    Vector parms;

    String buildParms;

    int refcount;

    public static Vector permitRules = null;

    public static Vector denyRules = null;

    public static Vector permitTemplates = null;

    public static Vector denyTemplates = null;

    public final static int AND = 0;

    public final static int OR = 1;

    public GroupRuleInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public GroupRuleInfo(String _name,
                         String _desc,
                         String _subject,
                         String _condition,
                         String _parmsWithSemiColons,
                         boolean _accept) {
        setName(_name);
        setDescription(_desc);
        setSubject(_subject);
        setCondition(_condition);
        parms = new Vector();
        String parms[] = _parmsWithSemiColons.split(";");
        setParms(parms);
        accept = _accept;
        refcount = 0;
    }

    private GroupRuleInfo(Object ruleTemplateObj, String _parmsWithSemiColons) {
        GroupRuleInfo ruleTemplate = (GroupRuleInfo) ruleTemplateObj;
        parms = new Vector();
        if (_parmsWithSemiColons != null) {
            String parms[] = _parmsWithSemiColons.split(";");
            setParms(parms);
        }
        setName(expandString(ruleTemplate.getName()));
        setDescription(expandString(ruleTemplate.getDescription()));
        setSubject(expandString(ruleTemplate.getSubject()));
        setCondition(expandString(ruleTemplate.getCondition()));
        accept = ruleTemplate.accept;
        refcount = 0;
    }

    public static void buildFromTemplate(boolean accept,
                                         int templateNum,
                                         String parms) {
        GroupRuleInfo tmp;
        if (accept) {
            tmp =
                    new GroupRuleInfo(permitTemplates.elementAt(templateNum),
                                      parms);
            tmp.buildParms = "permit,Template," + templateNum;
            permitRules.addElement(tmp);
        } else {
            tmp =
                    new GroupRuleInfo(denyTemplates.elementAt(templateNum),
                                      parms);
            tmp.buildParms = "deny,Template," + templateNum;
            denyRules.addElement(tmp);
        }
    }

    public void rebuildFromTemplate(GroupRuleInfo template,
                                    String _parmsWithSemiColons) {
        parms = new Vector();
        if (_parmsWithSemiColons != null) {
            String parms[] = _parmsWithSemiColons.split(";");
            setParms(parms);
        }
        setName(expandString(template.getName()));
        setDescription(expandString(template.getDescription()));
        setSubject(expandString(template.getSubject()));
        setCondition(expandString(template.getCondition()));
        accept = template.accept;
    }

    private GroupRuleInfo(Object ruleTemplateObj1,
                          Object ruleTemplateObj2,
                          int andOrOr) {
        GroupRuleInfo ruleTemplate1 = (GroupRuleInfo) ruleTemplateObj1;
        GroupRuleInfo ruleTemplate2 = (GroupRuleInfo) ruleTemplateObj2;
        setName(ruleTemplate1.getName() + (andOrOr == AND ? " AND " : " OR ")
                + ruleTemplate2.getName());
        setDescription(ruleTemplate1.getDescription()
                + (andOrOr == AND ? " AND " : " OR ")
                + ruleTemplate2.getDescription());
        setSubject(ruleTemplate1.getSubject(),
                   ruleTemplate1.getSubject(),
                   andOrOr);
        setCondition(ruleTemplate1.getCondition(),
                     ruleTemplate2.getCondition(),
                     andOrOr);
        parms = null;
        accept = ruleTemplate1.accept;
        refcount = 0;
    }

    public static void buildFromRules(boolean accept,
                                      int rule1,
                                      int rule2,
                                      int andOrOr) {
        GroupRuleInfo tmp;
        if (accept) {
            tmp =
                    new GroupRuleInfo(permitRules.elementAt(rule1), permitRules
                            .elementAt(rule2), andOrOr);
            tmp.buildParms =
                    "permit,Combo," + (andOrOr == AND ? "and" : "or") + ","
                            + rule1 + "," + rule2;
            permitRules.addElement(tmp);
        } else {
            tmp =
                    new GroupRuleInfo(denyRules.elementAt(rule1), denyRules
                            .elementAt(rule2), andOrOr);
            tmp.buildParms =
                    "deny,Combo," + (andOrOr == AND ? "and" : "or") + ","
                            + rule1 + "," + rule2;
            denyRules.addElement(tmp);
        }
    }

    public static void buildFromRules(boolean accept, int[] rules, int andOrOr) {
        GroupRuleInfo tmp;
        if (accept) {
            tmp =
                    new GroupRuleInfo(permitRules.elementAt(rules[0]),
                                      permitRules.elementAt(rules[1]),
                                      andOrOr);
            String rule = rules[0] + "," + rules[1];
            for (int i = 2; i < rules.length; i++) {
                tmp =
                        new GroupRuleInfo(tmp,
                                          permitRules.elementAt(rules[i]),
                                          andOrOr);
                rule = rule + "," + rules[i];
            }
            tmp.buildParms =
                    "permit,Combo," + (andOrOr == AND ? "and" : "or") + ","
                            + rule;

            permitRules.addElement(tmp);
        } else {
            tmp =
                    new GroupRuleInfo(denyRules.elementAt(rules[0]), denyRules
                            .elementAt(rules[1]), andOrOr);
            String rule = rules[0] + "," + rules[1];
            for (int i = 2; i < rules.length; i++) {
                tmp =
                        new GroupRuleInfo(tmp,
                                          denyRules.elementAt(rules[i]),
                                          andOrOr);
                rule = rule + "," + rules[i];
            }
            tmp.buildParms =
                    "deny,Combo," + (andOrOr == AND ? "and" : "or") + ","
                            + rule;

            denyRules.addElement(tmp);
        }
    }

    public static void init() {
        permitTemplates = new Vector();
        denyTemplates = new Vector();
        permitRules = new Vector();
        denyRules = new Vector();

        permitTemplates
                .addElement(new GroupRuleInfo("unrestricted",
                                              "Allow unrestricted to resource to anyone",
                                              "<AnySubject/>",
                                              "",
                                              "",
                                              true));
        denyTemplates
                .addElement(new GroupRuleInfo("  -- none --",
                                              "No exception to the allow rules.",
                                              "",
                                              "",
                                              "",
                                              false));
        permitRules.addElement(new GroupRuleInfo(permitTemplates.elementAt(0),
                                                 null));
        denyRules
                .addElement(new GroupRuleInfo(denyTemplates.elementAt(0), null));

    }

    public static void defineDefaultRules() {
        buildFromTemplate(true,
                          1,
                          "machine_name=localhost;IP_Address_of_machine=127.0.0.1");
        buildFromTemplate(true,
                          2,
                          "User_parameter_name=fedoraRole;User_parameter_value=administrator");
        buildFromTemplate(true, 3, null);
        buildFromTemplate(true,
                          2,
                          "User_parameter_name=objectClass;User_parameter_value=uvaPerson");
        buildFromTemplate(true,
                          4,
                          "organization_name=virginia.edu;IP_address_range=128\\.143\\.\\d{1,3}\\.\\d{1,3}");

        buildFromRules(true, 2, 1, AND);
        buildFromRules(true, 3, 2, OR);

        buildFromTemplate(false,
                          1,
                          "machine_name=localhost;IP_Address_of_machine=127.0.0.1");
        buildFromTemplate(false,
                          2,
                          "User_parameter_name=fedoraRole;User_parameter_value=administrator");
        buildFromTemplate(false, 3, null);
        buildFromTemplate(false, 4, "object_state=Deleted");
        buildFromTemplate(false,
                          5,
                          "organization_name=virginia.edu;IP_address_range=128\\.143\\.\\d{1,3}\\.\\d{1,3}");

    }

    public static void writeRuleDefs(PrintWriter out) {
        //Note skip the first default rule for unrestricted
        for (int i = 1; i < permitRules.size(); i++) {
            GroupRuleInfo rule = (GroupRuleInfo) permitRules.elementAt(i);
            writeRuleDef(out, rule);
        }
        //Note skip the first default rule for no exception
        for (int i = 1; i < denyRules.size(); i++) {
            GroupRuleInfo rule = (GroupRuleInfo) denyRules.elementAt(i);
            writeRuleDef(out, rule);
        }

    }

    private static void writeRuleDef(PrintWriter out, GroupRuleInfo rule) {
        String buildParms = rule.buildParms;
        String parms = "";
        for (int j = 0; j < rule.getNumParms(); j++) {
            parms = parms + (j == 0 ? "" : ";") + rule.getParm(j);
        }
        if (rule.getNumParms() > 0) {
            out.println("    <ruledef buildparm=\"" + buildParms
                    + "\" parms=\"" + parms + "\" />");
        } else {
            out.println("    <ruledef buildparm=\"" + buildParms + "\"  />");
        }
    }

    public static GroupRuleInfo getEntry(boolean allowOrDeny, int num) {
        if (allowOrDeny) {
            if (num < 0 || num >= permitRules.size()) {
                return null;
            }
            return (GroupRuleInfo) permitRules.elementAt(num);
        } else {
            if (num < 0 || num >= denyRules.size()) {
                return null;
            }
            return (GroupRuleInfo) denyRules.elementAt(num);
        }
    }

    public static GroupRuleInfo findEntryByShortName(boolean allowOrDeny,
                                                     String value) {
        try {
            int intVal = Integer.parseInt(value);
            return getEntry(allowOrDeny, intVal - 2);
        } catch (NumberFormatException nfe) {
            // do nothing, fall through, be happy
        }
        if (allowOrDeny) {
            return findEntryByShortName(permitRules, value);
        } else {
            return findEntryByShortName(denyRules, value);
        }
    }

    public static GroupRuleInfo findEntryByShortName(Vector ruleSet,
                                                     String value) {
        for (int i = 0; i < ruleSet.size(); i++) {
            GroupRuleInfo group = (GroupRuleInfo) ruleSet.elementAt(i);
            if (group.getShortGroupname().equals(value)) {
                return group;
            }
        }
        return null;
    }

    public static int getNumRules(boolean allowOrDeny) {
        if (allowOrDeny) {
            return permitRules.size();
        } else {
            return denyRules.size();
        }
    }

    GroupRuleInfo getTemplateForRule() {
        String buildParmsArray[] = buildParms.split(",");
        if (!buildParmsArray[1].equals("Template")) {
            return null;
        }
        if (buildParmsArray[0].equals("permit")) {
            return (GroupRuleInfo) permitTemplates.elementAt(Integer
                    .parseInt(buildParmsArray[2]));
        } else if (buildParmsArray[0].equals("deny")) {
            return (GroupRuleInfo) denyTemplates.elementAt(Integer
                    .parseInt(buildParmsArray[2]));
        }
        return null;
    }

    /**
     * @return Returns the condition.
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition
     *        The condition to set.
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getExpandedCondition() {
        String expCondition = expandString(getCondition());
        return expCondition;
    }

    private String expandString(String input) {
        String output = input;
        for (int i = 0; i < getNumParms(); i++) {
            String parmpart[] = getParm(i).split("=");
            output = output.replaceAll("@" + parmpart[0] + "@", parmpart[1]);
        }
        return output;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *        The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the name.
     */
    @Override
    public String toString() {
        return name;
    }

    public String getShortGroupname() {
        return Utility.camelCasify(name);
    }

    /**
     * @param name
     *        The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the subject.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject
     *        The subject to set.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSubject(String subject1, String subject2, int andOrOr) {
        if (subject1.indexOf("AnySubject") != -1) {
            subject = subject2;
        } else if (subject2.indexOf("AnySubject") != -1) {
            subject = subject1;
        } else {
            subject = subject1 + subject2;
        }
    }

    public void setCondition(String condition1, String condition2, int andOrOr) {
        if (condition1.length() == 0) {
            condition = condition2;
        } else if (condition2.length() == 0) {
            condition = condition1;
        } else {
            String tmp1 = condition1.replaceAll("Condition", "Apply");
            String tmp2 = condition2.replaceAll("Condition", "Apply");
            condition =
                    "    <Condition FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:"
                            + (andOrOr == AND ? "and" : "or") + "\">\n" + tmp1
                            + "\n" + tmp2 + "\n    </Condition>";
        }

    }

    public String getExpandedSubject() {
        String expSubject = "<Subjects>\n" + getSubject() + "\n</Subjects>";
        return expSubject;
    }

    /**
     * @return Returns the parms.
     */
    public int getNumParms() {
        if (parms == null) {
            return 0;
        }
        return parms.size();
    }

    /**
     * @return Returns the parms.
     */
    public String getParm(int num) {
        return parms.elementAt(num).toString();
    }

    /**
     * @return Returns the parms.
     */
    public String getParmName(int num) {
        String parmpart[] = getParm(num).split("=");
        return parmpart[0];
    }

    /**
     * @return Returns the parms.
     */
    public String getParmValue(int num) {
        String parmpart[] = getParm(num).split("=");
        return parmpart[1];
    }

    /**
     * @param parmArray
     *        The parms to set.
     */
    public void setParms(String[] parmArray) {
        for (String element : parmArray) {
            if (element.length() != 0) {
                parms.add(element);
            }
        }
    }

    /**
     * @return Returns the Permit or Deny.
     */
    public String getEffect() {
        return accept ? "Permit" : "Deny";
    }

    public void addRef() {
        refcount++;
    }

    public void removeRef() {
        refcount--;
    }

    public int getRefCount() {
        return refcount;
    }

}
