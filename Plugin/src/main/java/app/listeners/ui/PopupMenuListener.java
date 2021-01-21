package app.listeners.ui;

import app.listeners.base.BaseListener;
import com.intellij.openapi.actionSystem.impl.ActionMenu;
import com.intellij.openapi.actionSystem.impl.ActionMenuItem;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import java.util.ArrayList;

public class PopupMenuListener extends BaseListener implements javax.swing.event.PopupMenuListener {

    private MenuActionListener actionListener;
    private ArrayList<MenuElement> knownElements;

    public PopupMenuListener() {
        actionListener = new MenuActionListener();
        knownElements = new ArrayList<MenuElement>();
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
        JPopupMenu menu = (JPopupMenu) popupMenuEvent.getSource();
        for(MenuElement element: menu.getSubElements()) {
            if(knownElements.contains(element)) {
                continue;
            }
            processMenuElement(element);
            knownElements.add(element);
        }
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {

    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {

    }

    public void processMenuElement(MenuElement element) {
        if(element instanceof ActionMenuItem) {
            ((ActionMenuItem) element).addActionListener(actionListener);
        } else if (element instanceof ActionMenu) {
            ActionMenu menu = (ActionMenu) element;
            for(MenuElement subElement: menu.getSubElements()) {
                processMenuElement(subElement);
            }
        }
    }
}
