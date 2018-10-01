/*
 * InputHandler.java - Manages key bindings and executes actions
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

package mars.venus.editors.jeditsyntax;

import javax.swing.text.*;
import javax.swing.JPopupMenu;
import java.awt.event.*;
import java.awt.Component;
import java.util.*;

/**
 * An input handler converts the user's key strokes into concrete actions.
 * It also takes care of macro recording and action repetition.<p>
 * <p>
 * This class provides all the necessary support code for an input
 * handler, but doesn't actually do any key binding logic. It is up
 * to the implementations of this class to do so.
 *
 * @author Slava Pestov
 * @version $Id: InputHandler.java,v 1.14 1999/12/13 03:40:30 sp Exp $
 * @see org.syntax.jedit.DefaultInputHandler
 * <p>
 * 08/12/2002	Clipboard actions	(Oliver Henning)
 */
public abstract class InputHandler extends KeyAdapter {
    /**
     * If this client property is set to Boolean.TRUE on the text area,
     * the home/end keys will support 'smart' BRIEF-like behaviour
     * (one press = start/end of line, two presses = start/end of
     * viewscreen, three presses = start/end of document). By default,
     * this property is not set.
     */
    private static final String SMART_HOME_END_PROPERTY = "InputHandler.homeEnd";

    static final ActionListener BACKSPACE = new backspace();
    static final ActionListener BACKSPACE_WORD = new backspace_word();
    static final ActionListener DELETE = new delete();
    static final ActionListener DELETE_WORD = new delete_word();
    static final ActionListener END = new end(false);
    static final ActionListener DOCUMENT_END = new document_end(false);
    static final ActionListener SELECT_ALL = new select_all();
    static final ActionListener SELECT_END = new end(true);
    static final ActionListener SELECT_DOC_END = new document_end(true);
    static final ActionListener INSERT_BREAK = new insert_break();
    static final ActionListener INSERT_TAB = new insert_tab();
    static final ActionListener HOME = new home(false);
    static final ActionListener DOCUMENT_HOME = new document_home(false);
    static final ActionListener SELECT_HOME = new home(true);
    static final ActionListener SELECT_DOC_HOME = new document_home(true);
    static final ActionListener NEXT_CHAR = new next_char(false);
    static final ActionListener NEXT_LINE = new next_line(false);
    static final ActionListener NEXT_PAGE = new next_page(false);
    static final ActionListener NEXT_WORD = new next_word(false);
    static final ActionListener SELECT_NEXT_CHAR = new next_char(true);
    static final ActionListener SELECT_NEXT_LINE = new next_line(true);
    static final ActionListener SELECT_NEXT_PAGE = new next_page(true);
    static final ActionListener SELECT_NEXT_WORD = new next_word(true);
    static final ActionListener OVERWRITE = new overwrite();
    static final ActionListener PREV_CHAR = new prev_char(false);
    static final ActionListener PREV_LINE = new prev_line(false);
    static final ActionListener PREV_PAGE = new prev_page(false);
    static final ActionListener PREV_WORD = new prev_word(false);
    static final ActionListener SELECT_PREV_CHAR = new prev_char(true);
    static final ActionListener SELECT_PREV_LINE = new prev_line(true);
    static final ActionListener SELECT_PREV_PAGE = new prev_page(true);
    static final ActionListener SELECT_PREV_WORD = new prev_word(true);
    static final ActionListener REPEAT = new repeat();
    static final ActionListener TOGGLE_RECT = new toggle_rect();
    // Clipboard
    static final ActionListener CLIP_COPY = new clip_copy();
    static final ActionListener CLIP_PASTE = new clip_paste();
    static final ActionListener CLIP_CUT = new clip_cut();

    // Default action
    static final ActionListener INSERT_CHAR = new insert_char();

    private static final Hashtable actions;

