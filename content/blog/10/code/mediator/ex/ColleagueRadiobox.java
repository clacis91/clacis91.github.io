import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ColleagueRadiobox extends Checkbox implements Colleague, ItemListener {
    private Mediator mediator;

    public ColleagueRadiobox(String label, boolean state, CheckboxGroup group) {
        super(label, state, group);
    }
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
    public void setColleagueEnabled(boolean enabled) {
        setEnabled(enabled);
    }
    public void itemStateChanged(ItemEvent e) {
        mediator.colleagueChanged();
    }
}