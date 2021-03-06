package graphics;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class ChatGUI {

    private static final int LOG_SIZE = 10;

    public static void main(String[] args) {

        new ChatGUI();
    }

    private JFrame frame;
    private JPanel root;
    private JTextField messageBox;
    private JButton sendButton;
    private JLabel messageDisplay;
    private MessageHolder messages;
    private String lastMessage;

    public ChatGUI() {

        this.messages = new MessageHolder(LOG_SIZE);
        setFrame();
        setListeners();
        display();
        setVisible();
    }

    private void setListeners() {

        this.messageBox.registerKeyboardAction(this::send, KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_FOCUSED);

        this.sendButton.addActionListener(this::send);

        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                System.exit(0);
            }
        });
    }

    private void setFrame() {

        frame = new JFrame("Chat");
        frame.setContentPane(root);
        frame.setResizable(false);
        frame.pack();
    }

    private void setVisible() {

        frame.setVisible(true);
    }

    public void display(String message) {

        messages.add(message);
        display();
    }

    private void display() {

        messageDisplay.setText("<html>" + String.join("<br>", messages.getMessages()) + "</html>");
        frame.pack();
    }

    public String getMessage() {

        while (true) {
            try {
                synchronized (this) {
                    wait();
                    return lastMessage;
                }
            }

            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void send(ActionEvent e) {

        synchronized (this) {
            lastMessage = messageBox.getText();
            display(lastMessage);
            messageBox.setText("");
            notifyAll();
        }
    }

    private class MessageHolder {

        private String[] messages;
        private int mostRecent;

        MessageHolder(int size) {

            this.messages = new String[size];
            this.mostRecent = 0;

            Arrays.fill(this.messages, "");
        }

        String[] getMessages() {

            String[] output = new String[messages.length];
            for (int i = 0; i < messages.length; i++) {
                output[i] = messages[(i + mostRecent) % messages.length];
            }
            return output;
        }

        void add(String message) {

            messages[mostRecent] = message;
            advancePointer();
        }

        String getMostRecent() {

            return messages[mostRecent];
        }

        private void advancePointer() {

            mostRecent++;
            mostRecent %= messages.length;
        }
    }
}