    static {
        actions = new Hashtable();
        actions.put("backspace", BACKSPACE);
        actions.put("backspace-word", BACKSPACE_WORD);
        actions.put("delete", DELETE);
        actions.put("delete-word", DELETE_WORD);
        actions.put("end", END);
        actions.put("select-all", SELECT_ALL);
        actions.put("select-end", SELECT_END);
        actions.put("document-end", DOCUMENT_END);
        actions.put("select-doc-end", SELECT_DOC_END);
        actions.put("insert-break", INSERT_BREAK);
        actions.put("insert-tab", INSERT_TAB);
        actions.put("home", HOME);
        actions.put("select-home", SELECT_HOME);
        actions.put("document-home", DOCUMENT_HOME);
        actions.put("select-doc-home", SELECT_DOC_HOME);
        actions.put("next-char", NEXT_CHAR);
        actions.put("next-line", NEXT_LINE);
        actions.put("next-page", NEXT_PAGE);
        actions.put("next-word", NEXT_WORD);
        actions.put("select-next-char", SELECT_NEXT_CHAR);
        actions.put("select-next-line", SELECT_NEXT_LINE);
        actions.put("select-next-page", SELECT_NEXT_PAGE);
        actions.put("select-next-word", SELECT_NEXT_WORD);
        actions.put("overwrite", OVERWRITE);
        actions.put("prev-char", PREV_CHAR);
        actions.put("prev-line", PREV_LINE);
        actions.put("prev-page", PREV_PAGE);
        actions.put("prev-word", PREV_WORD);
        actions.put("select-prev-char", SELECT_PREV_CHAR);
        actions.put("select-prev-line", SELECT_PREV_LINE);
        actions.put("select-prev-page", SELECT_PREV_PAGE);
        actions.put("select-prev-word", SELECT_PREV_WORD);
        actions.put("repeat", REPEAT);
        actions.put("toggle-rect", TOGGLE_RECT);
        actions.put("insert-char", INSERT_CHAR);
        actions.put("clipboard-copy", CLIP_COPY);
        actions.put("clipboard-paste", CLIP_PASTE);
        actions.put("clipboard-cut", CLIP_CUT);
    }

    /**
     * Returns a named text area action.
     *
     * @param name The action name
     */
    private static ActionListener getAction(String name) {
        return (ActionListener) actions.get(name);
    }

