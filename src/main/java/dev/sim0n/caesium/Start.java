package dev.sim0n.caesium;

import be.ninedocteur.caesium.cli.CaesiumCli;
import dev.sim0n.caesium.gui.CGui;

import java.io.IOException;

public class Start {
    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            CaesiumCli.main(args);
            return;
        }

        try {
            CGui.main(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
