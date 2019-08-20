import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends Frame implements ActionListener, Mediator {
    private ColleagueButton buttonOk;
    private ColleagueButton buttonCancel;

    private ColleagueRadiobox radioGuest;
    private ColleagueRadiobox radioLogin;

    private ColleagueTextField textId;
    private ColleagueTextField textPw;

    public LoginFrame(String title) {
        super(title);
        setBackground(Color.lightGray);
        setLayout(new GridLayout(4, 2));
        createColleagues();
        add(radioGuest);
        add(radioLogin);
        add(new Label("Username: "));
        add(textId);
        add(new Label("Password: "));
        add(textPw);
        add(buttonOk);
        add(buttonCancel);
        colleagueChanged();
        pack();
        show();
    }

    public void createColleagues() {
        CheckboxGroup g = new CheckboxGroup();
        radioGuest = new ColleagueRadiobox("Guest", true, g);
        radioLogin = new ColleagueRadiobox("Login", false, g); 

        textId = new ColleagueTextField("", 10);
        textPw = new ColleagueTextField("", 10);
        textPw.setEchoChar('*');

        buttonOk = new ColleagueButton("OK");
        buttonCancel = new ColleagueButton("Cancel");

        radioGuest.setMediator(this);
        radioLogin.setMediator(this);
        textId.setMediator(this);
        textPw.setMediator(this);
        buttonOk.setMediator(this);
        buttonCancel.setMediator(this);

        radioGuest.addItemListener(radioGuest);
        radioLogin.addItemListener(radioLogin);
        textId.addTextListener(textId);
        textPw.addTextListener(textPw);
        buttonOk.addActionListener(this);
        buttonCancel.addActionListener(this);
    }

    public void colleagueChanged() {
        if(radioGuest.getState()) {
            textId.setColleagueEnabled(false);
            textPw.setColleagueEnabled(false);
            buttonOk.setColleagueEnabled(true);
        }
        else {
            textId.setColleagueEnabled(true);
            userpassChanged();
        }
    }

    private void userpassChanged() {
        if(textId.getText().length() > 0) {
            textPw.setColleagueEnabled(true);
            if(textPw.getText().length() > 0) {
                buttonOk.setColleagueEnabled(true);
            }
            else {
                buttonOk.setColleagueEnabled(false);
            }
        }
        else {
            textPw.setColleagueEnabled(false);
            buttonOk.setColleagueEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println(e);
        System.exit(0);
    }
}