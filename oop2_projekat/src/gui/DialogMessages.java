package gui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Button;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogMessages extends Dialog {
	
	public DialogMessages(Frame parent, String title, String message,boolean modal){
		super(parent,title,modal);
		
		setLayout(new BorderLayout());

        Label msgLabel = new Label(message);
        add(msgLabel, BorderLayout.CENTER);

        Button okButton = new Button("OK");
        okButton.addActionListener(e -> dispose());
        add(okButton, BorderLayout.SOUTH);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(300, 100);
        setLocationRelativeTo(parent);
        setVisible(true);
		
	}

}
