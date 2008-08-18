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

package fedora.client.console;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.axis.types.NonNegativeInteger;

/**
 * @author Chris Wilper
 * @author Ross Wayland
 */
public class NonNegativeIntegerInputPanel
        extends InputPanel {

    private static final long serialVersionUID = 1L;

    private final JRadioButton m_nullRadioButton;

    private final JTextField m_textField;

    public NonNegativeIntegerInputPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel nullPanel = new JPanel();
        nullPanel.setLayout(new BorderLayout());
        m_nullRadioButton = new JRadioButton("Use null");
        nullPanel.add(m_nullRadioButton, BorderLayout.WEST);
        add(nullPanel);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
        JRadioButton textRadioButton = new JRadioButton("Use text: ");
        textRadioButton.setSelected(true);
        textPanel.add(textRadioButton);
        m_textField = new JTextField(10);
        textPanel.add(m_textField);
        add(textPanel);

        ButtonGroup g = new ButtonGroup();
        g.add(m_nullRadioButton);
        g.add(textRadioButton);

    }

    @Override
    public Object getValue() {
        if (m_nullRadioButton.isSelected()) {
            return null;
        } else {
            return new NonNegativeInteger(m_textField.getText());
        }
    }

}