    /**
     * Returns the name of the specified text area action.
     *
     * @param listener The action
     */
    public static String getActionName(ActionListener listener) {
        Enumeration enumeration = getActions();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            ActionListener _listener = getAction(name);
            if (_listener == listener)
                return name;
        }
        return null;
    }

    /**
     * Returns an enumeration of all available actions.
     */
    private static Enumeration getActions() {
        return actions.keys();
    }

    /**
     * Adds the default key bindings to this input handler.
     * This should not be called in the constructor of this
     * input handler, because applications might load the
     * key bindings from a file, etc.
     */
    public abstract void addDefaultKeyBindings();

    /**
     * Adds a key binding to this input handler.
     *
     * @param keyBinding The key binding (the format of this is
     *                   input-handler specific)
     * @param action     The action
     */
    public abstract void addKeyBinding(String keyBinding, ActionListener action);

    /**
     * Removes a key binding from this input handler.
     *
     */
    public abstract void removeKeyBinding();

    /**
     * Removes all key bindings from this input handler.
     */
    public abstract void removeAllKeyBindings();

    /**
     * Grabs the next key typed event and invokes the specified
     * action with the key as a the action command.
     *
     */
    public void grabNextKeyStroke(ActionListener listener) {
        grabAction = listener;
    }

    /**
     * Returns if repeating is enabled. When repeating is enabled,
     * actions will be executed multiple times. This is usually
     * invoked with a special key stroke in the input handler.
     */
    public boolean isRepeatEnabled() {
        return repeat;
    }

    /**
     * Enables repeating. When repeating is enabled, actions will be
     * executed multiple times. Once repeating is enabled, the input
     * handler should read a number from the keyboard.
     */
    private void setRepeatEnabled() {
        this.repeat = true;
    }

    /**
     * Returns the number of times the next action will be repeated.
     */
    public int getRepeatCount() {
        return (repeat ? Math.max(1, repeatCount) : 1);
    }

    /**
     * Sets the number of times the next action will be repeated.
     *
     * @param repeatCount The repeat count
     */
    private void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    /**
     * Returns the macro recorder. If this is non-null, all executed
     * actions should be forwarded to the recorder.
     */
    public InputHandler.MacroRecorder getMacroRecorder() {
        return recorder;
    }

    /**
     * Sets the macro recorder. If this is non-null, all executed
     * actions should be forwarded to the recorder.
     *
     * @param recorder The macro recorder
     */
    public void setMacroRecorder(InputHandler.MacroRecorder recorder) {
        this.recorder = recorder;
    }

    /**
     * Returns a copy of this input handler that shares the same
     * key bindings. Setting key bindings in the copy will also
     * set them in the original.
     */
    public abstract InputHandler copy();

    /**
     * Executes the specified action, repeating and recording it as
     * necessary.
     *
     * @param listener      The action listener
     * @param source        The event source
     * @param actionCommand The action command
     */
    void executeAction(ActionListener listener, Object source,
                       String actionCommand) {
        // create event
        ActionEvent evt = new ActionEvent(source,
                ActionEvent.ACTION_PERFORMED,
                actionCommand);

        // don't do anything if the action is a wrapper
        // (like EditAction.Wrapper)
        if (listener instanceof Wrapper) {
            listener.actionPerformed(evt);
            return;
        }

        // remember old values, in case action changes them
        boolean _repeat = repeat;
        int _repeatCount = getRepeatCount();

        // execute the action
        if (listener instanceof InputHandler.NonRepeatable) {
            listener.actionPerformed(evt);
        } else {
            for (int i = 0; i < Math.max(1, repeatCount); i++)
                listener.actionPerformed(evt);
        }

        // do recording. Notice that we do no recording whatsoever
        // for actions that grab keys
        if (grabAction == null) {
            if (recorder != null) {
                if (!(listener instanceof InputHandler.NonRecordable)) {
                    if (_repeatCount != 1)
                        recorder.actionPerformed(REPEAT, String.valueOf(_repeatCount));

                    recorder.actionPerformed(listener, actionCommand);
                }
            }

            // If repeat was true originally, clear it
            // Otherwise it might have been set by the action, etc
            if (_repeat) {
                repeat = false;
                repeatCount = 0;
            }
        }
    }

    /**
     * Returns the text area that fired the specified event.
     *
     * @param evt The event
     */
    private static JEditTextArea getTextArea(EventObject evt) {
        if (evt != null) {
            Object o = evt.getSource();
            if (o instanceof Component) {
                // find the parent text area
                Component c = (Component) o;
                for (; ; ) {
                    if (c instanceof JEditTextArea)
                        return (JEditTextArea) c;
                    else if (c == null)
                        break;
                    if (c instanceof JPopupMenu)
                        c = ((JPopupMenu) c)
                                .getInvoker();
                    else
                        c = c.getParent();
                }
            }
        }

        // this shouldn't happen
        System.err.println("BUG: getTextArea() returning null");
        System.err.println("Report this to Slava Pestov <sp@gjt.org>");
        return null;
    }


    // protected members

    /**
     * If a key is being grabbed, this method should be called with
     * the appropriate key event. It executes the grab action with
     * the typed character as the parameter.
     */
    void handleGrabAction(KeyEvent evt) {
        // Clear it *before* it is executed so that executeAction()
        // resets the repeat count
        ActionListener _grabAction = grabAction;
        grabAction = null;
        executeAction(_grabAction, evt.getSource(),
                String.valueOf(evt.getKeyChar()));
    }

    // protected members
    ActionListener grabAction;
    boolean repeat;
    int repeatCount;
    private InputHandler.MacroRecorder recorder;

    /**
     * If an action implements this interface, it should not be repeated.
     * Instead, it will handle the repetition itself.
     */
    private interface NonRepeatable {
    }

    /**
     * If an action implements this interface, it should not be recorded
     * by the macro recorder. Instead, it will do its own recording.
     */
    private interface NonRecordable {
    }

    /**
     * For use by EditAction.Wrapper only.
     *
     * @since jEdit 2.2final
     */
    private interface Wrapper {
    }

    /**
     * Macro recorder.
     */
    public interface MacroRecorder {
        void actionPerformed(ActionListener listener,
                             String actionCommand);
    }

    static class backspace implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            if (!Objects.requireNonNull(textArea).isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            if (textArea.getSelectionStart()
                    != textArea.getSelectionEnd()) {
                textArea.setSelectedText("");
            } else {
                int caret = textArea.getCaretPosition();
                if (caret == 0) {
                    textArea.getToolkit().beep();
                    return;
                }
                try {
                    textArea.getDocument().remove(caret - 1, 1);
                } catch (BadLocationException bl) {
                    bl.printStackTrace();
                }
            }
        }
    }

    static class backspace_word implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int start = Objects.requireNonNull(textArea).getSelectionStart();
            if (start != textArea.getSelectionEnd()) {
                textArea.setSelectedText("");
            }

            int line = textArea.getCaretLine();
            int lineStart = textArea.getLineStartOffset(line);
            int caret = start - lineStart;

            String lineText = textArea.getLineText(textArea
                    .getCaretLine());

            if (caret == 0) {
                if (lineStart == 0) {
                    textArea.getToolkit().beep();
                    return;
                }
                caret--;
            } else {
                String noWordSep = (String) textArea.getDocument().getProperty("noWordSep");
                caret = TextUtilities.findWordStart(lineText, caret, noWordSep);
            }

            try {
                textArea.getDocument().remove(
                        caret + lineStart,
                        start - (caret + lineStart));
            } catch (BadLocationException bl) {
                bl.printStackTrace();
            }
        }
    }

    static class delete implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            if (!Objects.requireNonNull(textArea).isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            if (textArea.getSelectionStart()
                    != textArea.getSelectionEnd()) {
                textArea.setSelectedText("");
            } else {
                int caret = textArea.getCaretPosition();
                if (caret == textArea.getDocumentLength()) {
                    textArea.getToolkit().beep();
                    return;
                }
                try {
                    textArea.getDocument().remove(caret, 1);
                } catch (BadLocationException bl) {
                    bl.printStackTrace();
                }
            }
        }
    }

    static class delete_word implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int start = Objects.requireNonNull(textArea).getSelectionStart();
            if (start != textArea.getSelectionEnd()) {
                textArea.setSelectedText("");
            }

            int line = textArea.getCaretLine();
            int lineStart = textArea.getLineStartOffset(line);
            int caret = start - lineStart;

            String lineText = textArea.getLineText(textArea
                    .getCaretLine());

            if (caret == lineText.length()) {
                if (lineStart + caret == textArea.getDocumentLength()) {
                    textArea.getToolkit().beep();
                    return;
                }
                caret++;
            } else {
                String noWordSep = (String) textArea.getDocument().getProperty("noWordSep");
                caret = TextUtilities.findWordEnd(lineText, caret, noWordSep);
            }

            try {
                textArea.getDocument().remove(start,
                        (caret + lineStart) - start);
            } catch (BadLocationException bl) {
                bl.printStackTrace();
            }
        }
    }

    static class end implements ActionListener {
        private final boolean select;

        end(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            int caret = Objects.requireNonNull(textArea).getCaretPosition();

            int lastOfLine = textArea.getLineEndOffset(
                    textArea.getCaretLine()) - 1;
            int lastVisibleLine = textArea.getFirstLine()
                    + textArea.getVisibleLines();
            if (lastVisibleLine >= textArea.getLineCount()) {
                lastVisibleLine = Math.min(textArea.getLineCount() - 1,
                        lastVisibleLine);
            } else
                lastVisibleLine -= (textArea.getElectricScroll() + 1);

            int lastVisible = textArea.getLineEndOffset(lastVisibleLine) - 1;
            int lastDocument = textArea.getDocumentLength();

            if (caret == lastDocument) {
                textArea.getToolkit().beep();
                return;
            } else if (!Boolean.TRUE.equals(textArea.getClientProperty(
                    SMART_HOME_END_PROPERTY)))
                caret = lastOfLine;
            else if (caret == lastVisible)
                caret = lastDocument;
            else if (caret == lastOfLine)
                caret = lastVisible;
            else
                caret = lastOfLine;

            if (select)
                textArea.select(textArea.getMarkPosition(), caret);
            else
                textArea.setCaretPosition(caret);
        }
    }

    static class select_all implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            Objects.requireNonNull(textArea).selectAll();
        }
    }

    static class document_end implements ActionListener {
        private final boolean select;

        document_end(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (select)
                textArea.select(Objects.requireNonNull(textArea).getMarkPosition(),
                        textArea.getDocumentLength());
            else
                textArea.setCaretPosition(Objects.requireNonNull(textArea)
                        .getDocumentLength());
        }
    }

    static class home implements ActionListener {
        private final boolean select;

        home(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            int caret = Objects.requireNonNull(textArea).getCaretPosition();

            int firstLine = textArea.getFirstLine();

            int firstOfLine = textArea.getLineStartOffset(
                    textArea.getCaretLine());
            int firstVisibleLine = (firstLine == 0 ? 0 :
                    firstLine + textArea.getElectricScroll());
            int firstVisible = textArea.getLineStartOffset(
                    firstVisibleLine);

            if (caret == 0) {
                textArea.getToolkit().beep();
                return;
            } else if (!Boolean.TRUE.equals(textArea.getClientProperty(
                    SMART_HOME_END_PROPERTY)))
                caret = firstOfLine;
            else if (caret == firstVisible)
                caret = 0;
            else if (caret == firstOfLine)
                caret = firstVisible;
            else
                caret = firstOfLine;

            if (select)
                textArea.select(textArea.getMarkPosition(), caret);
            else
                textArea.setCaretPosition(caret);
        }
    }

    static class document_home implements ActionListener {
        private final boolean select;

        document_home(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (select)
                textArea.select(Objects.requireNonNull(textArea).getMarkPosition(), 0);
            else
                Objects.requireNonNull(textArea).setCaretPosition(0);
        }
    }

    static class insert_break implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            if (!Objects.requireNonNull(textArea).isEditable()) {
                textArea.getToolkit().beep();
                return;
            }
            // AutoIndent feature added DPS 31-Dec-2010
            textArea.setSelectedText("\n" + textArea.getAutoIndent());
        }
    }

    static class insert_tab implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            if (!Objects.requireNonNull(textArea).isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            textArea.overwriteSetSelectedText("\t");
        }
    }

    static class next_char implements ActionListener {
        private final boolean select;

        next_char(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int caret = Objects.requireNonNull(textArea).getCaretPosition();
            if (caret == textArea.getDocumentLength()) {
                textArea.getToolkit().beep();
                return;
            }

            if (select)
                textArea.select(textArea.getMarkPosition(),
                        caret + 1);
            else
                textArea.setCaretPosition(caret + 1);
        }
    }

    static class next_line implements ActionListener {
        private final boolean select;

        next_line(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int caret = Objects.requireNonNull(textArea).getCaretPosition();
            int line = textArea.getCaretLine();

            if (line == textArea.getLineCount() - 1) {
                textArea.getToolkit().beep();
                return;
            }

            int magic = textArea.getMagicCaretPosition();
            if (magic == -1) {
                magic = textArea.offsetToX(line,
                        caret - textArea.getLineStartOffset(line));
            }

            caret = textArea.getLineStartOffset(line + 1)
                    + textArea.xToOffset(line + 1, magic);
            if (select)
                textArea.select(textArea.getMarkPosition(), caret);
            else
                textArea.setCaretPosition(caret);
            textArea.setMagicCaretPosition(magic);
        }
    }

    static class next_page implements ActionListener {
        private final boolean select;

        next_page(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int lineCount = Objects.requireNonNull(textArea).getLineCount();
            int firstLine = textArea.getFirstLine();
            int visibleLines = textArea.getVisibleLines();
            int line = textArea.getCaretLine();

            firstLine += visibleLines;

            if (firstLine + visibleLines >= lineCount - 1)
                firstLine = lineCount - visibleLines;

            textArea.setFirstLine(firstLine);

            int caret = textArea.getLineStartOffset(
                    Math.min(textArea.getLineCount() - 1,
                            line + visibleLines));
            if (select)
                textArea.select(textArea.getMarkPosition(), caret);
            else
                textArea.setCaretPosition(caret);
        }
    }

    static class next_word implements ActionListener {
        private final boolean select;

        next_word(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int caret = Objects.requireNonNull(textArea).getCaretPosition();
            int line = textArea.getCaretLine();
            int lineStart = textArea.getLineStartOffset(line);
            caret -= lineStart;

            String lineText = textArea.getLineText(textArea
                    .getCaretLine());

            if (caret == lineText.length()) {
                if (lineStart + caret == textArea.getDocumentLength()) {
                    textArea.getToolkit().beep();
                    return;
                }
                caret++;
            } else {
                String noWordSep = (String) textArea.getDocument().getProperty("noWordSep");
                caret = TextUtilities.findWordEnd(lineText, caret, noWordSep);
            }

            if (select)
                textArea.select(textArea.getMarkPosition(),
                        lineStart + caret);
            else
                textArea.setCaretPosition(lineStart + caret);
        }
    }

    static class overwrite implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setOverwriteEnabled(
                    !Objects.requireNonNull(textArea).isOverwriteEnabled());
        }
    }

    static class prev_char implements ActionListener {
        private final boolean select;

        prev_char(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int caret = Objects.requireNonNull(textArea).getCaretPosition();
            if (caret == 0) {
                textArea.getToolkit().beep();
                return;
            }

            if (select)
                textArea.select(textArea.getMarkPosition(),
                        caret - 1);
            else
                textArea.setCaretPosition(caret - 1);
        }
    }

    static class prev_line implements ActionListener {
        private final boolean select;

        prev_line(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int caret = Objects.requireNonNull(textArea).getCaretPosition();
            int line = textArea.getCaretLine();

            if (line == 0) {
                textArea.getToolkit().beep();
                return;
            }

            int magic = textArea.getMagicCaretPosition();
            if (magic == -1) {
                magic = textArea.offsetToX(line,
                        caret - textArea.getLineStartOffset(line));
            }

            caret = textArea.getLineStartOffset(line - 1)
                    + textArea.xToOffset(line - 1, magic);
            if (select)
                textArea.select(textArea.getMarkPosition(), caret);
            else
                textArea.setCaretPosition(caret);
            textArea.setMagicCaretPosition(magic);
        }
    }

    static class prev_page implements ActionListener {
        private final boolean select;

        prev_page(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int firstLine = Objects.requireNonNull(textArea).getFirstLine();
            int visibleLines = textArea.getVisibleLines();
            int line = textArea.getCaretLine();

            if (firstLine < visibleLines)
                firstLine = visibleLines;

            textArea.setFirstLine(firstLine - visibleLines);

            int caret = textArea.getLineStartOffset(
                    Math.max(0, line - visibleLines));
            if (select)
                textArea.select(textArea.getMarkPosition(), caret);
            else
                textArea.setCaretPosition(caret);
        }
    }

    static class prev_word implements ActionListener {
        private final boolean select;

        prev_word(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int caret = Objects.requireNonNull(textArea).getCaretPosition();
            int line = textArea.getCaretLine();
            int lineStart = textArea.getLineStartOffset(line);
            caret -= lineStart;

            String lineText = textArea.getLineText(textArea
                    .getCaretLine());

            if (caret == 0) {
                if (lineStart == 0) {
                    textArea.getToolkit().beep();
                    return;
                }
                caret--;
            } else {
                String noWordSep = (String) textArea.getDocument().getProperty("noWordSep");
                caret = TextUtilities.findWordStart(lineText, caret, noWordSep);
            }

            if (select)
                textArea.select(textArea.getMarkPosition(),
                        lineStart + caret);
            else
                textArea.setCaretPosition(lineStart + caret);
        }
    }

    static class repeat implements ActionListener,
            InputHandler.NonRecordable {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            Objects.requireNonNull(textArea).getInputHandler().setRepeatEnabled();
            String actionCommand = evt.getActionCommand();
            if (actionCommand != null) {
                textArea.getInputHandler().setRepeatCount(
                        Integer.parseInt(actionCommand));
            }
        }
    }

    static class toggle_rect implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setSelectionRectangular(
                    !Objects.requireNonNull(textArea).isSelectionRectangular());
        }
    }

    static class insert_char implements ActionListener,
            InputHandler.NonRepeatable {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            String str = evt.getActionCommand();
            int repeatCount = Objects.requireNonNull(textArea).getInputHandler().getRepeatCount();

            if (textArea.isEditable()) {
                StringBuilder buf = new StringBuilder();
                for (int i = 0; i < repeatCount; i++)
                    buf.append(str);
                textArea.overwriteSetSelectedText(buf.toString());
            } else {
                textArea.getToolkit().beep();
            }
        }
    }

    static class clip_copy implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            Objects.requireNonNull(textArea).copy();
        }
    }

    static class clip_paste implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            Objects.requireNonNull(textArea).paste();
        }
    }

    static class clip_cut implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            Objects.requireNonNull(textArea).cut();
        }
    }
}
