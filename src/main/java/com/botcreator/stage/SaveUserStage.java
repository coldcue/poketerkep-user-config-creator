package com.botcreator.stage;

import com.botcreator.Main;
import com.botcreator.WebRegisterInstance;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.logging.Logger;

public class SaveUserStage extends StageImpl {
    private static final String blowfish = "csicskacsalo";
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public SaveUserStage(WebRegisterInstance wri) {
        super(wri);
    }

    @Override
    public void run() throws IOException {
        log.info("Saving user...");

        File usersFile = new File("users.csv");

        if (!usersFile.exists()) {
            boolean newFile = usersFile.createNewFile();
            if (!newFile) System.exit(0);
        }

        FileWriter fileWriter = new FileWriter(usersFile, true);
        PrintWriter out = new PrintWriter(fileWriter, true);
        CSVPrinter csvPrinter = new CSVPrinter(out, CSVFormat.DEFAULT);

        String username = wri.getUsername();
        String time = Long.toString(Instant.now().toEpochMilli());
        String hash = DigestUtils.md5Hex(username + ":" + time + ":" + blowfish);
        String workerName = Main.workerName;

        csvPrinter.printRecord(username, time, workerName, hash);

        csvPrinter.flush();
        csvPrinter.close();
        log.info("User saved!");

    }
}
