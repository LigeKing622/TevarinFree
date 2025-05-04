package dev.tevarin.command.commands;

import dev.tevarin.command.Command;

import java.util.ArrayList;
import java.util.List;

public class IRCCommand extends Command {
    public IRCCommand() {
        super("irc");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }

    @Override

    public void run(String[] args, String originalMessag) {
    }
}