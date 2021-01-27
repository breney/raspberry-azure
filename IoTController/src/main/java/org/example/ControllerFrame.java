package org.example;

import javax.swing.*;

public class ControllerFrame extends JFrame {
    private JPanel JPanelMain;
    private JTextArea textArea1;
    private JSlider slider1;
    private JButton alterarButton;
    private JButton ativarButton;

    public ControllerFrame (String title){



        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(JPanelMain);
        this.pack();
        slider1.setMajorTickSpacing(50);
        slider1.setMinorTickSpacing(10);
        slider1.setPaintTicks(true);
        slider1.setPaintLabels(true);
    }

    public static void main(String[] args) {
        ControllerFrame frame = new ControllerFrame("Controller frame");
        frame.setVisible(true);
    }
}
