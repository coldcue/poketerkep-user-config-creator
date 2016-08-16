package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;

public class SaveUserStage extends StageImpl {
    public SaveUserStage(WebRegisterInstance wri) {
        super(wri);
    }

    @Override
    public void run() throws Exception {
        System.out.println("Saving user...");

    }
}
