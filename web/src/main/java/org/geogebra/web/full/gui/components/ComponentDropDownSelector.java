package org.geogebra.web.full.gui.components;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;

import java.util.ArrayList;
import java.util.List;

public class ComponentDropDownSelector extends FlowPanel {

    private Label titleLabel;
    private Label selectedOptionLabel;
    private GPopupMenuW dropDownMenu;
    private List<AriaMenuItem> dropDownElementsList;
    private DropDownSelectionListener selectionListener;

    public ComponentDropDownSelector(AppW app) {
        buildGui(app);
    }

    private void buildGui(AppW app) {
        FlowPanel contentPanel = new FlowPanel();
        contentPanel.setStyleName("dropDownSelector");

        createTitleLabel();
        createSelectedOptionLabel();
        createDropDownMenu(app);

        contentPanel.add(titleLabel);
        contentPanel.add(selectedOptionLabel);

        add(contentPanel);
    }

    private void createTitleLabel() {
        titleLabel = new Label();

        titleLabel.setStyleName("titleLabel");
    }

    private void createSelectedOptionLabel() {
        selectedOptionLabel = new Label();

        selectedOptionLabel.setText("Hungarian");
        selectedOptionLabel.setStyleName("selectedOptionLabel");
    }

    private void createDropDownMenu(AppW app) {
        dropDownMenu = new GPopupMenuW(app);
        dropDownMenu.getPopupPanel().addStyleName("matMenu");
        dropDownMenu.getPopupPanel().addStyleName("dropDownPopup");

        ClickStartHandler.init(this, new ClickStartHandler(true, true) {

            @Override
            public void onClickStart(int x, int y, PointerEventType type) {
                dropDownMenu.showAtPoint(getAbsoluteLeft() + 16, getAbsoluteTop() - 64);
            }
        });
    }

    private void setupDropDownMenu(List<AriaMenuItem> menuItems) {
        for (AriaMenuItem menuItem : menuItems) {
            dropDownMenu.addItem(menuItem);
        }
    }

    public void setTitleText(String title) {
        titleLabel.setText(title);
    }

    public void setSelected(int selected) {
        selectedOptionLabel.setText(dropDownElementsList.get(selected).getElement().getInnerText());
    }

    public void setElements(final List<String> dropDownList) {
        dropDownElementsList = new ArrayList<>();

        for (int i = 0; i < dropDownList.size(); ++i) {
            final int currentIndex = i;
            AriaMenuItem item = new AriaMenuItem(
                    MainMenu.getMenuBarHtmlNoIcon(dropDownList.get(i)), true,
                    new Command() {
                        @Override
                        public void execute() {
                            setSelected(currentIndex);
                            if (selectionListener != null) {
                                selectionListener.onSelectionChanged(currentIndex);
                            }
                        }
                    });

            item.setStyleName("dropDownElement");
            dropDownElementsList.add(item);
        }
        setupDropDownMenu(dropDownElementsList);
    }

    public void setDropDownSelectionListener(DropDownSelectionListener listener) {
        selectionListener = listener;
    }

    public interface DropDownSelectionListener {
        void onSelectionChanged(int index);
    }
}