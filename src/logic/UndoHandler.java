package logic;

import java.util.ArrayList;
import java.util.Arrays;

import application.Task;

class UndoHandler extends UndoableCommandHandler {

    private ArrayList<String> aliases = new ArrayList<String>(
            Arrays.asList("undo", "u"));

    @Override
    protected ArrayList<String> getAliases() {
        // TODO Auto-generated method stub
        return aliases;
    }

    @Override
    protected String execute(String command, String parameter) {
        String[] token = parameter.split(" ");
        if (token[0].toLowerCase().equals("help")) {
            return getHelp();
        }

        if (undo.empty()) {
            return "Nothing to undo\n";
        }
        else {
            undo.pop().undo();
            updateTaskList();
            return "Revoked latest change\n";
        }

    }

    @Override
    public String getHelp() {
        // TODO Auto-generated method stub
        return "undo\n\t revoke latest change";
    }

    @Override
    void undo() {
    }

    /**
     * update the taskList in CommandHandler
     */
    private void updateTaskList() {
        taskList.clear();
        taskList.addAll(0, memory.getTaskList());
    }

}
