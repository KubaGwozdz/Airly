import main.CmdLineParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Created by kuba on 24.01.2018
 */


public class AirlyTest {

    @Test
    void cmdLineTest() {
        String line = "mogilany leśna 3 -h";
        String[] args = line.split(" ");
        CmdLineParser cmdLine = new CmdLineParser();
        try {
            cmdLine.parse(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cmdLine.getJsonParser();
        assertEquals(cmdLine.isLast24H(), true);

    }



}