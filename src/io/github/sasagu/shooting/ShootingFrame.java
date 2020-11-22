package io.github.sasagu.shooting;

import javax.swing.JFrame;
import javax.swing.*;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ShootingFrame extends JFrame {
	
	private static final long serialVersionUID = 8305700499359793505L;
	public ShootingPanel panel;

    public ShootingFrame() {

        panel = new ShootingPanel();

        this.add(panel);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                Shooting.loop = !true;
            }
        });

        this.addKeyListener(new Keyboard());

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("SPACE Shooting");
        this.setIconImage(new ImageIcon("C:/Users/sasagu/Pictures/shoot-icon.png").getImage());
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

}
